package org.krobot.module;

import org.krobot.command.runtime.CommandContext;

@FunctionalInterface
public interface Handler
{
    void handle(CommandContext context);
}
