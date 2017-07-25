package org.krobot;

import org.krobot.command.CommandManager;
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

    private CommandManager commands;
    private List<ImportRules> imports;
    private List<ConfigRules> configs;

    public KrobotModule()
    {
        this.imports = new ArrayList<>();
        this.configs = new ArrayList<>();
    }

    public abstract void init();

    protected void include(Class<? extends KrobotModule>... modules)
    {
        for (Class<? extends KrobotModule> module : modules)
        {
            from(module);
        }
    }

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
}
