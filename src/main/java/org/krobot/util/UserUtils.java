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

import org.krobot.Krobot;
import java.util.List;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import org.jetbrains.annotations.NotNull;

/**
 * User Utils<br><br>
 *
 *
 * A class containing user-related util functions.
 *
 * @author Litarvan
 * @version 2.1.1
 * @since 2.0.0
 */
public final class UserUtils
{
    private static JDA jda = Krobot.jda();

    /**
     * Resolve a user from a String.<br>
     * If it can be a nickname, use {@link #resolve(Guild, String)}<br><br>
     *
     * Example : "@Litarvan", "Litarvan", or "87279950075293696"<br>
     * returns the JDA User object of Litarvan.
     *
     * @param user A string (mention/username/id) of the user
     *             to resolve
     *
     * @return The resolved user
     */
    public static User resolve(@NotNull String user)
    {
        List<User> users = jda.getUsersByName(user, true);

        if (users.size() == 0 && user.startsWith("@"))
        {
            users = jda.getUsersByName(user.substring(1), true);
        }

        if (users.size() == 0)
        {
            return jda.getUserById(user);
        }

        return users.get(0);
    }

    /**
     * Resolve a user from a String.<br>
     * The guild is used to check if it is a nickname and not the
     * user real name.<br><br>
     *
     * Example : "@Litarvan", "Litarvan", or "87279950075293696"<br>
     * returns the JDA User object of Litarvan.
     *
     * @param guild The guild where the user is
     * @param user A string (mention/username/id) of the user
     *             to resolve
     *
     * @return The resolved user
     */
    public static User resolve(@NotNull Guild guild, @NotNull String user)
    {
        List<Member> users = guild.getMembersByEffectiveName(user, true);

        if (users.size() == 0 && user.startsWith("@"))
        {
            users = guild.getMembersByEffectiveName(user.substring(1), true);
        }

        if (users.size() == 0)
        {
            return resolve(user);
        }

        return users.get(0).getUser();
    }

    /**
     * Get the private channel of the given user. Opens it if needed.
     *
     * @param user The user private channel
     *
     * @return The private channel
     *
     * @deprecated Deprecated in JDA, use now {@link User#openPrivateChannel()} directly.
     */
    public static PrivateChannel privateChannel(@NotNull User user)
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
}
