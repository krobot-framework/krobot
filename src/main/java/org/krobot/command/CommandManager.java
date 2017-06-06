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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The CommandManager<br><br>
 *
 *
 * It's the starting point of a bot, and the main part of Krobot.<br>
 * It has a command registry, and handle the command call to its
 * registered command.<br>
 * It uses the builders to make commands.<br><br>
 *
 * To use it, use the {@link #make} methods.<br><br>
 *
 * <b>Examples :</b>
 *
 * <pre>
 *     manager.make("/mycommand", MyHandler.class).middlewares(MyMiddleware.class).register();
 *     manager.make("/version", (context, args) -&gt; {
 *         context.getChannel().sendMessage("v1.0.0").queue();
 *     }).register();
 * </pre>
 *
 * <pre>
 *     manager.group().prefix("!").middleware((command, args, context) -&gt; {
 *         context.getChannel().sendMessage("A command was called").queue();
 *     }).apply(() -&gt; {
 *         manager.make("mycommand", MyHandler.class).register();
 *         manager.make("myothercommand", MyOtherHandler.class).register();
 *     });
 * </pre>
 *
 * Don't forget to call the register function at the end of the builder.
 *
 * @author Litarvan
 * @version 2.1.0
 * @since 2.0.0
 */
@Singleton
public class CommandManager
{
    private static final Logger LOGGER = LogManager.getLogger("CommandManager");

    private JDA jda;
    private ExceptionHandler exHandler;
    private List<Command> commands;
    private List<CommandGroup> stack;

    @Inject
    public CommandManager(JDA jda, ExceptionHandler exHandler)
    {
        this.jda = jda;
        this.exHandler = exHandler;
        this.commands = new ArrayList<>();
        this.stack = new ArrayList<>();

        this.jda.addEventListener(this);
    }

    /**
     * Create a group builder.<br><br>
     *
     * A command group is some properties that will be applied
     * to some commands at the same time.<br><br>
     *
     * <b>Example :</b>
     *
     * <pre>
     *     manager.group().prefix("!").middlewares(MyMiddleware.class).apply(() -&gt; {
     *         manager.make("mycommand", MyHandler.class).register();
     *         manager.make("myothercommand", MyOtherHandler.class).register();
     *     });
     * </pre>
     *
     * In this case, the label of mycommand and myothercommand will be
     * !mycommand and !myothercommand, and they will both have the MyMiddleware
     * middleware triggered before their call.
     *
     * @return A new group builder linked to this
     */
    public GroupBuilder group()
    {
        return new GroupBuilder(this);
    }

    /**
     * Generate a CommandBuilder linked to this, with pre-defined
     * path and command handler.
     *
     * @param path The path of the command (see {@link CommandBuilder#path(String)}
     *             for the syntax
     * @param commandCl The command handler (will be created by the injector)
     *
     * @return A new CommandBuilder
     */
    public CommandBuilder make(String path, Class<? extends CommandHandler> commandCl)
    {
        return make(path, Krobot.injector().getInstance(commandCl));
    }

    /**
     * Generate a CommandBuilder linked to this, with pre-defined
     * path and command handler.
     *
     * @param path The path of the command (see {@link CommandBuilder#path(String)}
     *             for the syntax
     * @param handler The command handler
     *
     * @return A new CommandBuilder
     */
    public CommandBuilder make(String path, CommandHandler handler)
    {
        CommandBuilder builder = make(handler);
        StringBuilder prefix = new StringBuilder();

        for (CommandGroup group : stack)
        {
            if (group.getPrefix() != null)
            {
                prefix.append(group.getPrefix());
            }

            if (group.getParent() != null)
            {
                builder.parent(group.getParent());
            }

            builder.middlewares(group.getMiddlewares().toArray(new Middleware[group.getMiddlewares().size()]));
        }

        return builder.path(prefix.toString() + path);
    }

    /**
     * Generate a CommandBuilder linked to this, with a pre-defined
     * command handler.
     *
     * @param handler The command handler
     *
     * @return A new CommandBuilder
     */
    public CommandBuilder make(CommandHandler handler)
    {
        return new CommandBuilder(this, jda, exHandler).handler(handler);
    }

    /**
     * Register a command
     *
     * @param command The command to register
     */
    public void register(Command command)
    {
        LOGGER.info("Registered command -> " + command.toString("", true));
        this.commands.add(command);
    }

    /**
     * Push a command group to the stack.<br>
     * The next command registered will have the group properties
     * applied.
     *
     * @param group The group to push
     */
    public void push(CommandGroup group)
    {
        stack.add(group);
    }

    /**
     * Pop the last group of the stack
     */
    public void pop()
    {
        stack.remove(stack.size() - 1);
    }

    @SubscribeEvent
    protected void onMessage(MessageReceivedEvent event)
    {
        String[] line = splitWithQuotes(event.getMessage().getContent());

        if (line.length == 0)
        {
            return;
        }

        for (Command command : commands)
        {
            if (command.getLabel().equalsIgnoreCase(line[0]))
            {
                CommandContext context = new CommandContext(event.getAuthor(), event.getMessage(), event.getTextChannel());
                List<String> args = Arrays.asList(ArrayUtils.subarray(line, 1, line.length));

                try
                {
                    command.call(context, args);
                }
                catch (Throwable t)
                {
                    exHandler.handle(t, command, args, context);
                }

                return;
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
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = regex.matcher(line);

        while (matcher.find())
        {
            if (matcher.group(1) != null)
            {
                matchList.add(matcher.group(1));
            }
            else if (matcher.group(2) != null)
            {
                matchList.add(matcher.group(2));
            }
            else
            {
                matchList.add(matcher.group());
            }
        }

        return matchList.toArray(new String[matchList.size()]);
    }

    /**
     * @return The registered commands
     */
    public List<Command> getCommands()
    {
        return commands;
    }
}
