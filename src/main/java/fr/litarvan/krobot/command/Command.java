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

public class Command
{
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
        if (args.size() > 0)
        {
            for (Command sub : subs)
            {
                if (sub.getLabel().equals(args.get(0)))
                {
                    if (executeMiddlewares(context))
                    {
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

        handler.handle(context, map);

        context.getMessage().delete().queue();
    }

    private boolean executeMiddlewares(CommandContext context)
    {
        for (Middleware middleware : middlewares)
        {
            if (!middleware.handle(this, context))
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
}
