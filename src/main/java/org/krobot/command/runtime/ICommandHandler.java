package org.krobot.command.runtime;


@FunctionalInterface
public interface ICommandHandler
{
    Object handle(MessageContext context, ArgumentMap args) throws Exception;
}
