package org.krobot;

import com.google.inject.Injector;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import org.krobot.command.ArgumentFactory;
import org.krobot.command.CommandAccessor;
import org.krobot.command.ICommandHandler;
import org.krobot.command.KrobotCommand;
import org.krobot.config.ConfigAccessor;
import org.krobot.config.ConfigRules;
import org.krobot.module.Filter;
import org.krobot.module.FilterAccessor;
import org.krobot.module.FilterRules;
import org.krobot.module.ImportAccessor;
import org.krobot.module.ImportRules;

import java.util.ArrayList;
import java.util.List;
import org.krobot.util.PathCompiler;


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

    private Map<String, ArgumentFactory> argFactories;

    private String prefix;
    private List<KrobotCommand> commands;

    public KrobotModule()
    {
        this.imports = new ArrayList<>();
        this.configs = new ArrayList<>();
        this.filters = new ArrayList<>();

        this.argFactories = new HashMap<>();

        this.commands = new ArrayList<>();
    }

    public abstract void preInit();
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

    protected FilterAccessor when(Filter filter)
    {
        FilterRules rules = new FilterRules(filter);
        this.filters.add(rules);

        return new FilterAccessor(rules);
    }

    protected FilterAccessor guild(Function<Guild, Boolean> guildFilter)
    {
        return when(context -> guildFilter.apply(context.getGuild()));
    }

    protected FilterAccessor guild(String guildName)
    {
        return guild(guild -> guild.getName().equalsIgnoreCase(guildName));
    }

    protected CommandAccessor command(String path, Class<? extends ICommandHandler> handler)
    {
        return command(path, injector.getInstance(handler));
    }

    protected CommandAccessor command(String path, ICommandHandler handler)
    {
        PathCompiler compiler = new PathCompiler(path);
        compiler.compile();

        KrobotCommand command = new KrobotCommand(compiler.label(), compiler.args(), handler);
        commands.add(command);

        return new CommandAccessor(this, command);
    }

    protected <T> void defineType(String name, Class<T> type, ArgumentFactory<T> factory)
    {
        this.argFactories.put(name, factory);
    }

    protected void prefix(String prefix)
    {
        this.prefix = prefix;
    }

    protected JDA jda()
    {
        return injector().getInstance(JDA.class);
    }

    public Injector injector()
    {
        return this.injector;
    }

    String getPrefix()
    {
        return prefix;
    }
}
