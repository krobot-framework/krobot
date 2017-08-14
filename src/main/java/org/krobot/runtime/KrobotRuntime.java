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

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.JDA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krobot.Krobot;
import org.krobot.KrobotModule;
import org.krobot.runtime.ModuleLoader.ComputedModule;

public class KrobotRuntime
{
    private static final Logger log = LogManager.getLogger("Krobot");
    private static KrobotRuntime current;

    private long time;

    private Class<? extends KrobotModule> botClass;
    private String key;

    private List<RuntimeModule> modules;
    private JDA jda;

    private KrobotRuntime(Class<? extends KrobotModule> botClass, String key)
    {
        this.botClass = botClass;
        this.key = key;

        this.modules = new ArrayList<>();
    }

    private void launch()
    {
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

        log.info("Krobot 3.0.0");
        log.info("Copyright (c) 2017 The Krobot Contributors\n");

        log.info("----> 1/4 Pre-initialization");
        timerStart();

        log.info("Computing module tree...");

        ModuleLoader loader = new ModuleLoader();
        loader.load(botClass);

        log.info("Processing configs...");

        List<ComputedModule> modules = loader.getModules();
        modules.forEach(source -> {
            RuntimeModule module = new RuntimeModule(source);

            ConfigLoader configLoader = new ConfigLoader(module);
            configLoader.load();

            this.modules.add(module);
        });

        log.info("----> Done in " + timerGet() + "ms\n");
    }

    private void end()
    {

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

    public JDA jda()
    {
        return jda;
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
}
