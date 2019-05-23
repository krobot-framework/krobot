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
 * The Bot Not Allowed Exception<br><br>
 *
 * This exception is thrown when the bot is missing a
 * necessary permission to execute a command.<br>
 * By default, this error is caught by the ExceptionHandler
 * to then print a specific error message.
 *
 * @author Litarvan
 * @version 2.3.0
 * @since 2.3.0
 */
public class BotNotAllowedException extends RuntimeException
{
    private Permission permission;

    /**
     * The Bot Not Allowed Exception
     *
     * @param permission The missing permission
     */
    public BotNotAllowedException(Permission permission)
    {
        super("The bot needs the permission '" + permission.getName() + "' to execute this command");

        this.permission = permission;
    }

    public Permission getPermission()
    {
        return permission;
    }
}
