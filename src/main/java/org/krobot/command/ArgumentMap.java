package org.krobot.command;

import java.util.Map;

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
        return (T) args.get(key);
    }
}
