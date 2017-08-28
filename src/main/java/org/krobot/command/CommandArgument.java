package org.krobot.command;

public class CommandArgument
{
    private boolean required;
    private String key;
    private String type;
    private ArgumentFactory factory;
    private boolean list;

    public CommandArgument(boolean required, String key, String type, ArgumentFactory factory, boolean list)
    {
        this.required = required;
        this.key = key;
        this.type = type;
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

    public String getType()
    {
        return type;
    }

    public ArgumentFactory getFactory()
    {
        return factory;
    }

    public boolean isList()
    {
        return list;
    }

    @Override
    public String toString()
    {
        return (isRequired() ? "<" : "[") + getKey() + ":" + getType() + (isRequired() ? ">" : "]");
    }
}
