package org.krobot.runtime;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.util.Providers;
import net.dv8tion.jda.core.JDA;
import org.krobot.KrobotModule;
import org.krobot.command.CommandManager;

public class KrobotGuiceModule extends AbstractModule
{
    private KrobotRuntime runtime;

    public KrobotGuiceModule(KrobotRuntime runtime)
    {
        this.runtime = runtime;
    }

    @Override
    protected void configure()
    {
        getKrobotRuntime().getModules().stream()
                          .map(m -> m.getComputed().getModule())
                          .forEach(module -> bind(getModuleClass(module)).toProvider(Providers.of(module)));
    }

    private <T extends KrobotModule> Key<T> getModuleClass(KrobotModule module)
    {
        return Key.get((Class<T>) module.getClass());
    }

    @Provides
    public JDA getJDA()
    {
        return getKrobotRuntime().jda();
    }

    @Provides
    public CommandManager getCommandManager()
    {
        return getKrobotRuntime().getCommandManager();
    }

    @Provides
    public KrobotRuntime getKrobotRuntime()
    {
        return runtime;
    }
}
