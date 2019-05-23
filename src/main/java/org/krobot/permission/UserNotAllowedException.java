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
package org.krobot.permission;

import net.dv8tion.jda.core.Permission;
import org.krobot.command.KrobotCommand;

/**
 * The User Not Allowed Exception<br><br>
 *
 * This exception is thrown when the user is missing a
 * permission required to execute a command.<br>
 * By default, this error is caught by the ExceptionHandler
 * to then print a specific error message.
 *
 * @author Litarvan
 * @version 2.3.0
 * @since 2.3.0
 */
public class UserNotAllowedException extends RuntimeException
{
    private Permission permission;

    /**
     * The User Not Allowed Exception (with a simple message)
     */
    public UserNotAllowedException()
    {
        super("You are not allowed to do that");
    }

    /**
     * The User Not Allowed Exception
     *
     * @param s Custom message to display
     */
    public UserNotAllowedException(String s)
    {
        super(s);
    }

    /**
     * The User Not Allowed Exception
     *
     * @param permission The missing permission
     */
    public UserNotAllowedException(Permission permission)
    {
        super("You needs the permission '" + permission.getName() + "' to execute this command");

        this.permission = permission;
    }

    public Permission getPermission()
    {
        return permission;
    }
}
