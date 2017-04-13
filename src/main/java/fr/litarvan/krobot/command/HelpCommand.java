package fr.litarvan.krobot.command;

import fr.litarvan.krobot.util.Dialog;
import fr.litarvan.krobot.util.Markdown;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

/**
 * The Help Command<br><br>
 *
 *
 * An automatic help command.<br>
 * It uses an info Dialog and displays all the commands with
 * their syntax, their descriptions, and their subcommands
 * recursively.
 *
 * @author Litarvan
 * @version 2.1.0
 * @since 2.0.0
 */
public class HelpCommand implements CommandHandler
{
    @Inject
    private CommandManager commandManager;

    @Override
    public void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args)
    {
        StringBuilder message = new StringBuilder();

        for (Command command : commandManager.getCommands())
        {
            message.append(toString("", command)).append("\n\n");
        }

        context.sendMessage(Dialog.info(Markdown.underline("List of commands :"), message.toString()));
    }

    private String toString(String prefix, Command command)
    {
        StringBuilder string = new StringBuilder();

        String str = command.toString("", false);
        String label = command.getLabel();

        string.append(prefix.replace('└', '├')).append(Markdown.bold(label)).append(str.substring(label.length())).append("\n");

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
            for (int i = 0; i < command.getSubs().size(); i++)
            {
                if (!(command.getDescription() == null && i == 0))
                {
                    string.append("\n");
                }

                string.append(toString((i == command.getSubs().size() - 1 ? "└" : "├") + "── " + prefix + Markdown.bold(label) + " ", command.getSubs().get(i)));
            }
        }

        return string.toString();
    }
}
