package org.krobot.command;

import java.util.ArrayList;
import java.util.List;
import org.fusesource.jansi.Ansi.Color;
import org.krobot.util.ColoredLogger;

public class PathCompiler
{
    private static final ColoredLogger log = ColoredLogger.getLogger("PathCompiler");

    private String path;

    private int cursor;

    private String label;
    private List<CommandArgument> args;

    public PathCompiler(String path)
    {
        this.path = path;

        this.cursor = 0;
        this.args = new ArrayList<>();
    }

    public void compile()
    {
        label = readWord();

        while (true)
        {
            if (!hasNext())
            {
                break;
            }

            skipEnsure(' ');

            switch (next("'[' or '<'"))
            {
                case '<':
                    readArg(true);
                    break;
                case '[':
                    readArg(false);
                    break;
                default:
                    throw error("Expected '<' or '[' got '" + previous() + "'");
            }
        }
    }

    protected void readArg(boolean required)
    {
        int start = cursor;
        char end = required ? '>' : ']';

        String name = readWord();

        if (args.stream().anyMatch(arg -> arg.getKey().equalsIgnoreCase(name)))
        {
            throw error("Duplicated argument '" + name + "'", cursor - name.length() + 1, cursor);
        }

        String type = null;

        char next = next("':' or '" + end + "'");

        if (next == ':')
        {
            type = readWord();
            next = next("'...' or '" + end + "'");
        }

        boolean list = false;

        if (next == '.')
        {
            skipEnsure('.');
            skipEnsure('.');

            list = true;

            skipEnsure(end);
        }
        else if (next != end)
        {
            throw error("Expected " + (type == null ? "':' or " : "") + "'" + end + "' got '" + next + "'");
        }

        if (args.size() > 0)
        {
            CommandArgument last = args.get(args.size() - 1);

            if (required && !last.isRequired())
            {
                throw error("Can't put a required argument after one/multiple optional argument(s)", start, cursor);
            }
            else if (last.isList())
            {
                throw error("List argument must stay the last one", start, cursor);
            }
        }

        type = type == null ? "string" : type;

        ArgumentFactory factory = CommandManager.getArgumentFactory(type);

        if (factory == null)
        {
            throw error("Unknown argument type '" + type + "'", cursor - type.length(), cursor - 1);
        }

        args.add(new CommandArgument(required, name, factory, list));
    }

    protected void skipEnsure(char c)
    {
        char next = next("'" + c + "'");

        if (next != c)
        {
            throw error("Expected '" + c + "' got '" + next + "'");
        }
    }

    protected String readWord()
    {
        StringBuilder result = new StringBuilder();
        char current;

        while (cursor < path.length())
        {
            current = current();

            if (!Character.isLetterOrDigit(current) && current != '-')
            {
                break;
            }

            result.append(current);
            cursor++;
        }

        return result.toString();
    }

    protected boolean hasNext()
    {
        return cursor < path.length();
    }

    protected char previous()
    {
        return path.charAt(cursor - 1);
    }

    protected char current()
    {
        return path.charAt(cursor);
    }

    protected char next(String expectation)
    {
        if (cursor >= path.length())
        {
            throw error("Unexpected end of path, expected '" + expectation + "'");
        }

        char val = current();
        cursor++;

        return val;
    }

    protected PathSyntaxException error(String message)
    {
        return error(message, cursor);
    }

    protected PathSyntaxException error(String message, int to)
    {
        return error(message, cursor, to);
    }

    protected PathSyntaxException error(String message, int from, int to)
    {
        log.error("");
        log.errorAuto("@|bold,red Error:|@ @|bold,white {}|@", message);
        log.errorAuto("    @|bold,blue -->|@ {}", path);
        log.errorAuto("        " + repeat(' ', from - 1) + "@|bold,red ^" + repeat('^', to - from) + "|@");
        log.errorBold(Color.WHITE, "Aborting due to command path compilation error");

        return new PathSyntaxException();
    }

    protected String repeat(char c, int amount)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < amount; i++)
        {
            result.append(c);
        }

        return result.toString();
    }

    public String label()
    {
        return this.label;
    }

    public CommandArgument[] args()
    {
        return this.args.toArray(new CommandArgument[this.args.size()]);
    }

    public String getPath()
    {
        return path;
    }

    public static class PathSyntaxException extends RuntimeException
    {
    }
}
