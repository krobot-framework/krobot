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

import org.jetbrains.annotations.Nullable;

/**
 * A Config
 *
 *
 * An object that contains objects/strings that can be
 * retrieved or set.
 *
 * @author Litarvan
 * @version 2.0.0
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
    Object get(String key, Object def);

    /**
     * Set a value of the config
     *
     * @param key The key of the value to set
     * @param value The value to set
     */
    void set(String key, Object value);

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
     * does not support objects) it just calls {@link #get(String, Object)}</b>
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
    default Object at(String path, Object def)
    {
        return get(path, def);
    }

    /**
     * Finds a value with the given path.
     * <b>If the config does not support the features (by example it
     * does not support objects) it just calls {@link #get(String, Object)}</b>
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
     *
     * @return The value at the given path or null if not found
     */
    @Nullable
    default Object at(String path)
    {
        return at(path, null);
    }

    /**
     * @return If the config supports containing objects
     */
    boolean areObjectsSupported();

    /**
     * @return If the config supports saving
     */
    boolean isSavingSupported();
}
