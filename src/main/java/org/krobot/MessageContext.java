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

import java.util.concurrent.Future;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.krobot.permission.BotNotAllowedException;
import org.krobot.permission.UserNotAllowedException;
import org.krobot.util.Dialog;

/**
 * The Command Context<br><br>
 *
 *
 * The context where the command was called.<br>
 * It contains the user that called the command, the message
 * of the command, and the channel where it was called.
 *
 * @author Litarvan
 * @version 2.1.1
 * @since 2.0.0
 */
public class MessageContext
{
    private JDA jda;
    private User user;
    private Message message;
    private TextChannel channel;

    /**
     * The command Context
     *
     * @param user The user that called the command
     * @param message The command message
     * @param channel The channel where the command was called
     */
    public MessageContext(JDA jda, User user, Message message, TextChannel channel)
    {
        this.jda = jda;
        this.user = user;
        this.message = message;
        this.channel = channel;
    }

    /**
     * Check for a <b>bot</b> required permission<br>
     * Throws a {@link BotNotAllowedException} if it hasn't (by default,
     * it will be caught by the ExceptionHandler to print a specific message).
     *
     * @param permission The permission to check
     *
     * @throws BotNotAllowedException If the bot hasn't the permission
     */
    public void require(Permission permission) throws BotNotAllowedException
    {
        if (!this.getGuild().getMember(jda.getSelfUser()).hasPermission(permission))
        {
            throw new BotNotAllowedException(permission);
        }
    }

    /**
     * Check for a <b>user</b> required permission<br>
     * Throws a {@link UserNotAllowedException} if it hasn't (by default,
     * it will be caught by the ExceptionHandler to print a specific message).
     *
     * @param permission The permission to check
     *
     * @throws UserNotAllowedException If the user hasn't the permission
     */
    public void requireCaller(Permission permission) throws UserNotAllowedException
    {
        if (!this.getMember().hasPermission(permission))
        {
            throw new UserNotAllowedException(permission);
        }
    }

    /**
     * Send a message on the context channel
     *
     * @param content The message content
     *
     * @return A Future representing the task result
     */
    public Future<Message> send(String content)
    {
        return channel.sendMessage(content).submit();
    }

    /**
     * Send a formatted message on the context channel
     *
     * @param content The message content (will be formated by {@link String#format(String, Object...)}
     * @param args The args for the format
     *
     * @return A Future representing the task result
     */
    public Future<Message> send(String content, Object... args)
    {
        return channel.sendMessage(String.format(content, args)).submit();
    }

    /**
     * Send an embed message on the context channel
     *
     * @param content The message content
     *
     * @return A Future representing the task result
     */
    public Future<Message> send(MessageEmbed content)
    {
        return channel.sendMessage(content).submit();
    }

    /**
     * Send an embed message on the context channel
     *
     * @param content The message content (will be built)
     *
     * @return A Future representing  task result
     */
    public Future<Message> send(EmbedBuilder content)
    {
        return send(content.build());
    }

    public Future<Message> info(String title, String message)
    {
        return send(Dialog.info(title, message));
    }

    public Future<Message> warn(String title, String message)
    {
        return send(Dialog.warn(title, message));
    }

    public Future<Message> error(String title, String message)
    {
        return send(Dialog.error(title, message));
    }

    public boolean hasPermission(Permission... permissions)
    {
        return getMember().hasPermission(getChannel(), permissions);
    }

    public boolean botHasPermission(Permission... permissions)
    {
        return getBotMember().hasPermission(getChannel(), permissions);
    }

    /**
     * @return Return the caller user as a mention
     */
    public String mentionCaller()
    {
        return this.getUser().getAsMention();
    }

    /**
     * @return The guild where the command was called
     */
    public Guild getGuild()
    {
        return this.getChannel().getGuild();
    }

    public Member getBotMember()
    {
        return this.getGuild().getMember(jda.getSelfUser());
    }

    /**
     * @return The guild member that called the command
     */
    public Member getMember()
    {
        return this.getGuild().getMember(this.getUser());
    }

    /**
     * @return The user that called the command
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @return The command message
     */
    public Message getMessage()
    {
        return message;
    }

    /**
     * @return The channel where the command was called
     */
    public TextChannel getChannel()
    {
        return channel;
    }
}
