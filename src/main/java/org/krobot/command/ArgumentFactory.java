package org.krobot.command;

@FunctionalInterface
public interface ArgumentFactory<T>
{
    T process(String argument) throws BadArgumentTypeException;
}
