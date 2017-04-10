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

import fr.litarvan.krobot.Krobot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupBuilder
{
    private CommandManager commandManager;

    public GroupBuilder(CommandManager commandManager)
    {
        this.commandManager = commandManager;
        this.middlewares = new ArrayList<>();
    }

    private String prefix;
    private Command parent;
    private List<Middleware> middlewares;

    public GroupBuilder prefix(String prefix)
    {
        this.prefix = prefix;
        return this;
    }

    public GroupBuilder parent(Command parent)
    {
        this.parent = parent;
        return this;
    }

    public GroupBuilder middleware(Middleware middleware) // Needed for lambda
    {
        this.middlewares.add(middleware);
        return this;
    }

    public GroupBuilder middlewares(Middleware... middlewares) // 's' needed for lambda
    {
        this.middlewares.addAll(Arrays.asList(middlewares));
        return this;
    }

    public GroupBuilder middlewares(Class<? extends Middleware>... middlewares)
    {
        Middleware[] mwares = new Middleware[middlewares.length];

        for (int i = 0; i < middlewares.length; i++)
        {
            mwares[i] = Krobot.injector().getInstance(middlewares[i]);
        }

        this.middlewares.addAll(Arrays.asList(mwares));

        return this;
    }

    public void apply(Runnable runnable)
    {
        CommandGroup group = new CommandGroup(prefix, parent, middlewares);
        commandManager.push(group);

        runnable.run();

        commandManager.pop();
    }
}
