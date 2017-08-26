package org.krobot.config;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

/**
 * A Config
 *
 *
 * An object that contains objects/strings that can be
 * retrieved or set.
 *
 * @author Litarvan
 * @version 2.2.0
 * @since 2.0.0
 */
public interface Config
{
    /**
     * Get a value of the config
     *
     * @param key The key of the value
     *
     * @return The found value or null
     */
    @Nullable
    default Object get(String key)
    {
        return get(key, null);
    }

    /**
     * Get a value of the config
     *
     * @param key The key of the value
     * @param def The default value if not found
     *
     * @return The found value or the default one if not found
     */
    default String get(String key, String def)
    {
        return get(key, def, String.class);
    }

    /**
     * Get a value of the config as an object
     *
     * @param key The key of the value
     * @param def The default value if not found
     * @param type The type of the object to return
     *
     * @param <T> The type of the object
     *
     * @return The found value or the default one if not found
     */
    <T> T get(String key, T def, Class<T> type);

    /**
     * Set a value of the config
     *
     * @param key The key of the value to set
     * @param value The value to set
     */
    void set(String key, Object value);

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
    default String at(String path)
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
    default String at(String path, String def)
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
    default <T> T at(String path, Class<T> type)
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
    <T> T at(String path, T def, Class<T> type);

    /**
     * Append an object to an array of the config
     *
     * @param field The path (see {@link #at(String)}) of the array
     * @param classOfArray The class of the array
     * @param toAppend The object to append
     *
     * @param <T> The type of the object
     *
     * @return The new array
     */
    default <T> T[] append(String field, Class<T[]> classOfArray, T toAppend)
    {
        T[] array = ArrayUtils.add(at(field, classOfArray), toAppend);
        set(field, array);

        return array;
    }
}