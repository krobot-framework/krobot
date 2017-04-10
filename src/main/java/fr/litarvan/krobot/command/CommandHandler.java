package fr.litarvan.krobot.command;

import java.util.Map;

@FunctionalInterface
public interface CommandHandler
{
    void handle(CommandContext context, Map<String, SuppliedArgument> args);
}
