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

/**
 * The Group Builder
 *
 *
 * Used to build a command group (a list of properties that
 * will be applied to the command registered in {@link #apply(Runnable)}).
 *
 * To be used from the {@link CommandManager}.
 *
 * Example :
 *
 * <pre>
 *     manager.group().prefix("!").middlewares(MyMiddleware.class).apply(() -> {
 *         manager.make("mycommand", MyHandler.class).register();
 *         manager.make("myothercommand", MyOtherHandler.class).register();
 *     });
 * </pre>
 *
 * In this case, the label of mycommand and myothercommand will be
 * !mycommand and !myothercommand, and they will have the MyMiddleware
 * middleware triggered before their call.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public class GroupBuilder
{
    private CommandManager commandManager;

    /**
     * Creates a GroupBuilder
     *
     * @param commandManager The current command manager
     */
    public GroupBuilder(CommandManager commandManager)
    {
        this.commandManager = commandManager;
        this.middlewares = new ArrayList<>();
    }

    private String prefix;
    private Command parent;
    private List<Middleware> middlewares;

    /**
     * Apply a prefix to the label of the group commands
     *
     * @param prefix The prefix to apply
     *
     * @return This
     */
    public GroupBuilder prefix(String prefix)
    {
        this.prefix = prefix;
        return this;
    }

    /**
     * Define a parent command of the group commands, so the
     * group commands will be sub commands of it.
     *
     * @param parent The parent to define
     *
     * @return This
     */
    public GroupBuilder parent(Command parent)
    {
        this.parent = parent;
        return this;
    }

    /**
     * Register a middleware to the group commands
     *
     * @param middleware The middleware to register
     *
     * @return This
     */
    public GroupBuilder middleware(Middleware middleware) // Needed for lambda
    {
        this.middlewares.add(middleware);
        return this;
    }

    /**
     * Register middlewares to the group commands
     *
     * @param middlewares The middlewares to register
     *
     * @return This
     */
    public GroupBuilder middlewares(Middleware... middlewares) // 's' needed for lambda
    {
        this.middlewares.addAll(Arrays.asList(middlewares));
        return this;
    }

    /**
     * Register middlewares to the group commands
     *
     * @param middlewares The middlewares to register (will be created
     *                    by the injector)
     *
     * @return This
     */
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

    /**
     * Setup the group
     *
     * Example of use :
     *
     * <pre>
     *     manager.group().prefix("!").middlewares(MyMiddleware.class).apply(() -> {
     *         manager.make("mycommand", MyHandler.class).register();
     *         manager.make("myothercommand", MyOtherHandler.class).register();
     *     });
     * </pre>
     *
     * In this case, the label of mycommand and myothercommand will be
     * !mycommand and !myothercommand, and they will have the MyMiddleware
     * middleware triggered before their call.
     *
     * @param runnable A runnable where to register the command of
     *                 the group.
     */
    public void apply(Runnable runnable)
    {
        CommandGroup group = new CommandGroup(prefix, parent, middlewares);
        commandManager.push(group);

        runnable.run();

        commandManager.pop();
    }
}
