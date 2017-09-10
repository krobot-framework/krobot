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

import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.krobot.KrobotModule;

public class CommandAccessor
{
    private KrobotModule module;
    private KrobotCommand command;

    public CommandAccessor(KrobotModule module, KrobotCommand command)
    {
        this.module = module;
        this.command = command;
    }

    public CommandAccessor description(String desc)
    {
        command.setDescription(desc);
        return this;
    }

    public CommandAccessor alias(String... aliases)
    {
        command.setAliases(ArrayUtils.addAll(command.getAliases(), aliases));
        return this;
    }

    @SafeVarargs // If you don't want your whole command(...) chain to be underlined
    public final /* @SafeVarargs requires the method to be final */ CommandAccessor filter(Class<? extends CommandFilter>... filters)
    {
        return filter(Stream.of(filters)
                            .map(filter -> module.injector().getInstance(filter))
                            .toArray(CommandFilter[]::new));
    }

    public CommandAccessor filter(CommandFilter... filter)
    {
        command.setFilters(ArrayUtils.addAll(command.getFilters(), filter));
        return this;
    }

    public SubCommandAccessor sub(String path, Class<? extends CommandHandler> handler)
    {
        return sub(path, module.injector().getInstance(handler));
    }

    public SubCommandAccessor sub(String path, CommandHandler handler)
    {
        PathCompiler compiler = new PathCompiler(path);
        compiler.compile();

        KrobotCommand sub = new KrobotCommand(compiler.label(), compiler.args(), handler);

        KrobotCommand[] merged = ArrayUtils.add(command.getSubCommands(), sub);
        this.command.setSubCommands(merged);

        return new SubCommandAccessor(module, this, sub);
    }

    public static class SubCommandAccessor extends CommandAccessor
    {
        private CommandAccessor parent;

        SubCommandAccessor(KrobotModule module, CommandAccessor parent, KrobotCommand child)
        {
            super(module, child);

            this.parent = parent;
        }

        @Override
        public SubCommandAccessor description(String desc)
        {
            return (SubCommandAccessor) super.description(desc);
        }

        @Override
        public SubCommandAccessor alias(String... aliases)
        {
            return (SubCommandAccessor) super.alias(aliases);
        }

        @SafeVarargs // If you don't want your whole command(...) chain to be underlined
        // @Override Couldn't override method, because @SafeVarargs makes it final. Renamed it to filterS
        public final /* @SafeVarargs requires the method to be final */ SubCommandAccessor filters(Class<? extends CommandFilter>... filters)
        {
            return (SubCommandAccessor) super.filter(filters);
        }

        @Override
        public SubCommandAccessor filter(CommandFilter... filter)
        {
            return (SubCommandAccessor) super.filter(filter);
        }

        public CommandAccessor then()
        {
            return this.parent;
        }
    }
}
