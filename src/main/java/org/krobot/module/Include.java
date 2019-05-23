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
package org.krobot.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.krobot.KrobotModule;
import org.krobot.command.CommandFilter;
import org.krobot.command.CommandHandler;
import org.krobot.console.ConsoleCommand;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Include
{
    Class<? extends KrobotModule>[] imports() default {};
    Class<? extends CommandFilter>[] filters() default {};
    Class<? extends CommandHandler>[] commands() default {};
    Class<?>[] listeners() default {};
    Class<? extends ConsoleCommand>[] consoleCommands() default {};
}
