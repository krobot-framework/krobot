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
import org.apache.commons.lang3.ArrayUtils;

/**
 * The Command Builder
 *
 *
 * Used to build commands, supposed to be used from the
 * {@link CommandManager} but can be used manually (the
 * group won't apply).
 *
 * Example :
 *
 * <pre>
 *     manager.make("command &lt;arg1&gt; [arg2...]", MyHandler.class).register();
 *     // Or
 *     Command command = new CommandBuiler().path("command &lt;arg1&gt; [arg2...]").handler(MyHandler.class).build();
 * </pre>
 *
 * See {@link #path(String)} for the make path expression
 * syntax.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public class CommandBuilder
{
    private CommandManager commandManager;

    /**
     * CommandBuidler for manual use
     */
    public CommandBuilder()
    {
        this(null);
    }

    /**
     * CommandBuilder with a manager
     *
     * @param commandManager The manager where to register the command
     */
    public CommandBuilder(CommandManager commandManager)
    {
        this.commandManager = commandManager;
        this.arguments = new ArrayList<>();
        this.middlewares = new ArrayList<>();
    }

    private String label;
    private String description;
    private List<CommandArgument> arguments;
    private Command parent;
    private CommandHandler handler;
    private List<Middleware> middlewares;

    // \B(\[|\()\w+(:\w+)?(\.\.\.)?(\]|\))

    /**
     * Generate a command label and arguments from an expression.
     *
     * Syntax :
     *
     * The first word, is the command label.
     * Then, each argument are separated by space.
     *
     * Argument syntax :
     *
     * If an argument is required, it is surrounded by &lt; &gt;
     * If it is optional, it is surrounded by [ ]
     *
     * In this, there is the name of the argument.
     *
     * Then, you can precise the type, for this just after
     * the argument name add a : and either
     *
     * <ul>
     *     <li>user</li>
     *     <li>string</li>
     *     <li>number</li>
     * </ul>
     *
     * (string is used by default when no type is given).
     *
     * Example : [arg:number]
     *
     * You can use vararg-like list, for this, just after
     * the type, put ...
     * Example: [arg...] [arg:user...]
     *
     * (It must to be the last argument)
     *
     * Instead of one of these, you can precise some fixed choices.
     * For this, replace the type (always use the : ) by the possible
     * choices separated by a |
     * Example : [arg:choice1|choice2|choice3]
     *
     * Here is an example of a command path :
     *
     * <pre>
     *     command &lt;arg1&gt; &lt;arg2:user&gt; [arg3:choice1|choice2] [arg4...]
     * </pre>
     *
     * @param path The path of the command
     *
     * @return This
     */
    public CommandBuilder path(String path)
    {
        String[] split = path.split(" ");
        this.label = split[0];

        for (String arg : ArrayUtils.subarray(split, 1, split.length))
        {
            boolean optional = false;
            boolean list = false;

            if (arg.equals("*"))
            {
                arg = "[dynamic:string...]";
            }

            if (arg.startsWith("["))
            {
                optional = true;
            }

            arg = arg.substring(1, arg.length() - 1);

            String type = "string";

            if (arg.contains(":"))
            {
                String[] spl = arg.split(":");

                arg = spl[0];
                type = spl[1];

                if (type.contains("|"))
                {
                    String[] choices = type.split("\\|");
                    this.arguments.add(new CommandArgument(arg, optional, choices));

                    continue;
                }
            }

            if (type.endsWith("..."))
            {
                list = true;
                type = type.substring(0, type.length() - 3);
            }

            ArgumentType argumentType = ArgumentType.valueOf(type.toUpperCase());
            this.arguments.add(new CommandArgument(arg, optional, list, argumentType));
        }

        return this;
    }

    /**
     * Set the label of the command
     *
     * @param label The command label
     *
     * @return This
     */
    public CommandBuilder label(String label)
    {
        this.label = label;
        return this;
    }

    /**
     * Set the command description
     *
     * @param description The description of the command
     *
     * @return This
     */
    public CommandBuilder description(String description)
    {
        this.description = description;
        return this;
    }

    /**
     * Add an argument to the command
     *
     * @param arg The argument to add
     *
     * @return This
     */
    public CommandBuilder arg(CommandArgument arg)
    {
        this.arguments.add(arg);
        return this;
    }

    /**
     * Define the parent of the command (so the command is a
     * sub of it)
     *
     * @param parent The command parent
     *
     * @return This
     */
    public CommandBuilder parent(Command parent)
    {
        this.parent = parent;
        return this;
    }

    /**
     * Set the command handler
     *
     * @param handler The command handler
     *
     * @return This
     */
    public CommandBuilder handler(CommandHandler handler)
    {
        this.handler = handler;
        return this;
    }

    /**
     * Register a middleware to the command
     *
     * @param middleware The middleware to register
     *
     * @return This
     */
    public CommandBuilder middleware(Middleware middleware) // Needed for lambda
    {
        this.middlewares.add(middleware);
        return this;
    }

    /**
     * Register middlewares to the command
     *
     * @param middlewares The middlewares to register
     *
     * @return This
     */
    public CommandBuilder middlewares(Middleware... middlewares) // 's' needed for lambda
    {
        this.middlewares.addAll(Arrays.asList(middlewares));
        return this;
    }

    /**
     * Register middlewares to the command
     *
     * @param middlewares The middlewares to register (will be created
     *                    by the injector)
     *
     * @return This
     */
    public CommandBuilder middlewares(Class<? extends Middleware>... middlewares)
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
     * Build the command
     *
     * @return The generated command
     */
    public Command build()
    {
        return new Command(this.parent,
                           this.label,
                           this.description,
                           this.arguments.toArray(new CommandArgument[this.arguments.size()]),
                           this.middlewares.toArray(new Middleware[this.middlewares.size()]),
                           this.handler);
    }

    /**
     * Build and register the command to the manager or to the
     * parent if given.
     *
     * Drop an exception if there is no command manager
     *
     * @return The generated command
     */
    public Command register()
    {
        Command command = build();

        if (parent != null)
        {
            parent.sub(command);
        }
        else
        {
            if (commandManager == null)
            {
                throw new IllegalStateException("Can't register the command if no command manager was given");
            }

            commandManager.register(command);
        }

        return command;
    }
}
