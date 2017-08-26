package org.krobot.module;

import org.krobot.command.MessageContext;

@FunctionalInterface
public interface Filter
{
    boolean filter(MessageContext context);
}
