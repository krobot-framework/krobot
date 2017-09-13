package org.krobot.console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.krobot.command.CommandManager;

public class ConsoleHighlighter implements Highlighter
{
    private KrobotConsole console;

    public ConsoleHighlighter(KrobotConsole console)
    {
        this.console = console;
    }

    @Override
    public AttributedString highlight(LineReader reader, String buffer)
    {
        AttributedStringBuilder builder = new AttributedStringBuilder();

        if (!buffer.isEmpty())
        {
            if (!console.isProcessing())
            {
                console.setProcessing(true);

                // Ok this is dirty, but it is the only way i found to hide the State Bar :p
                System.out.println("\r                                                                                                                         ");
            }

            // Deleted because else the cursor is too buggy
            // builder.append("> ");
        }
        else
        {
            console.setProcessing(false);
        }

        String[] split = splitWithQuotes(buffer);

        if (split.length == 0)
        {
            return new AttributedString("");
        }

        String label = split[0];
        int color = AttributedStyle.RED;

        for (ComputedConsoleCommand c : console.getCommands())
        {
            if (c.getLabel().equalsIgnoreCase(label))
            {
                color = AttributedStyle.BLUE;
                break;
            }
            else if (StringUtils.startsWithIgnoreCase(c.getLabel(), label))
            {
                color = AttributedStyle.YELLOW;
            }
        }

        builder.style(AttributedStyle.BOLD.foreground(color));
        builder.append(label);

        builder.style(AttributedStyle.DEFAULT);
        builder.append(" ");

        for (int i = 1; i < split.length; i++)
        {
            String arg = split[i];

            if (arg.startsWith("\"") && (arg.endsWith("\"") || i == split.length - 1))
            {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
            }
            else if (arg.startsWith("@"))
            {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold());
            }
            else if (StringUtils.isNumeric(arg))
            {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).italic());
            }

            builder.append(arg);
            builder.style(AttributedStyle.DEFAULT);
            builder.append(" ");
        }

        return builder.toAttributedString();
    }

    public static String[] splitWithQuotes(String string)
    {
        List<String> result = new ArrayList<>();
        String[] split = string.split(" ");

        for (int i = 0; i < split.length; i++)
        {
            StringBuilder current = new StringBuilder(split[i]);

            if (current.toString().startsWith("\""))
            {
                i++;

                while (i < split.length && !current.toString().endsWith("\""))
                {
                    current.append(" ").append(split[i]);
                    i++;
                }

                i--;
            }

            String done = current.toString();

            if (!done.endsWith("\""))
            {
                result.addAll(Arrays.asList(done.split(" ")));
            }
            else
            {
                result.add(done);
            }
        }

        return result.toArray(new String[result.size()]);
    }
}
