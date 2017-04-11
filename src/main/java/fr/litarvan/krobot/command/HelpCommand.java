package fr.litarvan.krobot.command;

import fr.litarvan.krobot.util.Dialog;
import fr.litarvan.krobot.util.Markdown;
import java.util.Map;
import javax.inject.Inject;

public class HelpCommand implements CommandHandler
{
    @Inject
    private CommandManager commandManager;

    @Override
    public void handle(CommandContext context, Map<String, SuppliedArgument> args)
    {
        StringBuilder message = new StringBuilder();

        for (Command command : commandManager.getCommands())
        {
            message.append(toString("", command)).append("\n\n");
        }

        context.getChannel().sendMessage(Dialog.info(Markdown.underline("List of commands :"), message.toString())).queue();
    }

    private String toString(String prefix, Command command)
    {
        StringBuilder string = new StringBuilder();

        String str = command.toString("", false);
        String label = command.getLabel();

        string.append(prefix).append(Markdown.bold(label)).append(str.substring(label.length())).append("\n");

        if (command.getDescription() != null)
        {
            if (!prefix.isEmpty())
            {
                string.append(prefix.substring(0, prefix.indexOf(Markdown.BOLD_MODIFIER)));
            }

            string.append("> ").append(Markdown.italic(command.getDescription()));
        }

        if (command.getSubs() != null && !command.getSubs().isEmpty())
        {
            for (Command sub : command.getSubs())
            {
                string.append("\n");
                string.append(toString("|--- " + prefix + Markdown.bold(label) + " ", sub));
            }
        }

        return string.toString();
    }
}
