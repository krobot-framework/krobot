package org.krobot.module;

import org.krobot.command.runtime.MessageContext;

@FunctionalInterface
public interface Filter
{
    boolean filter(MessageContext context);
}
