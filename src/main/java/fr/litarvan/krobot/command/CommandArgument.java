package fr.litarvan.krobot.command;

import fr.litarvan.krobot.command.ArgumentType;

public class CommandArgument
{
    private String key;
    private boolean optional;
    private boolean list;
    private String[] choices;
    private ArgumentType type;

    public CommandArgument(String key, boolean optional, boolean list, ArgumentType type)
    {
        this.key = key;
        this.optional = optional;
        this.list = list;
        this.type = type;
    }

    public CommandArgument(String key, boolean optional, String[] choices)
    {
        this.key = key;
        this.optional = optional;
        this.list = false;
        this.choices = choices;
        this.type = ArgumentType.STRING;
    }

    public String getKey()
    {
        return key;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public boolean isList()
    {
        return list;
    }

    public String[] getChoices()
    {
        return choices;
    }

    public ArgumentType getType()
    {
        return type;
    }
}
