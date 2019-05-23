package org.krobot.console;

import org.krobot.command.ArgumentMap;

public abstract class ConsoleCommand
{
    public abstract String getPath();
    public abstract String getDescription();
    public abstract void execute(ArgumentMap args);
}
