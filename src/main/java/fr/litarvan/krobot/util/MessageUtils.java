/*
 * Copyright 2017 Adrien "Litarvan" Navratil
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
package fr.litarvan.krobot.util;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public final class MessageUtils
{
    public static final int MAX_MESSAGE_CHARS = 1999;

    public static PrivateChannel privateChannel(User user)
    {
        if (!user.hasPrivateChannel())
        {
            try
            {
                return user.openPrivateChannel().submit().get();
            }
            catch (InterruptedException | ExecutionException ignored)
            {
            }
        }

        return user.getPrivateChannel();
    }

    public static String[] splitMessage(String message)
    {
        return splitMessage(message, MAX_MESSAGE_CHARS);
    }

    public static String[] splitMessage(String message, int limit)
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
}
