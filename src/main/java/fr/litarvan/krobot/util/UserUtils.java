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

import fr.litarvan.krobot.Krobot;
import java.util.List;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

public final class UserUtils
{
    private static JDA jda = Krobot.jda();

    public static User resolve(String user)
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
}
