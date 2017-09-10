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
package org.krobot.config;

public class BridgeConfig implements Config
{
    private ConfigProvider target;
    private String path;

    public BridgeConfig(ConfigProvider target, String path)
    {
        this.target = target;
        this.path = path;
    }

    @Override
    public <T> T get(String key, T def, Class<T> type)
    {
        return at(key, def, type);
    }

    @Override
    public void set(String key, Object value)
    {
        target.set(path + "." + key, value);
    }

    @Override
    public <T> T at(String path, T def, Class<T> type)
    {
        return target.at(this.path + "." + path, def, type);
    }
}
