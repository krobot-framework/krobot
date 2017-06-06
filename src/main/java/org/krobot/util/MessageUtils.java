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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Message Utils<br><br>
 *
 *
 * Message-related util functions.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public final class MessageUtils
{
    /**
     * Number of maximum character in a Discord message
     */
    public static final int MAX_MESSAGE_CHARS = 1999;

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
    public String getMostSimilar(String base, String[] messages)
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
}
