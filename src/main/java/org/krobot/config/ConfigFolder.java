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

import com.google.common.io.Files;
import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.krobot.config.ConfigAccessor.DefaultAccessor;

public class ConfigFolder
{
    private List<ConfigRules> configs;
    private File folder;

    public ConfigFolder(List<ConfigRules> configs, File folder)
    {
        this.configs = configs;
        this.folder = folder;
    }

    public ConfigAccessor config(String path)
    {
        ConfigRules rules = new ConfigRules(new File(folder, path).getPath());
        this.configs.add(rules);

        return new ConfigAccessor(rules);
    }

    public ConfigFolder configs(String... configs)
    {
        Stream.of(configs).forEach(this::config);
        return this;
    }

    public FolderDefaultAccessor withDefaultsIn()
    {
        return new FolderDefaultAccessor(this);
    }

    private List<ConfigRules> getConfigs()
    {
        return configs;
    }

    public static class FolderDefaultAccessor
    {
        private ConfigFolder configFolder;

        FolderDefaultAccessor(ConfigFolder configFolder)
        {
            this.configFolder = configFolder;
        }

        public ConfigFolder classpathFolder(String path)
        {
            if (!path.startsWith("/"))
            {
                path = "/" + path;
            }

            applyDef(path, ConfigRules.PathLocation.CLASSPATH);

            return this.configFolder;
        }

        public ConfigFolder folder(String path)
        {
            applyDef(path, ConfigRules.PathLocation.FILESYSTEM);
            return this.configFolder;
        }

        private void applyDef(String path, ConfigRules.PathLocation location)
        {
            configFolder.getConfigs().forEach(config -> {
                String name = Files.getNameWithoutExtension(config.getPath()) + ".default.json";
                config.setDef(new ConfigRules.DefaultPath(path + (path.endsWith("/") ? "" : "/") + name, location));
            });
        }
    }
}
