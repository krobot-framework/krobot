package org.krobot.command;

import org.krobot.command.runtime.ArgumentMap;
import org.krobot.command.runtime.CommandCall;
import org.krobot.command.runtime.MessageContext;

public abstract class CommandFilter
{
    public abstract void filter(CommandCall command,
                                MessageContext context,
                                ArgumentMap args);
}
