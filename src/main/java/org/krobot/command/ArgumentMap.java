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
package org.krobot.command;

import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

public class ArgumentMap
{
    private Map<String, Object> args;

    public ArgumentMap(Map<String, Object> args)
    {
        this.args = args;
    }

    public boolean has(String key)
    {
        return args.containsKey(key);
    }

    public <T> T get(String key, Class<T> type)
    {
        return (T) get(key);
    }

    public <T> T get(String key)
    {
        Object val = args.get(key);

        if (val == null)
        {
            return null;
        }

        if (val.getClass().isArray() && ClassUtils.isPrimitiveWrapper(val.getClass().getComponentType()))
        {
            val = ArrayUtils.toPrimitive(val);
        }

        return (T) val;
    }

    public int count()
    {
        return args.size();
    }
}
