package fr.litarvan.krobot.command;

@FunctionalInterface
public interface Middleware
{
    boolean handle(Command command, CommandContext context);
}
