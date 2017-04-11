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
            String str = command.toString();
            String label = command.getLabel();

            message.append(Markdown.BOLD_MODIFIER).append(label).append(Markdown.BOLD_MODIFIER)
                   .append(str.substring(label.length())).append("\n\n");
        }

        context.getChannel().sendMessage(Dialog.info("List of commands", message.toString())).queue();
    }
}
