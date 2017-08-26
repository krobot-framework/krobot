/*
 * Copyright 2017 The Krobot Contributors
 *
 * This file is part of Krobot.
 *
 * Krobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krobot.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.krobot.runtime;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krobot.Bot;
import org.krobot.Krobot;
import org.krobot.KrobotModule;
import org.krobot.command.CommandFilter;
import org.krobot.command.runtime.ArgumentMap;
import org.krobot.command.runtime.CommandCall;
import org.krobot.command.runtime.MessageContext;
import org.krobot.command.runtime.CommandManager;
import org.krobot.module.LoadModule;
import org.krobot.runtime.ModuleLoader.ComputedModule;

public class KrobotRuntime
{
    public static final int DEFAULT_MAX_THREAD = 25;

    private static final Logger log = LogManager.getLogger("Krobot");
    private static KrobotRuntime current;

    private long startTime;
    private long time;

    private Bot bot;
    private Class<? extends KrobotModule> botClass;
    private String token;

    private List<RuntimeModule> modules;
    private ComputedModule rootModule;

    private JDA jda;
    private Injector injector;
    private String prefix;
    private FilterRunner filterRunner;
    private CommandManager commandManager;

    private int maxThread;
    private ThreadPoolExecutor threadPool;

    private StateBar stateBar;
    private long lastExecutionTime;

    private KrobotRuntime(Class<? extends KrobotModule> botClass, String token)
    {
        this.botClass = botClass;
        this.token = token;

        this.modules = new ArrayList<>();

        this.maxThread = DEFAULT_MAX_THREAD;

        this.lastExecutionTime = 0;
    }

    private void launch()
    {
        startTime = System.currentTimeMillis();

        bot = botClass.getAnnotation(Bot.class);

        if (bot == null)
        {
            log.error("The provided bot class ({}) doesn't have the @Bot annotation", botClass.getName());
            System.exit(1);
        }

        String disableProperty = System.getProperty(Krobot.PROPERTY_DISABLE_START_MESSAGE);

        if (disableProperty == null || !disableProperty.equalsIgnoreCase("true"))
        {
            log.info("                                     ");
            log.info("         hMMMMM:      -dMMMMMMs`     ");
            log.info("        +MMMMMs     oNMMMMMd-        ");
            log.info("       .MMMMMm   -dMMMMMN+           ");
            log.info("       +MMMMMo.yMMMMMNo`             ");
            log.info("       dMMMMMyMMMMMMy`               ");
            log.info("      oMMMMMMMMMMMMMMy               ");
            log.info("      mMMMMMMNs-mMMMMMo              ");
            log.info("     -MMMMMNo`  .NMMMMM+             ");
            log.info("    sMMMMMd       yMMMMMN.           ");
            log.info("   sMMMMM+        `dMMMMMd`          ");
            log.info("   /+++++`         `++++++-          ");
            log.info("                                     \n");
        }

        log.info("--> Starting {} v{} by {}\n", bot.name(), bot.version(), bot.author());

        log.info("Running Krobot 3.0.0");
        log.info("Copyright (c) 2017 The Krobot Contributors\n");

        log.info("----> 1/3 Pre-initialization");
        timerStart();

        log.info("Computing modules...");

        ModuleLoader loader = new ModuleLoader();
        rootModule = loader.load(botClass);

        prefix = rootModule.getModule().getPrefix();

        List<ComputedModule> modules = loader.getModules();

        modules.stream()
                .map(ComputedModule::getModule)
                .forEach(module -> Stream.of(module.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(LoadModule.class))
                .forEach(field -> {
                    field.setAccessible(true); try
                    {
                        field.set(module,
                                modules.stream()
                                        .map(ComputedModule::getModule)
                                        .filter(m -> m.getClass() == field.getType())
                                        .findFirst()
                                        .orElseThrow(() -> new RuntimeException("Cannot load module " + field.getType().getName() + " as it isn't imported. @LoadModule annotation can't be used without @Include / from(...)")));
                    }
                    catch (IllegalAccessException ignored) {}
                    field.setAccessible(false);
                }));

        log.info("Processing configs...");

        modules.forEach(source -> {
            RuntimeModule module = new RuntimeModule(source);

            ConfigLoader configLoader = new ConfigLoader(module);
            configLoader.load();

            this.modules.add(module);
        });

        log.info("----> Done in " + timerGet() + "ms\n");

        log.info("----> 2/3 Initialization");
        timerStart();

        log.info("Processing dependency injection...");

        List<Module> guiceModules = new ArrayList<>();
        modules.stream().map(m -> m.getModule().getGuiceModules()).forEach(guiceModules::addAll);
        guiceModules.add(new KrobotGuiceModule());

        injector = Guice.createInjector(guiceModules);
        this.modules.forEach((module) ->
        {
            KrobotModule m = module.getComputed().getModule();
            injector.injectMembers(m);

            try
            {
                Field field = KrobotModule.class.getDeclaredField("injector");

                field.setAccessible(true);
                {
                    field.set(m, injector.createChildInjector(new ModuleModule(module)));
                }
                field.setAccessible(false);
            }
            catch (IllegalAccessException | NoSuchFieldException e)
            {
                e.printStackTrace();
            }
        });

        modules.stream().map(ComputedModule::getModule).forEach(module ->
        {
            module.injector().injectMembers(module);
            module.init();
        });

        log.info("Processing filters...");
        filterRunner = new FilterRunner(this, modules.toArray(new ComputedModule[modules.size()]));

        modules.forEach(module -> module.getModule().getCommands().forEach(command ->
        {
            command.setFilters(ArrayUtils.add(command.getFilters(), new CommandFilter()
            {
                @Override
                public void filter(CommandCall call, MessageContext context, ArgumentMap args)
                {
                    if (filterRunner.isDisabled(context, module.getModule()))
                    {
                        call.setCancelled(true);
                    }
                }
            }));
        }));

        log.info("Processing commands...");

        commandManager = new CommandManager(this);

        // TODO: Command annotations

        modules.forEach(module -> module.getModule().getCommandFilters().forEach(filter ->
        {
            commandManager.getFilters().add(new CommandFilter()
            {
                @Override
                public void filter(CommandCall command, MessageContext context, ArgumentMap args)
                {
                    if (!filterRunner.isDisabled(context, module.getModule()))
                    {
                        filter.filter(command, context, args);
                    }
                }
            });
        }));

        modules.forEach(m -> commandManager.getCommands().addAll(m.getModule().getCommands()));

        log.info("Registered {} commands", commandManager.getCommands().size());

        log.info("----> Done in " + timerGet() + "ms\n");

        log.info("----> 3/3 Starting JDA");
        timerStart();

        try
        {
            jda = new JDABuilder(AccountType.BOT)
                .setEventManager(new AnnotatedEventManager())
                .addEventListener(this)
                .setToken(token)
                .buildBlocking();
        }
        catch (LoginException e)
        {
            log.error("Wrong bot token provided ! Exiting...");
            System.exit(1);
        }
        catch (InterruptedException ignored)
        {
        }
        catch (RateLimitedException e)
        {
            log.error("Got rate limited when logging in ! Please try again later, exiting...");
            System.exit(1);
        }

        log.info("----> Done in " + timerGet() + "ms\n");

        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);

        log.info("Now running {} v{} by {} [{}] (started in {}ms)\n", bot.name(), bot.version(), bot.author(), botClass.getName(), System.currentTimeMillis() - startTime);

        stateBar = new StateBar(this);
        stateBar.start();
    }

    @SubscribeEvent
    public void onMessage(MessageReceivedEvent event)
    {
        if (threadPool == null)
        {
            return;
        }

        final MessageContext context = new MessageContext(event.getJDA(), event.getAuthor(), event.getMessage(), event.getTextChannel()); // El famoso (kappa)

        threadPool.submit(() -> {
            long time = System.currentTimeMillis();

            filterRunner.runFilters(context);

            try
            {
                commandManager.handle(context);
            }
            catch (Exception e)
            {
                // TODO: Exception handler
                e.printStackTrace();
            }

            setLastExecutionTime(System.currentTimeMillis() - time);
        });
    }

    private void end()
    {
        stateBar.interrupt();
        jda.shutdown();
    }

    public RuntimeModule getRuntimeModule(Class<? extends KrobotModule> moduleClass)
    {
        for (RuntimeModule module : modules)
        {
            if (module.getComputed().getModule().getClass() == moduleClass)
            {
                return module;
            }
        }

        return null;
    }

    public boolean isRootModule(KrobotModule module)
    {
        return module == getRootModule().getModule();
    }

    public ComputedModule getRootModule()
    {
        return rootModule;
    }

    public Bot getBot()
    {
        return bot;
    }

    public ThreadPoolExecutor getThreadPool()
    {
        return threadPool;
    }

    public FilterRunner getFilterRunner()
    {
        return filterRunner;
    }

    public JDA jda()
    {
        return jda;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setMaxThread(int maxThread)
    {
        this.maxThread = maxThread;
    }

    public long getLastExecutionTime()
    {
        return lastExecutionTime;
    }

    private synchronized void setLastExecutionTime(long lastExecutionTime)
    {
        this.lastExecutionTime = lastExecutionTime;
    }

    private void timerStart()
    {
        time = System.currentTimeMillis();
    }

    private long timerGet()
    {
        return System.currentTimeMillis() - time;
    }

    public static KrobotRuntime start(Class<? extends KrobotModule> bot, String key)
    {
        if (current != null)
        {
            log.warn("Another runtime is tried to be launched when one is currently running");
            log.warn("Bot : " + current.botClass.getName());
            log.warn("Stopping the old one... Next time consider calling KrobotRuntime.stop()");

            stop();
        }

        current = new KrobotRuntime(bot, key);
        current.launch();

        return current;
    }

    public static boolean isStarted()
    {
        return current != null;
    }

    public static KrobotRuntime get()
    {
        if (current == null)
        {
            log.warn("KrobotRuntime.get() was called without any runtime launched, this will probably lead to a NullPointerException");
            log.warn("Consider using KrobotRuntime.isStarted() to check if a bot was launched");
        }

        return current;
    }

    public static void stop()
    {
        if (current == null)
        {
            log.error("Tried to stop the runtime, but no one was started, nothing will be done");
            return;
        }

        get().end();
    }

    public List<RuntimeModule> getModules()
    {
        return modules;
    }
}
