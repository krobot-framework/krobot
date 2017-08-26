package org.krobot.command;

@FunctionalInterface
public interface ICommandHandler
{
    Object handle(MessageContext context, ArgumentMap args) throws Exception;
}
