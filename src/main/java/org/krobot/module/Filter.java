package org.krobot.module;

import org.krobot.MessageContext;

@FunctionalInterface
public interface Filter
{
    boolean filter(MessageContext context);
}
