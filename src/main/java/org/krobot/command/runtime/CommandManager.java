package org.krobot.command.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.krobot.command.CommandArgument;
import org.krobot.command.CommandFilter;
import org.krobot.command.KrobotCommand;
import org.krobot.runtime.KrobotRuntime;

public class CommandManager
{
    private KrobotRuntime runtime;

    private List<KrobotCommand> commands;
    private List<CommandFilter> filters;

    public CommandManager(KrobotRuntime runtime)
    {
        this.runtime = runtime;

        this.commands = new ArrayList<>();
        this.filters = new ArrayList<>();
    }

    public void handle(MessageContext context) throws Exception
    {
        String prefix = runtime.getFilterRunner().getPrefix(context);
        String[] split = splitWithQuotes(context.getMessage().getRawContent());

        if (!split[0].startsWith(prefix) || split[0].equalsIgnoreCase(prefix))
        {
            return;
        }

        String label = split[0].trim().substring(prefix.length(), split[0].length());

        // TODO: Subs

        // TODO: Aliases
        Optional<KrobotCommand> optional = commands.stream().filter(c -> label.equalsIgnoreCase(prefix + c.getLabel())).findFirst();

        if (!optional.isPresent())
        {
            return;
        }

        KrobotCommand command = optional.get();
        String[] args = ArrayUtils.subarray(split, 1, split.length);

        Map<String, Object> supplied = new HashMap<>();

        int i;

        for (i = 0; i < command.getArguments().length; i++)
        {
            CommandArgument arg = command.getArguments()[i];

            if (i > args.length - 1 && arg.isRequired())
            {
                throw new WrongArgumentNumberException(command, args.length);
            }

            supplied.put(arg.getKey(), arg.getFactory().process(args[0]));
        }

        if (i < args.length - 1)
        {
            throw new WrongArgumentNumberException(command, args.length);
        }

        // TODO: Filters

        command.getHandler().handle(context, new ArgumentMap(supplied));
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

    public List<KrobotCommand> getCommands()
    {
        return commands;
    }

    public List<CommandFilter> getFilters()
    {
        return filters;
    }
}
