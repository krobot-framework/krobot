package org.krobot.module;

import org.krobot.command.MessageContext;

@FunctionalInterface
public interface Handler
{
    void handle(MessageContext context);
}
