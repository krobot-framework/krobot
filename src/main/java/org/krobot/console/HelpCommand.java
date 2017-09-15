package org.krobot.console;

import org.fusesource.jansi.Ansi;
import org.krobot.command.ArgumentMap;
import org.krobot.command.CommandArgument;

public class HelpCommand extends ConsoleCommand
{
    private KrobotConsole console;

    public HelpCommand(KrobotConsole console)
    {
        this.console = console;
    }

    @Override
    public String getPath()
    {
        return "help";
    }

    @Override
    public String getDescription()
    {
        return "Displays a detailed list of commands";
    }

    @Override
    public void execute(ArgumentMap args)
    {
        Ansi result = Ansi.ansi().fgYellow().bold().a("\nList of commands :\n").reset();

        console.getCommands().forEach(command -> {
            result.a("    ").bold().fgBlue().a(command.getLabel()).reset().a(" ");

            for (CommandArgument arg : command.getArguments())
            {
                result.fgCyan().a(arg.toString()).reset().a(" ");
            }

            result.a("\n        ").fgBrightGreen().a(command.getCommand().getDescription()).reset().a("\n");
        });

        System.out.println(result.toString());
    }
}
