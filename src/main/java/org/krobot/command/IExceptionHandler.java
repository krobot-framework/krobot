package org.krobot.command;

import org.krobot.MessageContext;

@FunctionalInterface
public interface IExceptionHandler<T extends Throwable>
{
    void handle(MessageContext context, T t);
}
