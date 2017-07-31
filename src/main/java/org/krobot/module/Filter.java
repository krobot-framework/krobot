package org.krobot.module;

import org.krobot.command.CommandContext;

@FunctionalInterface
public interface Filter
{
    boolean filter(CommandContext context);
}
