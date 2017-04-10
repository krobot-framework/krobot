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

public class Command
{
    private static final Logger LOGGER = LogManager.getLogger("Command");

    private String label;
    private CommandArgument[] arguments;
    private Middleware[] middlewares;
    private CommandHandler handler;

    private List<Command> subs;

    public Command(String label, CommandArgument[] arguments, Middleware[] middlewares, CommandHandler handler)
    {
        this.label = label;
        this.arguments = arguments;
        this.middlewares = middlewares;
        this.handler = handler;

        this.subs = new ArrayList<>();
    }

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

        executeMiddlewares(context, map);
        handler.handle(context, map);

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

    public void sub(Command command)
    {
        this.subs.add(command);
    }

    public CommandBuilder sub(String path, Class<? extends CommandHandler> commandCl)
    {
        return sub(path, Krobot.injector().getInstance(commandCl));
    }

    public CommandBuilder sub(String path, CommandHandler handler)
    {
        return sub(handler).path(path);
    }

    public CommandBuilder sub(CommandHandler handler)
    {
        return new CommandBuilder(null).parent(this).handler(handler);
    }

    public String getLabel()
    {
        return label;
    }

    public CommandArgument[] getArguments()
    {
        return arguments;
    }

    public Middleware[] getMiddlewares()
    {
        return middlewares;
    }

    public CommandHandler getHandler()
    {
        return handler;
    }

    public List<Command> getSubs()
    {
        return subs;
    }

    @Override
    public String toString()
    {
        return toString("", true);
    }

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
