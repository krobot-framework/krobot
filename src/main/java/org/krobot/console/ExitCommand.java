package org.krobot.console;

import org.krobot.command.ArgumentMap;
import org.krobot.runtime.KrobotRuntime;

public class ExitCommand extends ConsoleCommand
{
    @Override
    public String getPath()
    {
        return "exit";
    }

    @Override
    public String getDescription()
    {
        return "Shut down the bot";
    }

    @Override
    public void execute(ArgumentMap args)
    {
        KrobotRuntime.stop();
        System.exit(0);
    }
}
