/*
 * Copyright 2017 Adrien "Litarvan" Navratil
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
package fr.litarvan.krobot.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class ConfigProvider
{
    private static final Logger LOGGER = LogManager.getLogger("ConfigProvider");

    private Map<String, Config> configs = new HashMap<>();

    public FileConfig from(String file)
    {
        return from(new File(file));
    }

    public FileConfig from(File file)
    {
        return json(file);
    }

    public JSONConfig json(File file)
    {
        return json(file, file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    public JSONConfig json(File file, String name)
    {
        return register(new JSONConfig(file), name);
    }

    public PropertiesConfig properties(File file)
    {
        return properties(file, file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    public PropertiesConfig properties(File file, String name)
    {
        return register(new PropertiesConfig(file), name);
    }

    public <T extends FileConfig> T register(T config, String name)
    {
        LOGGER.info("Loaded config -> " + name + " (" + config.getFile().getAbsolutePath() + ")");
        return (T) register((Config) config, name);
    }

    public <T extends Config> T register(T config, String name)
    {
        configs.put(name, config);
        return config;
    }

    public Config get(String name)
    {
        return configs.get(name);
    }

    public Object at(String path)
    {
        int index = path.indexOf(".");
        return get(path.substring(0, index)).at(path.substring(index + 1));
    }
}
