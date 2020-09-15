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
package org.krobot;

import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.krobot.command.CommandFilter;
import org.krobot.command.PathCompiler.PathSyntaxException;
import org.krobot.command.ArgumentFactory;
import org.krobot.command.CommandAccessor;
import org.krobot.command.CommandHandler;
import org.krobot.command.KrobotCommand;
import org.krobot.config.ConfigAccessor;
import org.krobot.config.ConfigRules;
import org.krobot.config.ConfigFolder;
import org.krobot.console.ConsoleCommand;
import org.krobot.module.Filter;
import org.krobot.module.FilterAccessor;
import org.krobot.module.FilterRules;
import org.krobot.module.ImportAccessor;
import org.krobot.module.ImportRules;

import java.util.ArrayList;
import java.util.List;
import org.krobot.runtime.KrobotRuntime;
import org.krobot.command.PathCompiler;


import static org.krobot.module.ImportRules.*;

public abstract class KrobotModule
{
    protected static final Includes COMMANDS = Includes.COMMANDS;
    protected static final Includes CONFIGS = Includes.CONFIGS;
    protected static final Includes EVENTS = Includes.EVENTS;
    protected static final Includes FILTERS = Includes.FILTERS;

    protected Injector injector;

    private List<ImportRules> imports;
    private List<ConfigRules> configs;
    private List<FilterRules> filters;
    private List<Object> eventListeners;

    private String prefix;
    private List<KrobotCommand> commands;
    private List<CommandFilter> commandFilters;
    private Map<String, ArgumentFactory> argTypes;

    private List<Module> guiceModules;

    private List<ConsoleCommand> consoleCommands;

    public KrobotModule()
    {
        this.imports = new ArrayList<>();
        this.configs = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.eventListeners = new ArrayList<>();

        this.commands = new ArrayList<>();
        this.commandFilters = new ArrayList<>();
        this.argTypes = new HashMap<>();

        this.guiceModules = new ArrayList<>();

        this.consoleCommands = new ArrayList<>();
    }

    public abstract void preInit();
    public abstract void init();
    public abstract void postInit();

    protected ImportAccessor from(Class<? extends KrobotModule> module)
    {
        ImportRules rules = new ImportRules(module);
        this.imports.add(rules);

        return new ImportAccessor(rules);
    }

    protected ConfigAccessor config(String path)
    {
        ConfigRules rules = new ConfigRules(path);
        this.configs.add(rules);

        return new ConfigAccessor(rules);
    }

    protected ConfigFolder folder(String path)
    {
        return folder(new File(path));
    }

    protected ConfigFolder folder(File file)
    {
        return new ConfigFolder(configs, file);
    }

    protected FilterAccessor when(Filter filter)
    {
        FilterRules rules = new FilterRules(filter);
        this.filters.add(rules);

        return new FilterAccessor(rules);
    }

    protected FilterAccessor guild(Function<Guild, Boolean> guildFilter)
    {
        return when(context -> {
            Guild guild = context.getGuild();
            if (guild == null)
            {
                return false;
            }

            return guildFilter.apply(guild);
        });
    }

    protected FilterAccessor guild(String guildName)
    {
        return guild(guild -> guild.getName().equalsIgnoreCase(guildName));
    }

    protected CommandAccessor command(String path, Class<? extends CommandHandler> handler)
    {
        return command(path, injector.getInstance(handler));
    }

    protected CommandAccessor command(String path, CommandHandler handler)
    {
        PathCompiler compiler = new PathCompiler(path.trim());

        try
        {
            compiler.compile();
        }
        catch (PathSyntaxException e)
        {
            System.exit(0);
        }

        KrobotCommand command = new KrobotCommand(compiler.label(), compiler.args(), handler);
        commands.add(command);

        return new CommandAccessor(this, command);
    }

    @SafeVarargs /* To suppress an ugly warning */
    protected final /* @SafeVarargs requires final */ void filters(Class<CommandFilter>... filters)
    {
        filters(Stream.of(filters).map(injector::getInstance).toArray(CommandFilter[]::new));
    }

    protected void filters(CommandFilter... filters)
    {
        commandFilters.addAll(Arrays.asList(filters));
    }

    @SafeVarargs /* To suppress an ugly warning */
    protected final /* @SafeVarargs requires final */ void listeners(Class<? extends Object>... listeners) // "? extends Object" needed for SafeVarargs
    {
        listeners(Stream.of(listeners).map(injector::getInstance).toArray(Object[]::new));
    }

    protected void listeners(Object... listeners)
    {
        eventListeners.addAll(Arrays.asList(listeners));
    }

    protected <T> void defineArgType(String name, Class<T> type, ArgumentFactory<T> factory)
    {
        this.argTypes.put(name, factory);
    }

    protected void addGuiceModules(Module... modules)
    {
        this.guiceModules.addAll(Arrays.asList(modules));
    }

    @SafeVarargs /* To suppress an ugly warning */
    protected final /* @SafeVarargs requires final */ void consoleCommands(Class<? extends ConsoleCommand>... commands)
    {
        consoleCommands(Stream.of(commands).map(injector::getInstance).toArray(ConsoleCommand[]::new));
    }

    protected void consoleCommands(ConsoleCommand... commands)
    {
        consoleCommands.addAll(Arrays.asList(commands));
    }

    protected void prefix(String prefix)
    {
        this.prefix = prefix;
    }

    protected JDA jda()
    {
        return KrobotRuntime.get().jda();
    }

    public Injector injector()
    {
        return this.injector;
    }

    public List<ImportRules> getImports()
    {
        return imports;
    }

    public List<ConfigRules> getConfigs()
    {
        return configs;
    }

    public List<FilterRules> getFilters()
    {
        return filters;
    }

    public List<Object> getEventListeners()
    {
        return eventListeners;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public List<KrobotCommand> getCommands()
    {
        return commands;
    }

    public List<CommandFilter> getCommandFilters()
    {
        return commandFilters;
    }

    public Map<String, ArgumentFactory> getArgTypes()
    {
        return argTypes;
    }

    public List<ConsoleCommand> getConsoleCommands()
    {
        return consoleCommands;
    }

    public List<Module> getGuiceModules()
    {
        return guiceModules;
    }
}
