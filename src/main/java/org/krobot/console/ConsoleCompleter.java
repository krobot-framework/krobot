package org.krobot.console;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

public class ConsoleCompleter implements Completer
{
    private KrobotConsole console;

    public ConsoleCompleter(KrobotConsole console)
    {
        this.console = console;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates)
    {
        List<String> words = line.words();

        if (words.size() == 0 || words.size() == 1)
        {
            for (ComputedConsoleCommand command : console.getCommands())
            {
                if (StringUtils.startsWithIgnoreCase(command.getLabel(), line.word()))
                {
                    candidates.add(new Candidate(command.getLabel()));
                }
            }

            return;
        }

        // Argument completion
    }
}
