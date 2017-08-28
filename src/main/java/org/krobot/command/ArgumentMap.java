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

    public <T> T get(String key, Class<T> type)
    {
        return (T) get(key);
    }

    public <T> T get(String key)
    {
        Object val = args.get(key);

        if (val.getClass().isArray() && ClassUtils.isPrimitiveWrapper(val.getClass().getComponentType()))
        {
            val = ArrayUtils.toPrimitive(val);
        }

        return (T) val;
    }
}
