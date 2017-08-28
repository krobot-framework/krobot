package org.krobot.module;

import org.krobot.MessageContext;

@FunctionalInterface
public interface Handler
{
    void handle(MessageContext context);
}
