package org.krobot.console;

import java.io.IOException;
import java.util.List;
import org.jline.reader.Candidate;
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

public class ConsoleCommandProcessor extends Thread
{
    private KrobotRuntime runtime;

    private Terminal terminal;
    private LineReader reader;

    private boolean typing = false;

    public ConsoleCommandProcessor(KrobotRuntime runtime)
    {
        this.runtime = runtime;
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
                    .highlighter(this::highlight)
                    .completer(this::complete)
                    //.history(new ConsoleHistory())
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
                runtime.stop();
                System.exit(0);
            }
        }
    }

    protected AttributedString highlight(LineReader reader, String buffer)
    {
        AttributedStringBuilder builder = new AttributedStringBuilder();

        if (!buffer.isEmpty())
        {
            if (!typing)
            {
                typing = true;
                System.out.println("\r                                                                                                                         ");
            }

            // Deleted because else the cursor is too buggy
            // builder.append("> ");
        }
        else
        {
            if (typing)
            {
                typing = false;
            }
        }

        for (String word : buffer.split(" "))
        {
            if (word.equals("help"))
            {
                builder.style(AttributedStyle.BOLD.foreground(AttributedStyle.BLUE));
                builder.append(word);
                builder.style(AttributedStyle.BOLD_OFF);
            }
            else
            {
                builder.append(word);
            }

            builder.append(" ");
        }

        return builder.toAttributedString();
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
