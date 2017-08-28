package org.krobot.command;

import org.krobot.MessageContext;
import org.krobot.runtime.KrobotRuntime;
import org.krobot.util.Markdown;
import org.krobot.util.MessageUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
 * @version 2.2.0
 * @since 2.0.0
 */
@Command(value = "help", desc = "Displays the list of commands with their descriptions")
public class HelpCommand implements CommandHandler
{
    private KrobotRuntime runtime;

    @Inject
    public HelpCommand(KrobotRuntime runtime)
    {
        this.runtime = runtime;
    }

    @Override
    public Object handle(MessageContext context, ArgumentMap args)
    {
        StringBuilder curMessage = new StringBuilder();

        List<StringBuilder> messages = new ArrayList<>();
        messages.add(curMessage);

        String prefix = runtime.getFilterRunner().getPrefix(context);

        if (prefix == null)
        {
            prefix = "";
        }

        for (KrobotCommand command : runtime.getCommandManager().getCommands())
        {
            String cmdStr = prefix + toString("", command);

            if(curMessage.length() + cmdStr.length() + 4 > MessageUtils.MAX_MESSAGE_CHARS)
            {
                curMessage = new StringBuilder();
                messages.add(curMessage);
            }

            curMessage.append(cmdStr).append("\n\n");
        }

        context.info(Markdown.underline("List of commands :"), messages.get(0).toString());

        for(int i = 1; i < messages.size(); i++)
        {
            context.info(null, messages.get(i).toString());
        }

        return null;
    }

    private String toString(String prefix, KrobotCommand command)
    {
        StringBuilder string = new StringBuilder();

        String str = command.toString("", false);
        String label = command.getLabel();

        string.append(prefix.replace("└", "├")).append(Markdown.bold(label)).append(str.substring(label.length())).append("\n");

        if (command.getDescription() != null && !command.getDescription().trim().isEmpty())
        {
            if (!prefix.isEmpty())
            {
                string.append(prefix.substring(0, prefix.indexOf(Markdown.BOLD_MODIFIER)));
            }

            string.append("> ").append(Markdown.italic(command.getDescription()));
        }

        if (command.getSubCommands() != null && command.getSubCommands().length != 0)
        {
            for (int i = 0; i < command.getSubCommands().length; i++)
            {
                if (!(command.getDescription() == null && i == 0))
                {
                    string.append("\n");
                }

                string.append(toString((i == command.getSubCommands().length - 1 ? "└" : "├") + "── " + prefix + Markdown.bold(label) + " ", command.getSubCommands()[i]));
            }
        }

        return string.toString();
    }
}
