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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.krobot.Krobot;
import org.krobot.runtime.KrobotRuntime;

/**
 * User Utils<br><br>
 *
 *
 * A class containing user-related util functions.
 *
 * @author Litarvan
 * @version 2.3.1
 * @since 2.0.0
 */
public final class UserUtils
{
    /**
     * Resolve a user from a String.<br>
     * If it can be a nickname, use {@link #resolve(Guild, String)}<br><br>
     *
     * Example : "@Litarvan", "Litarvan", "&lt;@!87279950075293696&gt;" or "87279950075293696"<br>
     * returns the JDA User object of Litarvan.
     *
     * @param user A string (mention/username/id) of the user
     *             to resolve
     *
     * @return The resolved user or null if none was found
     */
    @Nullable
    public static User resolve(@NotNull String user)
    {
        user = user.trim();
        List<User> users = jda().getUsersByName(user, true);

        if (users.size() == 0 && user.startsWith("<@") && user.endsWith(">"))
        {
            try
            {
                return jda().getUserById(Long.parseLong(user.substring(2, user.length() - 1)));
            }
            catch (NumberFormatException ignored)
            {
            }
        }

        if (users.size() == 0 && user.startsWith("@"))
        {
            users = jda().getUsersByName(user.substring(1), true);
        }

        if (users.size() == 0 && StringUtils.isNumeric(user))
        {
            return jda().getUserById(user);
        }

        return users.size() > 0 ? users.get(0) : null;
    }

    /**
     * Resolve a user from a String.<br>
     * The guild is used to check if it is a nickname and not the
     * user real name.<br><br>
     *
     * Example : "@Litarvan", "Litarvan", "&lt;@!87279950075293696&gt;" or "87279950075293696"<br>
     * returns the JDA User object of Litarvan.
     *
     * @param guild The guild where the user is
     * @param user A string (mention/username/id) of the user
     *             to resolve
     *
     * @return The resolved user or null if none was found
     */
    @Nullable
    public static User resolve(@NotNull Guild guild, @NotNull String user)
    {
        user = user.trim();
        List<Member> users = guild.getMembersByNickname(user, true);

        if (users.size() == 0 && user.startsWith("@"))
        {
            users = guild.getMembersByNickname(user.substring(1), true);
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
    @Deprecated
    public static PrivateChannel privateChannel(@NotNull User user)
    {
        return user.openPrivateChannel().complete();
    }

    private static JDA jda()
    {
        return KrobotRuntime.get().jda();
    }
}
