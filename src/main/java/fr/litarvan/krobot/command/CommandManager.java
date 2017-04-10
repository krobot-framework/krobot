package fr.litarvan.krobot.command;

import fr.litarvan.krobot.ExceptionHandler;
import fr.litarvan.krobot.Krobot;
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

    public GroupBuilder group()
    {
        return new GroupBuilder(this);
    }

    public CommandBuilder make(String path, Class<? extends CommandHandler> commandCl)
    {
        return make(path, Krobot.injector().getInstance(commandCl));
    }

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

    public CommandBuilder make(CommandHandler handler)
    {
        return new CommandBuilder(this).handler(handler);
    }

    public void register(Command command)
    {
        LOGGER.info("Registered command -> " + command.toString(""));
        this.commands.add(command);
    }

    public void push(CommandGroup group)
    {
        stack.add(group);
    }

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

                try
                {
                    command.call(context, Arrays.asList(ArrayUtils.subarray(line, 1, line.length)));
                }
                catch (Throwable t)
                {
                    exHandler.handle(t, command, context);
                }

                return;
            }
        }
    }

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

    public List<Command> getCommands()
    {
        return commands;
    }
}
