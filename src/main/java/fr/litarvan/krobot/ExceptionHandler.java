package fr.litarvan.krobot;

import fr.litarvan.krobot.command.Command;
import fr.litarvan.krobot.command.CommandContext;

public class ExceptionHandler
{
    public void handle(Throwable throwable, Command command, CommandContext context)
    {
        throwable.printStackTrace(); // TODO: REAL HANDLER
    }
}
