package org.krobot.command;

import org.krobot.MessageContext;

@FunctionalInterface
public interface CommandFilter
{
    void filter(CommandCall command, MessageContext context, ArgumentMap args);
}
