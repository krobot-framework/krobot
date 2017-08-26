package org.krobot.runtime;

import org.krobot.config.ConfigProvider;
import org.krobot.runtime.ModuleLoader.ComputedModule;

public class RuntimeModule
{
    private ComputedModule module;
    private ConfigProvider config;

    public RuntimeModule(ComputedModule module)
    {
        this.module = module;
        this.config = new ConfigProvider();
    }

    public ComputedModule getComputed()
    {
        return module;
    }

    public ConfigProvider getConfig()
    {
        return config;
    }
}
