package org.krobot.command;

import org.jetbrains.annotations.NotNull;
import org.krobot.util.Dialog;
import org.krobot.util.Markdown;
import org.krobot.util.MessageUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * @version 2.1.2
 * @since 2.0.0
 */
public class HelpCommand implements CommandHandler
{
    @Inject
    private CommandManager commandManager;

    @Override
    public void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args)
    {
        StringBuilder curMessage = new StringBuilder();

        List<StringBuilder> messages = new ArrayList<>();
        messages.add(curMessage);


        for (Command command : commandManager.getCommands())
        {
            String cmdStr = toString("", command);

            if(curMessage.length() + cmdStr.length() + 4 > MessageUtils.MAX_MESSAGE_CHARS)
            {
                curMessage = new StringBuilder();
                messages.add(curMessage);
            }

            curMessage.append(cmdStr).append("\n\n");
        }

        context.sendMessage(Dialog.info(Markdown.underline("List of commands :"), messages.get(0).toString()));

        for(int i = 1; i < messages.size(); i++)
        {
            context.sendMessage(Dialog.info(null, messages.get(i).toString()));
        }
    }

    private String toString(String prefix, Command command)
    {
        StringBuilder string = new StringBuilder();

        String str = command.toString("", false);
        String label = command.getLabel();

        string.append(prefix.replace("└", "├")).append(Markdown.bold(label)).append(str.substring(label.length())).append("\n");

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
