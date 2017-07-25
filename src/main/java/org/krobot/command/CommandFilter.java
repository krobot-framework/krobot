package org.krobot.command;

import java.util.Map;

public abstract class CommandFilter
{
    public abstract boolean filter(Command command,
                                   CommandContext context,
                                   Map<String, SuppliedArgument> args);
}
