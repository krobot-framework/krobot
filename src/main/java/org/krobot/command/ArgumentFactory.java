package org.krobot.command;

public interface ArgumentFactory<T>
{
    T process(String argument) throws BadArgumentTypeException;
    T[] createArray();
}
