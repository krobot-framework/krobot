package org.krobot.module;

import org.krobot.KrobotModule;

import java.util.ArrayList;
import java.util.List;

import static org.krobot.module.ImportRules.Includes.*;

public class ImportRules
{
    private Class<? extends KrobotModule> module;

    private Includes[] includes;
    private List<ConfigBridge> bridges;
    private List<FilterRules> filters;

    public ImportRules(Class<? extends KrobotModule> module)
    {
        this.module = module;

        this.includes = new Includes[] {COMMANDS, CONFIGS, EVENTS, FILTERS};
        this.bridges = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    public Includes[] getIncludes()
    {
        return includes;
    }

    public void setIncludes(Includes[] includes)
    {
        this.includes = includes;
    }

    public List<ConfigBridge> getBridges()
    {
        return bridges;
    }

    public List<FilterRules> getFilters()
    {
        return filters;
    }

    public Class<? extends KrobotModule> getModule()
    {
        return module;
    }

    public static class ConfigBridge
    {
        private String config;
        private String dest;

        ConfigBridge(String config, String dest)
        {
            this.config = config;
            this.dest = dest;
        }

        public String getConfig()
        {
            return config;
        }

        public String getDest()
        {
            return dest;
        }
    }

    public enum Includes
    {
        COMMANDS,
        CONFIGS,
        EVENTS,
        FILTERS
    }
}
