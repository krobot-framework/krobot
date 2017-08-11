package org.krobot.module;

import org.krobot.command.runtime.CommandContext;

@FunctionalInterface
public interface Filter
{
    boolean filter(CommandContext context);
}
