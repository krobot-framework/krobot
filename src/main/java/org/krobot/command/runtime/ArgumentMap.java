package org.krobot.command.runtime;

import java.util.Map;

public class ArgumentMap
{
    private Map<String, Object> args;

    public ArgumentMap(Map<String, Object> args)
    {
        this.args = args;
    }

    public String asString(String key)
    {
        return get(key, String.class);
    }

    public int asInt(String key)
    {
        return get(key);
    }

    public long asLong(String key)
    {
        return get(key);
    }

    public float asFloat(String key)
    {
        return get(key);
    }

    public double asDouble(String key)
    {
        return get(key);
    }

    public <T> T get(String key)
    {
        return (T) args.get(key);
    }

    public <T> T get(String key, Class<T> type)
    {
         return (T) get(key);
    }
}
