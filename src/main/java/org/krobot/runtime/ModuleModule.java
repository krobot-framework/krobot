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
