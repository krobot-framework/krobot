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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;
import org.apache.commons.lang3.ArrayUtils;
import org.krobot.MessageContext;
import org.krobot.permission.BotNotAllowedException;
import org.krobot.permission.BotRequires;
import org.krobot.permission.UserNotAllowedException;
import org.krobot.permission.UserRequires;
import org.krobot.runtime.KrobotRuntime;
import org.krobot.util.UserUtils;

@Singleton
public class CommandManager
{
    private static final Map<String, ArgumentFactory> argumentFactories = new HashMap<>();

    private KrobotRuntime runtime;

    private List<KrobotCommand> commands;
    private List<CommandFilter> filters;

    @Inject
    public CommandManager(KrobotRuntime runtime)
    {
        this.runtime = runtime;

        this.commands = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    public void handle(MessageContext context)
    {
        String content = context.getMessage().getContentRaw().trim();
        String prefix = runtime.getFilterRunner().getPrefix(context);

        String botMention = "<@!" + runtime.jda().getSelfUser().getId() + "> ";

        if (content.startsWith(botMention) && !content.equals(botMention) && !runtime.getRootModule().getModule().getClass().isAnnotationPresent(DisableMention.class))
        {
            prefix = botMention;
        }

        if (prefix != null && (!content.startsWith(prefix) || content.equals(prefix)))
        {
            return;
        }

        if (content.startsWith(botMention))
        {
            content = content.substring(botMention.length());
        }
        else if (prefix != null && content.startsWith(prefix))
        {
            content = content.substring(prefix.length());
        }

        String[] split = splitWithQuotes(content);

        if (split.length == 0)
        {
            return;
        }

        String label = split[0];

        Optional<KrobotCommand> optional = commands.stream().filter(c -> {
            if (c.getAliases() != null)
            {
                Optional<String> alias = Stream.of(c.getAliases()).filter(a -> a.equalsIgnoreCase(label)).findFirst();

                if (alias.isPresent())
                {
                    return true;
                }
            }

            return c.getLabel().equalsIgnoreCase(label);
        }).findFirst();

        if (!optional.isPresent())
        {
            return;
        }

        KrobotCommand command = optional.get();
        String[] args = ArrayUtils.subarray(split, 1, split.length);

        if (command.getHandler().getClass().isAnnotationPresent(DisableMention.class) && Objects.equals(prefix, botMention))
        {
            return;
        }

        if (args.length > 0)
        {
            Optional<KrobotCommand> sub = command.getSubCommands().stream().filter(s -> s.getLabel().equalsIgnoreCase(args[0]) || ArrayUtils.contains(s.getAliases(), args[0])).findFirst();

            if (sub.isPresent())
            {
                try
                {
                    execute(context, sub.get(), ArrayUtils.subarray(args, 1, args.length));
                }
                catch (Exception e)
                {
                    runtime.getExceptionHandler().handle(context, command, args, e);
                }

                return;
            }
        }

        try
        {
            execute(context, command, args);
        }
        catch (Exception e)
        {
            runtime.getExceptionHandler().handle(context, command, args, e);
        }
    }

    public void execute(MessageContext context, KrobotCommand command, String[] args) throws Exception
    {
        CommandHandler handler = command.getHandler();

        if (handler.getClass().isAnnotationPresent(BotRequires.class))
        {
            for (Permission perm : handler.getClass().getAnnotation(BotRequires.class).value())
            {
                if (!context.botHasPermission(perm))
                {
                    throw new BotNotAllowedException(perm);
                }
            }
        }

        if (handler.getClass().isAnnotationPresent(UserRequires.class))
        {
            for (Permission perm : handler.getClass().getAnnotation(UserRequires.class).value())
            {
                if (!context.hasPermission(perm))
                {
                    throw new UserNotAllowedException(perm);
                }
            }
        }

        Map<String, Object> supplied = new HashMap<>();

        int i;

        for (i = 0; i < command.getArguments().length; i++)
        {
            CommandArgument arg = command.getArguments()[i];

            if (i > args.length - 1)
            {
                if (arg.isRequired())
                {
                    throw new WrongArgumentNumberException(command, args.length);
                }

                break;
            }

            if (arg.isList())
            {
                List list = new ArrayList();

                for (; i < args.length; i++)
                {
                    list.add(arg.getFactory().process(args[i]));
                }

                supplied.put(arg.getKey(), list.toArray(arg.getFactory().createArray()));
            }
            else
            {
                supplied.put(arg.getKey(), arg.getFactory().process(args[i]));
            }
        }

        if (i < args.length - 1)
        {
            throw new WrongArgumentNumberException(command, args.length);
        }

        ArgumentMap argsMap = new ArgumentMap(supplied);

        CommandCall call = new CommandCall(command);

        if (command.getFilters() != null)
        {
            for (CommandFilter filter : command.getFilters())
            {
                filter.filter(call, context, argsMap);
            }
        }

        if (!call.isCancelled())
        {
            if (!command.getHandler().getClass().isAnnotationPresent(NoTyping.class))
            {
                context.getChannel().sendTyping().queue();
            }

            Object result;

            try
            {
                result = command.getHandler().handle(context, argsMap);
            }
            catch (Throwable t)
            {
                runtime.getExceptionHandler().handle(context, command, args, t);
                return;
            }

            if (context.botHasPermission(Permission.MESSAGE_MANAGE) && context.getGuild() != null /* Check we are not in dm */ )
            {
                context.getMessage().delete().reason("Command triggered").queue();
            }

            if (result != null)
            {
                if (result instanceof EmbedBuilder)
                {
                    context.send((EmbedBuilder) result);
                }
                else if (result instanceof MessageEmbed)
                {
                    context.send((MessageEmbed) result);
                }
                else if (result instanceof RestAction)
                {
                    ((RestAction) result).queue();
                }
                else if (!(result instanceof Future))
                {
                    context.send(result.toString());
                }
            }
        }
    }

    /**
     * Split a message from whitespaces, ignoring the one in quotes.<br><br>
     *
     * <b>Example :</b>
     *
     * <pre>
     *     I am a "discord bot"
     * </pre>
     *
     * Will return ["I", "am", "a", "discord bot"].
     *
     * @param line The line to split
     *
     * @return The line split
     */
    public static String[] splitWithQuotes(String line)
    {
        ArrayList<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"]+|\"([^\"]*)\"");
        Matcher matcher = regex.matcher(line);

        while (matcher.find())
        {
            if (matcher.group(1) != null)
            {
                matchList.add(matcher.group(1));
            }
            else
            {
                matchList.add(matcher.group());
            }
        }

        return matchList.toArray(new String[matchList.size()]);
    }

    public List<KrobotCommand> getCommands()
    {
        return commands;
    }

    public List<CommandFilter> getFilters()
    {
        return filters;
    }

    public static void registerArgumentFactory(String name, ArgumentFactory factory)
    {
        argumentFactories.put(name, factory);
    }

    public static ArgumentFactory getArgumentFactory(String key)
    {
        return argumentFactories.get(key);
    }

    static
    {
        registerArgumentFactory("string", new ArgumentFactory<String>()
        {
            @Override
            public String process(String a) throws BadArgumentTypeException
            {
                return a;
            }

            @Override
            public String[] createArray()
            {
                return new String[0];
            }
        });

        registerArgumentFactory("number", new ArgumentFactory<Integer>()
        {
            @Override
            public Integer process(String argument) throws BadArgumentTypeException
            {
                try
                {
                    return Integer.parseInt(argument);
                }
                catch (NumberFormatException e)
                {
                    throw new BadArgumentTypeException(argument, "number");
                }
            }

            @Override
            public Integer[] createArray()
            {
                return new Integer[0];
            }
        });

        registerArgumentFactory("float", new ArgumentFactory<Float>()
        {
            @Override
            public Float process(String argument) throws BadArgumentTypeException
            {
                try
                {
                    return Float.parseFloat(argument);
                }
                catch (NumberFormatException e)
                {
                    throw new BadArgumentTypeException(argument, "float");
                }
            }

            @Override
            public Float[] createArray()
            {
                return new Float[0];
            }
        });

        registerArgumentFactory("user", new ArgumentFactory<User>()
        {
            @Override
            public User process(String argument) throws BadArgumentTypeException
            {
                User result = UserUtils.resolve(argument);

                if (result == null)
                {
                    throw new BadArgumentTypeException("Can't find user '" + argument + "'", argument, "user");
                }

                return result;
            }

            @Override
            public User[] createArray()
            {
                return new User[0];
            }
        });

        // Aliases
        registerArgumentFactory("integer", getArgumentFactory("number"));
        registerArgumentFactory("int", getArgumentFactory("number"));
    }
}
