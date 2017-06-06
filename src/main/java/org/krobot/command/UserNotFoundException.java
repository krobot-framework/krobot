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
package org.krobot.command;

import org.krobot.ExceptionHandler;

/**
 * The User Not Found Exception<br><br>
 *
 *
 * An exception thrown when the user called a command and
 * provided a {@link ArgumentType#USER} argument but of
 * a user that cannot be resolved.<br><br>
 *
 * Supposed to be caught by the {@link ExceptionHandler}.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public class UserNotFoundException extends Exception
{
    private String user;

    /**
     * @param user The user that the command caller given
     */
    public UserNotFoundException(String user)
    {
        super("Can't find user '" + user + "'");
    }

    /**
     * @return The user that the command caller given
     */
    public String getUser()
    {
        return user;
    }
}
