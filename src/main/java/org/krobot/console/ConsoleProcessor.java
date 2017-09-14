package org.krobot.console;

import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.fusesource.jansi.Ansi;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.Highlighter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.krobot.command.CommandArgument;
import org.krobot.runtime.KrobotRuntime;
import org.krobot.util.MessageUtils;

public class ConsoleProcessor extends Thread
{
    private Terminal terminal;
    private LineReader reader;

    private KrobotConsole console;

    private Highlighter highlighter;
    private Completer completer;

    public ConsoleProcessor(KrobotConsole console, Highlighter highlighter, Completer completer)
    {
        this.console = console;

        this.highlighter = highlighter;
        this.completer = completer;
    }

    @Override
    public void run()
    {
        try
        {
            terminal = TerminalBuilder.terminal();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        reader = LineReaderBuilder.builder()
                    .appName("Krobot")
                    .terminal(terminal)
                    .highlighter(highlighter)
                    .completer(completer)
                    .build();

        while (!this.isInterrupted())
        {
            try
            {
                parse(reader.readLine());
            }
            catch (UserInterruptException e)
            {
                KrobotRuntime.stop();
                System.exit(0);
            }
        }
    }

    protected void parse(String line)
    {
        String[] split = MessageUtils.splitWithQuotes(line, false);

        if (split.length == 0)
        {
            return;
        }

        String label = split[0];
        String[] args = split.length > 1 ? ArrayUtils.subarray(split, 1, split.length) : ArrayUtils.EMPTY_STRING_ARRAY;

        ComputedConsoleCommand command = null;

        for (ComputedConsoleCommand c : console.getCommands())
        {
            if (c.getLabel().equalsIgnoreCase(label))
            {
                command = c;
                break;
            }
        }

        if (command == null)
        {
            System.out.println(Ansi.ansi().render("@|red Unknown command|@ @|bold,red '" + label + "'|@").toString());
            return;
        }

        for (int i = 0; i < args.length; i++)
        {
            String arg = args[i];

            if (i > command.getArguments().length - 1)
            {

            }

            CommandArgument argument = command.getArguments()[i];
        }
    }
}
