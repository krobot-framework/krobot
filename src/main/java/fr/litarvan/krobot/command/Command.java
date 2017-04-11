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
import fr.litarvan.krobot.util.UserUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Command
 *
 *
 * A command definition registered to the {@link CommandManager}.
 * Can be called using the {@link #call(CommandContext, List)}
 * method.
 *
 * Can be used as start point for making sub commands, using
 * {@link #sub} methods.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public class Command
{
    private static final Logger LOGGER = LogManager.getLogger("Command");

    private String label;
    private String description;
    private CommandArgument[] arguments;
    private Middleware[] middlewares;
    private CommandHandler handler;

    private List<Command> subs;

    /**
     * The command
     *
     * @param label The command label (By exemple in !command &lt;arg&gt; the label is '!command')
     * @param description The description of the command
     * @param arguments The arguments that the command can receive
     * @param middlewares The middlewares of the command
     * @param handler The handler of the command call
     */
    public Command(String label, String description, CommandArgument[] arguments, Middleware[] middlewares, CommandHandler handler)
    {
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
                        sub.call(context, args.subList(1, args.size()));
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
                ex = new BadSyntaxException();
            }
            else if (!arguments[i].isList())
            {
                ArgumentType type = arguments[i].getType();

                String[] choices = arguments[i].getChoices();
                String arg = args.get(i2);

                if ((type == ArgumentType.NUMBER && !StringUtils.isNumeric(arg)) || (choices != null && !ArrayUtils.contains(choices, arg)))
                {
                    ex = new BadSyntaxException();
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
                                User user = UserUtils.resolve(value);
                                if (user == null)
                                {
                                    throw new UserNotFoundException(value);
                                }

                                list.add(user);
                                break;
                            case NUMBER:
                                if (!StringUtils.isNumeric(value))
                                {
                                    throw new BadSyntaxException();
                                }

                                list.add(Integer.parseInt(value));
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
                            argument = new SuppliedArgument(UserUtils.resolve(value));
                            break;
                        case NUMBER:
                            argument = new SuppliedArgument(Integer.parseInt(value));
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

        LOGGER.debug("Handling call of -> " + this.getLabel() + " | with args -> " + map);

        if (executeMiddlewares(context, map))
        {
            handler.handle(context, map);
        }

        context.getMessage().delete().queue();
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
     * Register a sub command.
     *
     * Example :
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
        return new CommandBuilder(null).path(path).handler(handler);
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
