package org.krobot.command;

@FunctionalInterface
public interface IExceptionHandler<T extends Throwable>
{
    void handle(MessageContext context, T t);
}
