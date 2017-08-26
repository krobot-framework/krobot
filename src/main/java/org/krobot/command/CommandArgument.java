package org.krobot.command;

public class CommandArgument
{
    private boolean required;
    private String key;
    private ArgumentFactory factory;
    private boolean list;

    public CommandArgument(boolean required, String key, ArgumentFactory factory, boolean list)
    {
        this.required = required;
        this.key = key;
        this.factory = factory;
        this.list = list;
    }

    public boolean isRequired()
    {
        return required;
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
