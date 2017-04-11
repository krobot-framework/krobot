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
import org.jetbrains.annotations.Nullable;

/**
 * The Config Provider<br/><br/>
 *
 *
 * Manage the configs of a bot.<br/><br/>
 *
 * <b>Registering a config :</b>
 *
 * <pre>
 *     provider.json("config/myconfig.json");
 *     // Or
 *     provider.properties("config/myconfig.properties");
 *     // Or
 *     provider.json("myconfig").in("config/myconfig.json");
 *     // Etc...
 * </pre>
 *
 * <b>Getting a value :</b>
 *
 * <pre>
 *     String value = (String) provider.get("myconfig").get("myValue");
 *     // or
 *     String value = (String) provider.at("myconfig.subobject.subsubobject.value");
 *     // etc...
 * </pre>
 *
 * <b>Setting a value :</b>
 *
 * <pre>
 *     provider.get("myconfig").set("myValue", "value");
 *     // or
 *     provider.get("myconfig").set("myValue.subobject.subsubobject.value", "value");
 *     // etc...
 * </pre>
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
@Singleton
public class ConfigProvider
{
    private static final Logger LOGGER = LogManager.getLogger("ConfigProvider");

    private Map<String, Config> configs = new HashMap<>();

    /**
     * Create a config from a file path.<br/>
     * It names will be the file name without the extension.
     *
     * @param file The path of the config file
     *
     * @return A {@link JSONConfig} of this file.
     */
    public FileConfig from(String file)
    {
        return from(new File(file));
    }

    /**
     * Create a config from a file.<br/>
     * It names will be the file name without the extension.
     *
     * @param file The path of the config file
     *
     * @return A {@link JSONConfig} of this file.
     */
    public FileConfig from(File file)
    {
        return json(file);
    }

    /**
     * Create a JSON config with the given name.
     *
     * @param name The name of the config
     *
     * @return A new {@link JSONConfig}
     */
    public JSONConfig json(String name)
    {
        return register(new JSONConfig(), name);
    }

    /**
     * Create a JSON config from a file.<br/>
     * It names will be the file name without the extension.
     *
     * @param file The path of the config file
     *
     * @return A {@link JSONConfig} of this file.
     */
    public JSONConfig json(File file)
    {
        return json(file, file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    /**
     * Create a JSON config from a file with the given name
     *
     * @param file The path of the config file
     * @param name The name of the config
     *
     * @return A new {@link JSONConfig}
     */
    public JSONConfig json(File file, String name)
    {
        return register(new JSONConfig(file), name);
    }

    /**
     * Create a Java Properties config with the given name.
     *
     * @param name The name of the config
     *
     * @return A new {@link PropertiesConfig}
     */
    public PropertiesConfig properties(String name)
    {
        return register(new PropertiesConfig(), name);
    }

    /**
     * Create a Java Properties config from a file.<br/>
     * It names will be the file name without the extension.
     *
     * @param file The path of the config file
     *
     * @return A {@link PropertiesConfig} of this file.
     */
    public PropertiesConfig properties(File file)
    {
        return properties(file, file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    /**
     * Create a Java Properties config from a file with the given name
     *
     * @param file The path of the config file
     * @param name The name of the config
     *
     * @return A new {@link JSONConfig}
     */
    public PropertiesConfig properties(File file, String name)
    {
        return register(new PropertiesConfig(file), name);
    }

    /**
     * Register a file config
     *
     * @param config The config to register
     * @param name The name of the config
     * @param <T> The config type
     *
     * @return The given config
     */
    public <T extends FileConfig> T register(T config, String name)
    {
        LOGGER.info("Loaded config -> " + name + " (" + config.getFile().getAbsolutePath() + ")");
        return (T) register((Config) config, name);
    }

    /**
     * Register a config
     *
     * @param config The config to register
     * @param name The name of the config
     * @param <T> The config type
     *
     * @return The given config
     */
    public <T extends Config> T register(T config, String name)
    {
        configs.put(name, config);
        return config;
    }

    /**
     * Get a registered config
     *
     * @param name The config name
     *
     * @return The config, or null if not found
     */
    @Nullable
    public Config get(String name)
    {
        return configs.get(name);
    }

    /**
     * Finds a value with the given path.<br/><br/>
     *
     * <b>Example :</b>
     *
     * myconfig.json =&gt;
     * <pre>
     * {
     *     "object": {
     *         "key": "value"
     *     }
     * }
     * </pre>
     * Registered with provider.json("myconfig.json");<br/>
     *     =&gt; provider.at("myconfig.object.key") returns "value"
     *
     * @param path The path of the value to get (example config.object.key)
     *
     * @return The value at the given path
     */
    public String at(String path)
    {
        return at(path, (String) null);
    }

    /**
     * Finds a value with the given path.<br/><br/>
     *
     * <b>Example :</b>
     *
     * myconfig.json =&gt;
     * <pre>
     * {
     *     "object": {
     *         "key": "value"
     *     }
     * }
     * </pre>
     * Registered with provider.json("myconfig.json");<br/>
     *     =&gt; provider.at("myconfig.object.key") returns "value"
     *
     * @param path The path of the value to get (example config.object.key)
     * @param def The default value to return if not found
     *
     * @return The value at the given path or def if not found
     */
    public String at(String path, String def)
    {
        return at(path, def, String.class);
    }

    /**
     * Finds a value with the given path.<br/>
     *
     * <b>If the config does not support the features (by example it
     * does not support objects) it just calls {@link Config#get(String, String)}</b></b></b>
     *
     * <b>Example :</b>
     *
     * <pre>
     * {
     *     "object": {
     *         "key": "value"
     *     }
     * }
     * </pre>
     * as "myconfig"<br/>
     *     =&gt; provider.at("myconfig.object.key") returns "value"
     *
     * @param path The path of the value to get (example config.object.key)
     * @param type The type of the object to return
     *
     * @param <T> The type of the object
     *
     * @return The value at the given path
     */
    public <T> T at(String path, Class<T> type)
    {
        return at(path, null, type);
    }


    /**
     * Finds a value with the given path.<br/><br/>
     *
     * <b>If the config does not support the features (by example it
     * does not support objects) it just calls {@link Config#get(String, String)}</b><br/><br/>
     *
     * <b>Example :</b>
     *
     * <pre>
     * {
     *     "object": {
     *         "key": "value"
     *     }
     * }
     * </pre>
     * as "myconfig"<br/>
     *     =&gt; provider.at("myconfig.object.key") returns "value"
     *
     * @param path The path of the value to get (example config.object.key)
     * @param def The default value if not found
     * @param type The type of the object to return
     *
     * @param <T> The type of the object
     *
     * @return The value at the given path or the default if not found
     */
    public <T> T at(String path, T def, Class<T> type)
    {
        int index = path.indexOf(".");
        Config config = get(path.substring(0, index));

        return config == null ? null : config.at(path.substring(index + 1), def, type);
    }
}
