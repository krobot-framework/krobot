package org.krobot.module;

import org.krobot.command.runtime.MessageContext;

@FunctionalInterface
public interface Handler
{
    void handle(MessageContext context);
}
