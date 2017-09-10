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

public class ConfigAccessor
{
    private ConfigRules rules;

    public ConfigAccessor(ConfigRules rules)
    {
        this.rules = rules;
    }

    public ConfigAccessor named(String name)
    {
        this.rules.setName(name);
        return this;
    }

    public DefaultAccessor defaultIn()
    {
        return new DefaultAccessor(rules, this);
    }

    public static class DefaultAccessor
    {
        private ConfigRules rules;
        private ConfigAccessor configAccessor;

        DefaultAccessor(ConfigRules rules, ConfigAccessor configAccessor)
        {
            this.rules = rules;
            this.configAccessor = configAccessor;
        }

        public ConfigAccessor classpath(String path)
        {
            if (!path.startsWith("/"))
            {
                path = "/" + path;
            }

            this.rules.setDef(new ConfigRules.DefaultPath(path, ConfigRules.PathLocation.CLASSPATH));
            return this.configAccessor;
        }

        public ConfigAccessor file(String path)
        {
            this.rules.setDef(new ConfigRules.DefaultPath(path, ConfigRules.PathLocation.FILESYSTEM));
            return this.configAccessor;
        }
    }
}
