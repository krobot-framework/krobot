package fr.litarvan.krobot;

import fr.litarvan.krobot.command.BadSyntaxException;
import fr.litarvan.krobot.command.Command;
import fr.litarvan.krobot.command.CommandContext;
import fr.litarvan.krobot.command.SuppliedArgument;
import fr.litarvan.krobot.command.UserNotFoundException;
import fr.litarvan.krobot.util.Dialog;
import fr.litarvan.krobot.util.Markdown;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.core.entities.PrivateChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import static fr.litarvan.krobot.util.Markdown.*;
import static fr.litarvan.krobot.util.MessageUtils.*;

public class ExceptionHandler
{
    private static final Logger LOGGER = LogManager.getLogger("ExceptionHandler");

    public void handle(Throwable throwable, Command command, List<String> args, CommandContext context)
    {
        if (throwable instanceof BadSyntaxException)
        {
            context.getChannel().sendMessage(Dialog.warn("Bad command syntax", Markdown.underline("Syntax :") + " " + command.toString("", false))).queue();
            return;
        }
        else if (throwable instanceof UserNotFoundException)
        {
            context.getChannel().sendMessage(Dialog.warn("Unknown user", "Can't find user '" + ((UserNotFoundException) throwable).getUser() + "'"));
        }

        LOGGER.error("Exception while executing " + (context == null ? "a command" : "the command : " + command.toString("", false)), throwable);

        if (context == null)
        {
            return;
        }

        String report = makeCrashReport(throwable, command, args, context);

        context.getChannel().sendMessage(Dialog.error("Command crashed !", "A crash report has been sent to you " + context.getUser().getAsMention() + " . Please send it to the developer as soon as possible !")).queue();

        PrivateChannel channel = privateChannel(context.getUser());
        for (String message : splitMessage(report, MAX_MESSAGE_CHARS - code("").length()))
        {
            channel.sendMessage(code(message)).queue();
        }
    }

    protected String makeCrashReport(Throwable throwable, Command command, List<String> args, CommandContext context)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("####################################\n\n");

        builder.append("Krobot v").append(Krobot.VERSION).append(" crash report\n\n");

        builder.append("Command       : ").append(command.toString("", false)).append("\n");
        builder.append("Args          : ").append(args).append("\n");
        builder.append("Caller        : ").append(context.getUser().getName()).append("#").append(context.getUser().getDiscriminator()).append("\n");
        builder.append("Conversation  : #").append(context.getChannel().getName()).append("\n");
        builder.append("Time          : ").append(new Date()).append("\n\n");

        builder.append(ExceptionUtils.getStackTrace(throwable)).append("\n\n");

        builder.append("####################################");

        return builder.toString();
    }
}
