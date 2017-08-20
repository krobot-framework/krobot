package org.krobot.runtime;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import net.dv8tion.jda.core.JDA;
import org.krobot.KrobotModule;

public class KrobotGuiceModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        getKrobotRuntime().getModules().stream()
                          .map(m -> m.getComputed().getModule())
                          .forEach(module -> bind(getModuleClass(module)).toInstance(module));
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
    public KrobotRuntime getKrobotRuntime()
    {
        return KrobotRuntime.get();
    }
}
