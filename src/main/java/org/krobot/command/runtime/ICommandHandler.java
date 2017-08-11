package org.krobot.command.runtime;

import java.util.Map;
import org.krobot.command.runtime.CommandContext;
import org.krobot.command.runtime.SuppliedArgument;

@FunctionalInterface
public interface ICommandHandler
{
    Object handle(CommandContext context, Map<String, SuppliedArgument> args);
}
