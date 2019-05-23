package org.krobot.console;

import org.krobot.command.CommandArgument;

public class ComputedConsoleCommand
{
    private String label;
    private CommandArgument[] arguments;
    private ConsoleCommand command;

    public ComputedConsoleCommand(String label, CommandArgument[] arguments, ConsoleCommand command)
    {
        this.label = label;
        this.arguments = arguments;
        this.command = command;
    }

    public String getLabel()
    {
        return label;
    }

    public CommandArgument[] getArguments()
    {
        return arguments;
    }

    public ConsoleCommand getCommand()
    {
        return command;
    }
}
