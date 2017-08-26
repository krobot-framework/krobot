package org.krobot.config;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class ConfigProvider
{
    private Map<String, Config> configs;

    public ConfigProvider()
    {
        this.configs = new HashMap<>();
    }

    public void register(String name, Config config)
    {
        configs.put(name, config);
    }

    public Config get(String name)
    {
        return configs.get(name);
    }

    /**
     * Set a value of the config
     *
     * @param key The key of the value to set
     * @param value The value to set
     */
    public void set(String key, Object value)
    {
        int index = key.indexOf(".");
        Config config = get(key.substring(0, index));

        if (config != null)
        {
            config.set(key.substring(index + 1), value);
        }
    }

    /**
     * Finds a value with the given path.<br><br>
     *
     * <b>If the config does not support the features (by example it
     * does not support objects) it just calls {@link #get(String, String)}</b><br><br>
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
     *
     * config.at("object.key") returns "value"
     *
     * @param path The path of the value to get (example config.object.key)
     *
     * @return The value at the given path or null if not found
     */
    @Nullable
    public String at(String path)
    {
        return at(path, (String) null);
    }

    /**
     * Finds a value with the given path.
     * <b>If the config does not support the features (by example it
     * does not support objects) it just calls {@link #get(String, String)}</b>
     *
     * Example :
     *
     * <pre>
     * {
     *     "object": {
     *         "key": "value"
     *     }
     * }
     * </pre>
     *
     * config.at("object.key") returns "value"
     *
     * @param path The path of the value to get (example config.object.key)
     * @param def The default value if not found
     *
     * @return The value at the given path or the default if not found
     */
    public String at(String path, String def)
    {
        return at(path, def, String.class);
    }

    /**
     * Finds a value with the given path.<br><br>
     *
     * <b>If the config does not support the features (by example it
     * does not support objects) it just calls {@link #get(String, String)}</b><br><br>
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
     *
     * config.at("object.key") returns "value"
     *
     * @param path The path of the value to get (example config.object.key)
     * @param type The type of the object to return
     *
     * @param <T> The type of the object
     *
     * @return The value at the given path or the default if not found
     */
    public <T> T at(String path, Class<T> type)
    {
        return at(path, null, type);
    }

    /**
     * Finds a value with the given path.<br><br>
     *
     * <b>If the config does not support the features (by example it
     * does not support objects) it just calls {@link #get(String, String)}</b><br><br>
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
     *
     * config.at("object.key") returns "value"
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

    public boolean has(String name)
    {
        return configs.containsKey(name);
    }
}
