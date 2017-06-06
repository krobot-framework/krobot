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

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Middleware<br><br>
 *
 *
 * A Middleware is something that is called before that a/some
 * commands are called. It can cancel its handling.
 *
 * @author Litarvan
 * @version 2.1.1
 * @since 2.0.0
 */
@FunctionalInterface
public interface Middleware
{
    /**
     * Handle a command call
     *
     * @param command The command that was called
     * @param args The arguments given to the command =&gt; Null if it
     *             is called before a sub command handling
     * @param context The context of the command call
     *
     * @return If the handling should continue
     */
    boolean handle(@NotNull Command command, @Nullable Map<String, SuppliedArgument> args, @NotNull CommandContext context);
}
