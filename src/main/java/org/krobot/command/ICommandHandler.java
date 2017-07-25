package org.krobot.command;

import java.util.Map;

@FunctionalInterface
public interface ICommandHandler
{
    Object handle(CommandContext context, Map<String, SuppliedArgument> args);
}
