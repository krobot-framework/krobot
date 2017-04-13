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
package fr.litarvan.krobot.command;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * The Command Handler<br><br>
 *
 *
 * Called by the {@link CommandManager} to handle the call
 * of a command.
 *
 * @author Litarvan
 * @version 2.1.0
 * @since 2.0.0
 */
@FunctionalInterface
public interface CommandHandler
{
    /**
     * Handle a command calling
     *
     * @param context The context of the command call
     * @param args The supplied arguments
     *
     * @throws Exception If the command threw one
     */
    void handle(@NotNull CommandContext context, @NotNull Map<String, SuppliedArgument> args) throws Exception;
}
