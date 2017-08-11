package org.krobot.command;

import org.krobot.command.runtime.ArgumentFactory;

public class CommandArgument
{
    private String key;
    private ArgumentFactory factory;
    private boolean list;

    public CommandArgument(String key, ArgumentFactory factory, boolean list)
    {
        this.key = key;
        this.factory = factory;
        this.list = list;
    }

    public String getKey()
    {
        return key;
    }

    public ArgumentFactory getFactory()
    {
        return factory;
    }

    public boolean isList()
    {
        return list;
    }
}
