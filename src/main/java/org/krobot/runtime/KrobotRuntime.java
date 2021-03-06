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

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;

import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.krobot.Bot;
import org.krobot.Krobot;
import org.krobot.KrobotModule;
import org.krobot.MessageContext;
import org.krobot.command.Command;
import org.krobot.command.CommandFilter;
import org.krobot.command.CommandHandler;
import org.krobot.command.CommandManager;
import org.krobot.command.ExceptionHandler;
import org.krobot.command.KrobotCommand;
import org.krobot.command.PathCompiler;
import org.krobot.console.ExitCommand;
import org.krobot.console.HelpCommand;
import org.krobot.console.KrobotConsole;
import org.krobot.module.Include;
import org.krobot.module.LoadModule;
import org.krobot.runtime.ModuleLoader.ComputedModule;
import org.krobot.util.ColoredLogger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class KrobotRuntime
{
    // TODO: Rolling logger

    public static final int DEFAULT_MAX_THREAD = 25;

    private static final ColoredLogger log = ColoredLogger.getLogger("Krobot");
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

    private KrobotConsole console;
    private StateBar stateBar;
    private long lastExecutionTime;
    private long uptime;

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

        String prop = System.getProperty(Krobot.PROPERTY_DISABLE_COLORS);

        if (System.console() == null && (prop == null || !prop.equalsIgnoreCase("false")))
        {
            System.err.println("\n/!\\ No console detected => Disabled colors /!\\\n");
            prop = "true";
        }

        if (prop == null || !prop.equalsIgnoreCase("true"))
        {
            AnsiConsole.systemInstall();
        }
        else
        {
            Ansi.setEnabled(false);
        }

        String disableProperty = System.getProperty(Krobot.PROPERTY_DISABLE_START_MESSAGE);

        if (disableProperty == null || !disableProperty.equalsIgnoreCase("true"))
        {
            log.info(ansi().eraseScreen());
            log.info("                                     ");
            log.infoBold(BLUE, "         hMMMMM:      -dMMMMMMs`     ");
            log.infoBold(BLUE, "        +MMMMMs     oNMMMMMd-        ");
            log.infoBold(BLUE, "       .MMMMMm   -dMMMMMN+           ");
            log.infoBold(BLUE, "       +MMMMMo.yMMMMMNo`             ");
            log.infoBold(BLUE, "       dMMMMMyMMMMMMy`               ");
            log.infoBold(BLUE, "      oMMMMMMMMMMMMMMy               ");
            log.infoBold(BLUE, "      mMMMMMMNs-mMMMMMo              ");
            log.infoBold(BLUE, "     -MMMMMNo`  .NMMMMM+             ");
            log.infoBold(BLUE, "    sMMMMMd       yMMMMMN.           ");
            log.infoBold(BLUE, "   sMMMMM+        `dMMMMMd`          ");
            log.infoBold(BLUE, "   /+++++`         `++++++-          ");
            log.infoBold(BLUE, "                                     \n");
        }

        log.infoBold("--> Starting {} v{} by {}\n", bot.name(), bot.version(), bot.author());

        log.info("Running Krobot 3.0.0");
        log.info("Copyright (c) 2017 The Krobot Contributors\n");

        log.infoBold("----> 1/4 Pre-initialization");
        timerStart();

        log.info("Computing modules...");

        ModuleLoader loader = new ModuleLoader();
        rootModule = loader.load(botClass);

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

        log.infoBold("----> Done in " + timerGet() + "ms\n");

        log.infoBold("----> 2/4 Initialization");
        timerStart();

        log.info("Processing dependency injection...");

        List<Module> guiceModules = new ArrayList<>();
        modules.stream().map(m -> m.getModule().getGuiceModules()).forEach(guiceModules::addAll);
        guiceModules.add(new KrobotGuiceModule(this));

        injector = Guice.createInjector(guiceModules);
        this.modules.forEach((module) ->
        {
            KrobotModule m = module.getComputed().getModule();

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
            log.info("({}) Initialization...", module.getClass().getName());

            module.injector().injectMembers(module);
            module.init();
        });

        prefix = rootModule.getModule().getPrefix();

        log.info("Processing filters...");

        modules.forEach(module -> module.getFilters().addAll(module.getModule().getFilters()));

        filterRunner = new FilterRunner(this, modules.toArray(new ComputedModule[modules.size()]));

        modules.forEach(module -> module.getModule().getCommands().forEach(command ->
        {
            command.getFilters().add((call, context, args) -> {
                if (filterRunner.isDisabled(context, module.getModule()))
                {
                    call.setCancelled(true);
                }
            });
        }));

        log.info("Processing commands...");

        commandManager = injector.getInstance(CommandManager.class);

        modules.forEach(module -> module.getModule().getCommandFilters().forEach(filter ->
        {
            commandManager.getFilters().add((command, context, args) -> {
                if (!filterRunner.isDisabled(context, module.getModule()))
                {
                    filter.filter(command, context, args);
                }
            });
        }));

        modules.forEach(module -> {
            Class<? extends KrobotModule> cl = module.getModule().getClass();

            if (cl.isAnnotationPresent(Include.class))
            {
                Stream.of(cl.getAnnotation(Include.class).filters()).map(injector::getInstance).forEach(commandManager.getFilters()::add);
            }
        });
        
        modules.forEach(module -> {
            Class<? extends KrobotModule> cl = module.getModule().getClass();

            if (cl.isAnnotationPresent(Include.class))
            {
                Class<? extends CommandHandler>[] classes = cl.getAnnotation(Include.class).commands();

                for (Class<? extends CommandHandler> commandClass : classes)
                {
                    KrobotCommand command = registerCommandClass(module.getModule(), commandClass);

                    if (command != null)
                    {
                        KrobotCommand parent = module.getParentCommand();

                        if (parent != null)
                        {
                            parent.getSubCommands().add(command);
                            continue;
                        }

                        addCommand(module.getModule(), command);
                    }
                }
            }
        });

        modules.forEach(m -> {
            KrobotCommand parent = m.getParentCommand();
            List<KrobotCommand> commands = m.getModule().getCommands();

            if (parent != null)
            {
                parent.getSubCommands().addAll(Arrays.asList(commands.toArray(new KrobotCommand[0])));
                addCommand(m.getModule(), parent);

                return;
            }

            commands.forEach(c -> addCommand(m.getModule(), c));
        });

        List<String> labels = new ArrayList<>();
        commandManager.getCommands().forEach(command -> {
            if (labels.contains(command.getLabel()))
            {
                log.error(Color.RED, "Duplicated command '{}' => One will be randomly executed", command.getLabel());
            }

            labels.add(command.getLabel());
        });

        log.info("Registered {} commands", commandManager.getCommands().size());

        console = new KrobotConsole(this);
        console.register(new ExitCommand());
        console.register(new HelpCommand(console));

        modules.forEach(m -> m.getModule().getConsoleCommands().forEach(c -> console.register(c)));

        modules.stream().map(ComputedModule::getModule).filter(m -> m.getClass().isAnnotationPresent(Include.class)).forEach(m -> {
            Stream.of(m.getClass().getAnnotation(Include.class).consoleCommands()).forEach(console::register);
        });

        log.info("Registered {} console commands", console.getCommands().size());

        log.infoBold("----> Done in " + timerGet() + "ms\n");

        log.infoBold("----> 3/4 Starting JDA");
        timerStart();

        try
        {
            jda = JDABuilder.createDefault(token)
                .setEventManager(new AnnotatedEventManager())
                .addEventListeners(this)
                .setToken(token)
                .build()
                .awaitReady();
        }
        catch (LoginException e)
        {
            if (e.getCause() instanceof UnknownHostException)
            {
                log.error("You aren't connected to the Internet ! Exiting...");
            }
            else
            {
                log.error("Wrong bot token provided ! Exiting...");
            }

            System.exit(1);
        }
        catch (InterruptedException ignored)
        {
        }

        modules.forEach(m -> {
            m.getModule().getEventListeners().forEach(jda::addEventListener);

            Class<? extends KrobotModule> moduleClass = m.getModule().getClass();

            if (moduleClass.isAnnotationPresent(Include.class))
            {
                Object[] listeners = Stream.of(moduleClass.getAnnotation(Include.class).listeners()).map(m.getModule().injector()::getInstance).toArray();
                jda.addEventListener(listeners);
            }
        });

        log.infoBold("----> Done in " + timerGet() + "ms\n");

        log.infoBold("----> 4/4 Post-Initialization");
        timerStart();

        modules.stream().map(ComputedModule::getModule).forEach(module ->
        {
            log.info("({}) Post-Initialization...", module.getClass().getName());

            module.postInit();
        });

        if (System.console() == null)
        {
            if (System.getProperty(Krobot.PROPERTY_DISABLE_STATE_BAR) == null)
            {
                log.warn(Color.YELLOW, "No console detected, you are probably running from an IDE");
                log.warn(Color.YELLOW, "In these case, state bar can be really buggy");
                log.warn(Color.YELLOW, "If they are, disable them by adding -Dkrobot.disableStateBar=true in the run VM args\n");
            }

            // Disabling ugly JLine message
            Logger.getLogger("org.jline").setLevel(Level.OFF);
        }

        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThread);

        log.infoBold("----> Done in " + timerGet() + "ms\n");

        log.infoAuto("@|green Now running|@ @|bold,green {} v{} by {}|@ @|green [{}] (started in|@ @|bold,green {}ms|@@|green )|@", bot.name(), bot.version(), bot.author(), botClass.getName(), System.currentTimeMillis() - startTime);
        log.infoAuto("@|green Press any key to enter a command, do '|@@|bold,green exit|@@|green ' to close|@\n");

        String disable = System.getProperty(Krobot.PROPERTY_DISABLE_STATE_BAR);
        if (disable == null || !disable.equals("true"))
        {
            stateBar = new StateBar(this);
            stateBar.start();
        }

        disable = System.getProperty(Krobot.PROPERTY_DISABLE_CONSOLE);
        if (disable == null || !disable.equals("true"))
        {
            console.start();
        }

        uptime = System.currentTimeMillis();
    }

    protected void addCommand(KrobotModule source, KrobotCommand command)
    {
        command.getFilters().add((call, context, args) -> {
            if (filterRunner.isDisabled(context, source))
            {
                call.setCancelled(true);
            }
        });

        commandManager.getCommands().add(command);
    }

    protected KrobotCommand registerCommandClass(KrobotModule module, Class<? extends CommandHandler> commandClass)
    {
        if (!commandClass.isAnnotationPresent(Command.class))
        {
            log.error(Color.RED, "Class '{}' declared in @Include#commands is missing @Command annotation", commandClass.getName());
            log.error(Color.RED, "Command will not be registered");

            return null;
        }

        Command command = commandClass.getAnnotation(Command.class);

        PathCompiler compiler = new PathCompiler(command.value());

        try
        {
            compiler.compile();
        }
        catch (PathCompiler.PathSyntaxException e)
        {
            System.exit(1);
        }

        Injector injector = module.injector();

        List<CommandFilter> filters = Stream.of(command.filters()).map(injector::getInstance).collect(Collectors.toList());
        List<KrobotCommand> subs = Stream.of(command.subs()).map(c -> registerCommandClass(module, c)).collect(Collectors.toList());

        return new KrobotCommand(compiler.label(), compiler.args(), command.desc(), command.aliases(), command.errorMP(), command.handleMP(), filters, injector.getInstance(commandClass), subs);
    }

    @SubscribeEvent
    public void onMessage(MessageReceivedEvent event)
    {
        if (event.getAuthor() instanceof SelfUser) return;

        MessageChannel channel;
        if (event.isFromType(ChannelType.TEXT)) channel = event.getChannel();
        else if (event.isFromType(ChannelType.PRIVATE)) channel = event.getAuthor().openPrivateChannel().complete();
        else return;

        final MessageContext context = new MessageContext(event.getJDA(), event.getAuthor(), event.getMessage(), channel);
        handle(context);
    }

    protected void handle(MessageContext context)
    {
        if (threadPool == null)
        {
            return;
        }

        threadPool.submit(() -> {
            long time = System.currentTimeMillis();

            try
            {
                filterRunner.runFilters(context);
                commandManager.handle(context);
            }
            catch (Exception e)
            {
                log.error("Error while handling message", e);
            }

            setLastExecutionTime(System.currentTimeMillis() - time);
        });
    }

    private void end()
    {
        log.infoAuto("@|bold ----> Shutting down...|@");

        if (stateBar != null)
        {
            stateBar.interrupt();
        }

        jda.shutdown();
    }

    public ExceptionHandler getExceptionHandler()
    {
        return injector.getInstance(ExceptionHandler.class);
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

    public Injector getInjector()
    {
        return injector;
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

    public long getUptime()
    {
        return System.currentTimeMillis() - uptime;
    }

    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    public StateBar getStateBar()
    {
        return stateBar;
    }

    public KrobotConsole getConsole()
    {
        return console;
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
