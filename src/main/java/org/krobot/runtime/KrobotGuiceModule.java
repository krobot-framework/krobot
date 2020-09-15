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

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.util.Providers;
import net.dv8tion.jda.api.JDA;
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
    public KrobotRuntime getKrobotRuntime()
    {
        return runtime;
    }
}
