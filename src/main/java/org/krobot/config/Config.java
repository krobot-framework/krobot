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

import java.io.File;
import java.net.URISyntaxException;
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
     * @param def The default value if not found
     *
     * @return The found value or the default one if not found
     */
    String get(String key, String def);

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
    default <T> T get(String key, T def, Class<T> type)
    {
        throw new UnsupportedOperationException("This config does not support object serializing");
    }

    /**
     * Set a value of the config
     *
     * @param key The key of the value to set
     * @param value The value to set
     */
    void set(String key, String value);

    /**
     * Set a value of the config
     *
     * @param key The key of the value to set
     * @param value The value to set
     */
    default void set(String key, Object value)
    {
        throw new UnsupportedOperationException("This config does not support object serializing");
    }

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
    default <T> T at(String path, T def, Class<T> type)
    {
        return get(path, def, type);
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
    default String at(String path)
    {
        return at(path, (String) null);
    }

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

    /**
     * @return If the config supports containing objects
     */
    boolean areObjectsSupported();

    /**
     * @return If the config supports saving
     */
    boolean isSavingSupported();

    /**
     * Create a file object from a classpath resource
     *
     * @param path The path of the resource
     *
     * @return The file
     */
    static File resource(String path)
    {
        try
        {
            return new File(Config.class.getResource("/" + (path.startsWith("/") ? path.substring(1) : path)).toURI());
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Malformed URI from file (shouldn't happen)", e);
        }
    }
}
