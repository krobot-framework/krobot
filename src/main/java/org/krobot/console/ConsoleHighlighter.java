package org.krobot.console;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.krobot.util.MessageUtils;

import java.util.regex.Pattern;

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

                if (console.getRuntime().getStateBar() != null)
                {
                    // Ok this is dirty, but it is the only way i found to hide the State Bar :p
                    System.out.println("\r                                                                                                                         ");
                }
            }

            // Deleted because else the cursor is too buggy
            // builder.append("> ");
        }
        else
        {
            console.setProcessing(false);
        }

        String[] split = MessageUtils.splitWithQuotes(buffer, true);

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

        boolean inQuotes = false;

        for (int i = 1; i < split.length; i++)
        {
            String arg = split[i];

            if (arg.startsWith("\""))
            {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
                inQuotes = true;
            }
            else if (arg.startsWith("@") && !inQuotes)
            {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold());
            }
            else if (StringUtils.isNumeric(arg) && !inQuotes)
            {
                builder.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN).italic());
            }

            if (arg.endsWith("\""))
            {
                inQuotes = false;
            }

            builder.append(arg);

            if (!inQuotes)
            {
                builder.style(AttributedStyle.DEFAULT);
            }

            builder.append(" ");
        }

        return builder.toAttributedString();
    }

    @Override
    public void setErrorPattern(Pattern errorPattern)
    {
    }

    @Override
    public void setErrorIndex(int errorIndex)
    {
    }
}
