package org.krobot.command;

public abstract class CommandFilter
{
    public abstract void filter(CommandCall command,
                                MessageContext context,
                                ArgumentMap args);
}
