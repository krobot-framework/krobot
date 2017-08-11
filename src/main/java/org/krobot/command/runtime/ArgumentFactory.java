package org.krobot.command.runtime;

@FunctionalInterface
public interface ArgumentFactory<T>
{
    T process(String argument);
}
