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
import org.krobot.Krobot;
import org.krobot.util.UserUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Command<br><br>
 *
 *
 * A command definition registered to the {@link CommandManager}.<br>
 * Can be called using the {@link #call(CommandContext, List)}
 * method.<br><br>
 *
 * Can be used as start point for making sub commands, using
 * {@link #sub} methods.
 *
 * @author Litarvan
 * @version 2.1.1
 * @since 2.0.0
 */
public class Command
{
    private static final Logger LOGGER = LogManager.getLogger("Command");

    private Command parent;
    private String label;
    private String description;
    private CommandArgument[] arguments;
    private Middleware[] middlewares;
    private CommandHandler handler;

    private List<Command> subs;

    private JDA jda;
    private ExceptionHandler exHandler;

    /**
     * The command
     *
     * @param jda The current JDA instance
     * @param exHandler The handler that will catch the sub commands call exceptions
     *
     * @param parent The command parent (if it is a sub command)
     * @param label The command label (By exemple in !command &lt;arg&gt; the label is '!command')
     * @param description The description of the command
     * @param arguments The arguments that the command can receive
     * @param middlewares The middlewares of the command
     * @param handler The handler of the command call
     */
    public Command(JDA jda, ExceptionHandler exHandler, Command parent, String label, String description, CommandArgument[] arguments, Middleware[] middlewares, CommandHandler handler)
    {
        this.jda = jda;
        this.exHandler = exHandler;

        this.parent = parent;
        this.label = label;
        this.description = description;
        this.arguments = arguments;
        this.middlewares = middlewares;
        this.handler = handler;

        this.subs = new ArrayList<>();
    }

    /**
     * Call the command
     *
     * @param context The context of the command
     * @param args The given args
     *
     * @throws Exception If the command handler threw one
     */
    public void call(CommandContext context, List<String> args) throws Exception
    {
        LOGGER.debug("Parsing command call for -> " + this.label);

        if (args.size() > 0)
        {
            for (Command sub : subs)
            {
                if (sub.getLabel().equals(args.get(0)))
                {
                    if (executeMiddlewares(context, null))
                    {
                        LOGGER.debug("Sub command detected -> " + sub.getLabel());

                        List<String> subArgs = args.subList(1, args.size());

                        try
                        {
                            sub.call(context, subArgs);
                        }
                        catch (Throwable t)
                        {
                            exHandler.handle(t, sub, subArgs, context);
                        }
                    }

                    return;
                }
            }
        }

        Map<String, SuppliedArgument> map = new HashMap<>();

        for (int i = 0, i2 = 0; i < arguments.length; i++)
        {
            Exception ex = null;

            if (i2 > args.size() - 1)
            {
                ex = new BadSyntaxException("Missing some args");
            }
            else if (!arguments[i].isList())
            {
                ArgumentType type = arguments[i].getType();

                String[] choices = arguments[i].getChoices();
                String arg = args.get(i2);

                if (choices != null && !ArrayUtils.contains(choices, arg))
                {
                    ex = new BadSyntaxException("Supplied arg '" + arg + "' isn't one of the possible choices : " + ArrayUtils.toString(choices) + " (for arg '" + arguments[i].getKey() + "')");
                }
                if (type == ArgumentType.NUMBER && !NumberUtils.isCreatable(arg))
                {
                    ex = new BadSyntaxException("Supplied arg '" + arg + "' isn't a number (for arg '" + arguments[i].getKey() + "')");
                }
                else if (type == ArgumentType.USER && UserUtils.resolve(arg) == null)
                {
                    ex = new UserNotFoundException(arg);
                }
            }

            if (ex != null)
            {
                if (arguments[i].isOptional())
                {
                    if (i2 > args.size() - 1)
                    {
                        break;
                    }
                    else
                    {
                        i--;
                    }
                }
                else
                {
                    throw ex;
                }
            }
            else
            {
                SuppliedArgument argument;

                if (arguments[i].isList())
                {
                    List<String> values = args.subList(i2, args.size());
                    ArgumentType type = arguments[i].getType();
                    List list;

                    switch (type)
                    {
                        case USER:
                            list = new ArrayList<User>();
                            break;
                        case NUMBER:
                            list = new ArrayList<Integer>();
                            break;
                        case STRING:
                            list = new ArrayList<String>();
                            break;
                        default:
                            list = null;
                    }

                    for (String value : values)
                    {
                        switch (type)
                        {
                            case USER:
                                User user = UserUtils.resolve(context.getGuild(), value);
                                if (user == null)
                                {
                                    throw new UserNotFoundException(value);
                                }

                                list.add(user);
                                break;
                            case NUMBER:
                                if (!NumberUtils.isCreatable(value))
                                {
                                    throw new BadSyntaxException("Supplied arg '" + value + "' (from list arg '" + arguments[i].getKey() + "') isn't a number");
                                }

                                Number number = NumberUtils.createNumber(value);

                                if(!(number instanceof Integer))
                                {
                                    throw new BadSyntaxException("Supplied arg '" + value + "' (from list arg '" + arguments[i].getKey() + "') is too big");
                                }

                                list.add((Integer) number);
                                break;
                            case STRING:
                                list.add(value);
                                break;
                        }
                    }

                    argument = new SuppliedArgument(list, type);
                }
                else
                {
                    String value = args.get(i2);

                    switch (arguments[i].getType())
                    {
                        case USER:
                            argument = new SuppliedArgument(UserUtils.resolve(context.getGuild(), value));
                            break;
                        case NUMBER:
                            Number number = NumberUtils.createNumber(value);

                            if(!(number instanceof Integer))
                            {
                                throw new BadSyntaxException("Supplied arg '" + value + "' (from list arg '" + arguments[i].getKey() + "') is too big");
                            }

                            argument = new SuppliedArgument((Integer) number);
                            break;
                        case STRING:
                            argument = new SuppliedArgument(value);
                            break;
                        default:
                            argument = null;
                    }
                }

                map.put(arguments[i].getKey(), argument);
            }

            i2++;
        }

        StringBuilder label = new StringBuilder(this.getLabel());
        Command parent = this.getParent();

        while (parent != null)
        {
            label.insert(0, parent.getLabel() + " > ");
            parent = parent.getParent();
        }

        LOGGER.debug("Handling call of -> " + label + " | with args -> " + map);

        if (executeMiddlewares(context, map))
        {
            handler.handle(context, map);
        }

        if (context.getGuild().getMember(jda.getSelfUser()).hasPermission(Permission.MESSAGE_MANAGE))
        {
            context.getMessage().delete().queue();
        }
    }

    private boolean executeMiddlewares(CommandContext context, Map<String, SuppliedArgument> args)
    {
        for (Middleware middleware : middlewares)
        {
            if (!middleware.handle(this, args, context))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Register a sub command.<br><br>
     *
     * Example :<br>
     * Registering a command "sub &lt;arg&gt;" as a sub command
     * of "!test", will be registered as "!test sub &lt;arg&gt;"
     *
     * @param command The sub command to register
     */
    public void sub(Command command)
    {
        this.subs.add(command);
    }

    /**
     * Create a command builder for a sub command
     *
     * Example :
     * Registering a command "sub &lt;arg&gt;" as a sub command
     * of "!test", will be registered as "!test sub &lt;arg&gt;"
     *
     * @param path The path of the command (see {@link CommandBuilder#path(String)}
     *             for the syntax)
     * @param commandCl The command handler (to be created by the injector)
     *
     * @return The created CommandBuilder
     */
    public CommandBuilder sub(String path, Class<? extends CommandHandler> commandCl)
    {
        return sub(path, Krobot.injector().getInstance(commandCl));
    }

    /**
     * Create a command builder for a sub command
     *
     * Example :
     * Registering a command "sub &lt;arg&gt;" as a sub command
     * of "!test", will be registered as "!test sub &lt;arg&gt;"
     *
     * @param path The path of the command (see {@link CommandBuilder#path(String)}
     *             for the syntax)
     * @param handler The command handler
     *
     * @return The created CommandBuilder
     */
    public CommandBuilder sub(String path, CommandHandler handler)
    {
        return new CommandBuilder(null, jda, exHandler).parent(this).path(path).handler(handler);
    }

    /**
     * @return The parent command if it is a sub command
     */
    public Command getParent()
    {
        return parent;
    }

    /**
     * @return The command label (By example in !command &lt;arg&gt; the label is '!command')
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * @return The description of the command
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return The arguments that the command can receive
     */
    public CommandArgument[] getArguments()
    {
        return arguments;
    }

    /**
     * @return The middlewares of the command
     */
    public Middleware[] getMiddlewares()
    {
        return middlewares;
    }

    /**
     * @return The handler of the command call
     */
    public CommandHandler getHandler()
    {
        return handler;
    }

    /**
     * @return The registered sub commands
     */
    public List<Command> getSubs()
    {
        return subs;
    }

    @Override
    public String toString()
    {
        return toString("", true);
    }

    /**
     * Convert the command to a displayable string
     * (similar to the {@link CommandBuilder#path(String)} syntax)
     *
     * @param prefix The prefix to add (like tabs)
     * @param subs If the subs should be displayed too
     *
     * @return The generated string
     */
    public String toString(String prefix, boolean subs)
    {
        StringBuilder string = new StringBuilder(prefix + this.label + " ");

        for (CommandArgument argument : arguments)
        {
            string.append(argument).append(" ");
        }

        if (subs && this.subs != null && !this.subs.isEmpty())
        {
            for (Command sub : this.subs)
            {
                string.append("\n");
                string.append(prefix).append(sub.toString(prefix + this.getLabel() + " ", true));
            }
        }

        return string.toString();
    }
}
