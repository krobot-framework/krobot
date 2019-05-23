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
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.fusesource.jansi.Ansi.Color;
import org.krobot.KrobotModule;
import org.krobot.command.CommandArgument;
import org.krobot.command.KrobotCommand;
import org.krobot.config.ConfigRules;
import org.krobot.module.FilterRules;
import org.krobot.module.ImportRules.ConfigBridge;
import org.krobot.module.ImportRules.Includes;
import org.krobot.module.Include;
import org.krobot.module.ParentCommand;
import org.krobot.util.ColoredLogger;

public class ModuleLoader
{
    private static final ColoredLogger log = ColoredLogger.getLogger("ModuleLoader");

    private List<ComputedModule> modules;

    public ModuleLoader()
    {
        this.modules = new ArrayList<>();
    }

    public ComputedModule load(Class<? extends KrobotModule> moduleClass)
    {
        log.info("(" + moduleClass.getName() + ") Pre-initialization...");

        ComputedModule module = null;

        for (ComputedModule computed : modules)
        {
            if (computed.getModule().getClass() == moduleClass)
            {
                module = computed;
            }
        }

        if (module != null)
        {
            return module;
        }

        KrobotModule instance = null;

        try
        {
            instance = moduleClass.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            log.errorAuto("@|red Error while creating instance of module|@ @|red,bold " + moduleClass.getName() + "|@|@red ; remember that@| |@red,bold modules must have an empty constructor|@", e);
            System.exit(1);
        }

        module = new ComputedModule(instance);
        modules.add(module);

        importFromAnnotation(module);
        preInit(module);

        return module;
    }

    private void importFromAnnotation(ComputedModule module)
    {
        Include include = module.getModule().getClass().getAnnotation(Include.class);

        if (include == null)
        {
            return;
        }

        Stream.of(include.imports()).forEach(this::load);
    }

    public void preInit(ComputedModule module)
    {
        module.getModule().preInit();

        // Importing everything

        module.getConfigs().addAll(module.getModule().getConfigs());

        module.getModule().getImports().forEach(rules -> {
            ComputedModule loaded = load(rules.getModule());

            List<Pair<ConfigBridge, KrobotModule>> bridges = new ArrayList<>();
            rules.getBridges().forEach(bridge -> bridges.add(new ImmutablePair<>(bridge, module.getModule())));

            if (loaded.getIncludes() != null)
            {
                log.warn(Color.YELLOW,  "The module " + loaded.getModule().getClass().getName() + " was imported using from(...) multiple times");
                log.warn(Color.YELLOW, "If you defined inclusions/exclusions, they may differ from what you excepted");
            }

            loaded.getBridges().addAll(bridges);
            loaded.getFilters().addAll(rules.getFilters());
            loaded.setIncludes(rules.getIncludes());

            if (rules.getParentCommand() != null)
            {
                ParentCommand parent = rules.getParentCommand();
                KrobotCommand command = new KrobotCommand(parent.getParentLabel(), new CommandArgument[]{}, null);

                command.setHandler((context, args) -> {
                    KrobotCommand sub = null;

                    for (KrobotCommand c : command.getSubCommands())
                    {
                        if (c.getLabel().equals(parent.getDefaultSub()))
                        {
                            sub = c;
                            break;
                        }
                    }

                    if (sub == null)
                    {
                        log.warnAuto("@|yellow Module '|@@|yellow,bold{}|@@|yellow' was imported as sub-commands of command '|@@|yellow,bold{}|@@|yellow', but|@", module.getModule().getClass().getName(), command.getLabel());
                        log.warnAuto("@|yellow Can't find the default sub command '|@@|yellow,bold{}|@@|yellow', nothing will be done|@", parent.getDefaultSub());

                        return null;
                    }

                    return sub.getHandler().handle(context, args);
                });

                loaded.setParentCommand(command);
            }
        });
    }

    public List<ComputedModule> getModules()
    {
        return modules;
    }

    public static class ComputedModule
    {
        private KrobotModule module;

        private List<FilterRules> filters;
        private List<ConfigRules> configs;
        private List<Pair<ConfigBridge, KrobotModule>> bridges;
        private KrobotCommand parentCommand;

        private Includes[] includes;

        public ComputedModule(KrobotModule module)
        {
            this.module = module;

            this.filters = new ArrayList<>();
            this.configs = new ArrayList<>();
            this.bridges = new ArrayList<>();

            this.includes = null;
        }

        public KrobotModule getModule()
        {
            return module;
        }

        public List<FilterRules> getFilters()
        {
            return filters;
        }

        public List<ConfigRules> getConfigs()
        {
            return configs;
        }

        public List<Pair<ConfigBridge, KrobotModule>> getBridges()
        {
            return bridges;
        }

        public Includes[] getIncludes()
        {
            return includes;
        }

        public void setIncludes(Includes[] includes)
        {
            this.includes = includes;
        }

        public KrobotCommand getParentCommand()
        {
            return parentCommand;
        }

        public void setParentCommand(KrobotCommand parentCommand)
        {
            this.parentCommand = parentCommand;
        }
    }
}
