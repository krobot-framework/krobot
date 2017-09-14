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
package org.krobot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Message Utils<br><br>
 *
 *
 * Message-related util functions.
 *
 * @author Litarvan
 * @version 2.3.0
 * @since 2.0.0
 */
public final class MessageUtils
{
    /**
     * Number of maximum character in a Discord message
     */
    public static final int MAX_MESSAGE_CHARS = 1999;

    private static ScheduledExecutorService deletePool = Executors.newSingleThreadScheduledExecutor();

    /**
     * Split a message in messages of at most {@link #MAX_MESSAGE_CHARS} characters
     *
     * @param message The message to split
     *
     * @return The splitted message
     */
    public static String[] splitMessage(@NotNull String message)
    {
        return splitMessage(message, MAX_MESSAGE_CHARS);
    }

    /**
     * Split a message in messages of at most a given amount of characters
     *
     * @param message The message to split
     * @param limit The messages max characters
     *
     * @return The splitted message
     */
    public static String[] splitMessage(@NotNull String message, int limit)
    {
        ArrayList<String> messages = new ArrayList<>();

        while (message.length() > limit)
        {
            messages.add(message.substring(0, limit));
            message = message.substring(limit, message.length());
        }

        messages.add(message);

        return messages.toArray(new String[messages.size()]);
    }

    /**
     * Split a message in messages of at most {@link #MAX_MESSAGE_CHARS} characters,
     * without breaking the message lines.
     *
     * @param message The message to split
     *
     * @return The splitted message
     */
    public static String[] splitMessageKeepLines(@NotNull String message)
    {
        return splitMessageKeepLines(message, MAX_MESSAGE_CHARS);
    }

    /**
     * Split a message in messages of at most a given amount of characters,
     * without breaking the message lines.
     *
     * @param message The message to split
     * @param limit The messages max characters
     *
     * @return The splitted message
     */
    public static String[] splitMessageKeepLines(@NotNull String message, int limit)
    {
        ArrayList<String> messages = new ArrayList<>();
        String[] lines = message.split("\n");
        StringBuilder current = new StringBuilder();

        for (String line : lines)
        {
            if (current.length() + line.length() > limit)
            {
                messages.add(current.toString());
                current = new StringBuilder();
            }

            current.append(line).append("\n");
        }

        messages.add(current.toString());

        return messages.toArray(new String[messages.size()]);
    }

    public static String[] splitWithQuotes(String string, boolean keepQuotes)
    {
        List<String> result = new ArrayList<>();
        String[] split = string.split(" ");

        for (int i = 0; i < split.length; i++)
        {
            StringBuilder current = new StringBuilder(split[i]);

            if (current.toString().startsWith("\""))
            {
                i++;

                while (i < split.length && !current.toString().endsWith("\""))
                {
                    current.append(" ").append(split[i]);
                    i++;
                }

                i--;
            }

            String done = current.toString();

            if (!done.endsWith("\""))
            {
                result.addAll(Arrays.asList(done.split(" ")));
            }
            else
            {
                result.add(keepQuotes ? done : done.replace("\"", ""));
            }
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * Get the most similar message of a list to a base<br><br>
     *
     * <b>Example:</b><br><br>
     *
     * base = hello<br>
     * messages = [haul, hella, yay]<br><br>
     *
     * It returns <b>hella</b>
     *
     * @param base The base message
     * @param messages The messages where to get the most similar
     *
     * @return The most similar message to the base
     */
    public static String getMostSimilar(String base, String[] messages)
    {
        ArrayList<Integer> matches = new ArrayList<>();

        for (String message : messages)
        {
            matches.add(StringUtils.getLevenshteinDistance(base, message));
        }

        int candidateIndex = 0;
        int candidate = Integer.MAX_VALUE;

        for (int i = 0; i < matches.size(); i++)
        {
            int entry = matches.get(i);

            if (entry < candidate)
            {
                candidate = entry;
                candidateIndex = i;
            }
        }

        if (candidate > 10)
        {
            return null;
        }

        return messages[matches.get(candidateIndex)];
    }

    /**
     * Delete a message after a certain amount of time
     *
     * @param message The message to delete
     * @param duration How much time (in milliseconds) to wait before deletion
     */
    public static void deleteAfter(Message message, int duration)
    {
        deletePool.schedule(() -> message.delete().queue(), duration, TimeUnit.MILLISECONDS);
    }

    public static Message search(TextChannel channel, String query, int max)
    {
        List<Message> messages = channel.getHistory().retrievePast(100).complete();
        messages.remove(0);

        Message result = null;
        int searched = 0;

        while (result == null && searched < max)
        {
            messages = channel.getHistory().retrievePast(100).complete();

            for (Message message : messages)
            {
                if (message.getContent().toLowerCase().contains(query.toLowerCase().trim()))
                {
                    result = message;
                    break;
                }
            }

            searched += messages.size();
        }

        return result;
    }
}
