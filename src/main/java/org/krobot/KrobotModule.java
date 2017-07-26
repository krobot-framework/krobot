package org.krobot;

import com.google.inject.Injector;
import org.krobot.command.CommandAccessor;
import org.krobot.command.CommandManager;
import org.krobot.command.ICommandHandler;
import org.krobot.config.ConfigAccessor;
import org.krobot.config.ConfigRules;
import org.krobot.module.ImportAccessor;
import org.krobot.module.ImportRules;

import java.util.ArrayList;
import java.util.List;

import static org.krobot.module.ImportRules.*;

public abstract class KrobotModule
{
    protected static final Includes COMMANDS = Includes.COMMANDS;
    protected static final Includes CONFIGS = Includes.CONFIGS;
    protected static final Includes EVENTS = Includes.EVENTS;

    protected CommandManager commands;
    protected Injector injector;
    private List<ImportRules> imports;
    private List<ConfigRules> configs;

    public KrobotModule()
    {
        this.imports = new ArrayList<>();
        this.configs = new ArrayList<>();
        this.commands = new CommandManager(this);
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

    protected CommandAccessor command(String path, Class<? extends ICommandHandler> handler)
    {
        return commands.make(path, injector.getInstance(handler));
    }

    protected CommandAccessor command(String path, ICommandHandler handler)
    {
        return commands.make(path, handler);
    }

    protected void prefix(String prefix)
    {
        this.commands.setPrefix(prefix);
    }

    public Injector injector()
    {
        return this.injector;
    }
}
