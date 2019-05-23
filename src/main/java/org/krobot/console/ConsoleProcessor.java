package org.krobot.console;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.jline.terminal.impl.DumbTerminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.krobot.command.ArgumentMap;
import org.krobot.command.BadArgumentTypeException;
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

        boolean unicorn = !(terminal instanceof DumbTerminal);

        reader = LineReaderBuilder.builder()
                    .appName("Krobot")
                    .terminal(terminal)
                    .highlighter(unicorn ? highlighter : null)
                    .completer(unicorn ? completer : null)
                    .build();

        while (!this.isInterrupted())
        {
            try
            {
                parse(reader.readLine(console.getRuntime().getStateBar() == null ? "> " : null));
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

        if (label.trim().isEmpty())
        {
            return;
        }

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
            System.out.println(Ansi.ansi().render("@|red Unknown command|@ @|bold,red '" + label + "'|@\n").toString());
            return;
        }

        Map<String, Object> mappedArgs = new HashMap<>();
        for (int i = 0; i < command.getArguments().length; i++)
        {
            CommandArgument arg = command.getArguments()[i];

            if (i > args.length - 1)
            {
                System.out.println(Ansi.ansi().render("@|red Missing " + (i - (args.length - 1)) + " arguments. Syntax :|@ @|bold,red '" + command.getCommand().getPath() + "'|@\n"));
                return;
            }

            String argument = args[i];
            try
            {
                mappedArgs.put(arg.getKey(), arg.getFactory().process(argument));
            }
            catch (BadArgumentTypeException e)
            {
                System.out.println(Ansi.ansi().render("@|red Cannot convert '|@@|bold,red " + e.getValue() + "|@@|red ' to a '|@@|bold,red " + e.getType() + "|@@|red '. Syntax :|@ @|bold,red '" + command.getCommand().getPath() + "'|@\n"));
                return;
            }
        }

        command.getCommand().execute(new ArgumentMap(mappedArgs));

        System.out.println();
    }
}
