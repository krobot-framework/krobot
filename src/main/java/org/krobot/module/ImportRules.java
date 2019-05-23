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
    private ParentCommand parentCommand;

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

    public ParentCommand getParentCommand()
    {
        return parentCommand;
    }

    public void setParentCommand(ParentCommand parentCommand)
    {
        this.parentCommand = parentCommand;
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
