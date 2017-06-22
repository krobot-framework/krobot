/*
 * Copyright 2017 The Krobot Contributors
 *
 * This file is part of Krobot.
 *
 * Krobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krobot.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.krobot;

import org.krobot.command.BadSyntaxException;
import org.krobot.command.Command;
import org.krobot.command.CommandContext;
import org.krobot.command.UserNotFoundException;
import org.krobot.util.Dialog;
import org.krobot.util.Markdown;
import org.krobot.util.UserUtils;
import java.util.Date;
import java.util.List;
import javax.inject.Singleton;
import net.dv8tion.jda.core.entities.PrivateChannel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.krobot.util.MessageUtils;

/**
 * The Exception Handler<br><br>
 *
 *
 * Handle the exceptions thrown during a command handling.<br>
 * By default, it displays an error {@link Dialog}, and send a
 * complete crash report to the user that triggered the command.<br><br>
 *
 * You can override it using Guice binding in a module.
 *
 * @author Litarvan
 * @version 2.1.0
 * @since 2.0.0
 */
@Singleton
public class ExceptionHandler
{
    private static final Logger LOGGER = LogManager.getLogger("ExceptionHandler");

    /**
     * Handle a command exception
     *
     * @param throwable The exception thrown
     * @param command The command that was being handled
     * @param args The arguments given to the command
     * @param context The command context
     */
    public void handle(@NotNull Throwable throwable, @NotNull Command command, @NotNull List<String> args, @NotNull CommandContext context)
    {
        if (throwable instanceof BadSyntaxException)
        {
            context.sendMessage(Dialog.warn("Bad command syntax", Markdown.underline("Syntax :") + " " + command.toString("", false) + (throwable.getMessage() != null ? "\n\n" + throwable.getMessage() : "")));
            return;
        }
        else if (throwable instanceof UserNotFoundException)
        {
            context.sendMessage(Dialog.warn("Unknown user", "Can't find user '" + ((UserNotFoundException) throwable).getUser() + "'"));
            return;
        }

        LOGGER.error("Exception while executing the command : " + command.toString("", false), throwable);

        String report = makeCrashReport(throwable, command, args, context);

        context.sendMessage(Dialog.error("Command crashed !", "A crash report has been sent to you " + context.getUser().getAsMention() + " . Please send it to the developer as soon as possible !"));

        PrivateChannel channel = context.getUser().openPrivateChannel().complete();
        for (String message : MessageUtils.splitMessage(report, MessageUtils.MAX_MESSAGE_CHARS - Markdown.code("").length()))
        {
            channel.sendMessage(Markdown.code(message)).queue();
        }
    }

    /**
     * Create a crash report based on an Exception
     *
     * @param throwable The command of the report
     * @param command The command that was being handled when the exception was thrown
     * @param args The arguments of the command
     * @param context The context of the command
     *
     * @return The generated crash report
     */
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
