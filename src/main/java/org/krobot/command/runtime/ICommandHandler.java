package org.krobot.command.runtime;

import java.util.Map;

@FunctionalInterface
public interface ICommandHandler
{
    Object handle(MessageContext context, Map<String, SuppliedArgument> args);
}
