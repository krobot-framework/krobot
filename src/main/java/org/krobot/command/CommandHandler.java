package org.krobot.command;

import org.krobot.MessageContext;

@FunctionalInterface
public interface CommandHandler
{
    Object handle(MessageContext context, ArgumentMap args) throws Exception;
}
