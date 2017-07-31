package org.krobot.module;

import org.krobot.command.CommandContext;

@FunctionalInterface
public interface Handler
{
    void handle(CommandContext context);
}
