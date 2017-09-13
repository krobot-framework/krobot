package org.krobot.console;

import java.io.IOException;
import java.util.List;
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
import org.krobot.runtime.KrobotRuntime;

public class ConsoleProcessor extends Thread
{
    private Terminal terminal;
    private LineReader reader;

    private boolean typing = false;

    private Highlighter highlighter;
    private Completer completer;

    public ConsoleProcessor(Highlighter highlighter, Completer completer)
    {
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
                String line = reader.readLine();
                System.out.println("Commande : " + line);
            }
            catch (UserInterruptException e)
            {
                KrobotRuntime.stop();
                System.exit(0);
            }
        }
    }

    protected void complete(LineReader reader, ParsedLine line, List<Candidate> candidates)
    {
        if (line.line().equals("h"))
        {
            candidates.add(new Candidate("help"));
        }
    }

    public boolean isTyping()
    {
        return typing;
    }
}
