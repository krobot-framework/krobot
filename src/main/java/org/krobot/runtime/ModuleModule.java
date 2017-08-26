package org.krobot.runtime;

import com.google.inject.AbstractModule;
import org.krobot.config.ConfigProvider;

public class ModuleModule extends AbstractModule
{
    private RuntimeModule module;

    public ModuleModule(RuntimeModule module)
    {
        this.module = module;
    }

    @Override
    protected void configure()
    {
        bind(ConfigProvider.class).toInstance(module.getConfig());
    }
}
