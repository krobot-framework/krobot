package org.krobot.command;

public @interface Filters
{
    Class<? extends CommandFilter>[] value();
}
