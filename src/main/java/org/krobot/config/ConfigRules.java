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

public class ConfigRules
{
    private String path;
    private String name;
    private DefaultPath def;

    public ConfigRules(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public DefaultPath getDef()
    {
        return def;
    }

    public void setDef(DefaultPath def)
    {
        this.def = def;
    }

    public static class DefaultPath
    {
        private String path;
        private PathLocation location;

        DefaultPath(String path, PathLocation location)
        {
            this.path = path;
            this.location = location;
        }

        public String getPath()
        {
            return path;
        }

        public PathLocation getLocation()
        {
            return location;
        }

        @Override
        public String toString()
        {
            return "(from " + location.name().toLowerCase() + ") " + path;
        }
    }

    public enum PathLocation
    {
        CLASSPATH,
        FILESYSTEM
    }
}
