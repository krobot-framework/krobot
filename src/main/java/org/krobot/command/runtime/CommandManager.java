package org.krobot.command.runtime;

import java.util.ArrayList;
import java.util.List;
import org.krobot.command.CommandFilter;
import org.krobot.command.KrobotCommand;

public class CommandManager
{
    private List<KrobotCommand> commands;
    private List<CommandFilter> filters;

    public CommandManager()
    {
        this.commands = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    public List<KrobotCommand> getCommands()
    {
        return commands;
    }

    public List<CommandFilter> getFilters()
    {
        return filters;
    }
}
