package org.krobot.command;

import java.util.Map;
import org.krobot.command.runtime.CommandCall;
import org.krobot.command.runtime.MessageContext;
import org.krobot.command.runtime.SuppliedArgument;

public abstract class CommandFilter
{
    public abstract void filter(CommandCall command,
                                   MessageContext context,
                                   Map<String, SuppliedArgument> args);
}
