package org.krobot.command;

import java.util.Map;
import org.krobot.command.runtime.CommandContext;
import org.krobot.command.runtime.SuppliedArgument;

public abstract class CommandFilter
{
    public abstract boolean filter(KrobotCommand command,
                                   CommandContext context,
                                   Map<String, SuppliedArgument> args);
}
