/*
 * Copyright 2017 The Krobot Contributors
 *
 * This file is part of Krobot.
 *
 * Krobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krobot.  If not, see <http://www.gnu.org/licenses/>.
 */
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
            next = next("'...', '|' or '" + end + "'");
        }

        ArgumentFactory factory = null;
        boolean list = false;

        if (type != null && next == '|')
        {
            List<String> choices = new ArrayList<>();
            choices.add(type);

            while (next == '|')
            {
                choices.add(readWord());
                next = next("'|' or '" + end + "'");
            }

            type = String.join("|", choices);
            String finalType = type;

            factory = new ArgumentFactory<String>()
            {
                @Override
                public String process(String argument) throws BadArgumentTypeException
                {
                    if (!choices.contains(argument))
                    {
                        throw new BadArgumentTypeException("Can only be one of : " + String.join(", ", choices) + "; but not '" + argument + "'", argument, finalType);
                    }

                    return argument;
                }

                @Override
                public String[] createArray()
                {
                    return new String[0];
                }
            };
        }
        else
        {
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
        }

        if (args.size() > 0)
        {
            CommandArgument last = args.get(args.size() - 1);

            if (required && !last.isRequired())
            {
                throw error("Can't put a required argument after an optional one", start, cursor);
            }
            else if (last.isList())
            {
                throw error("List argument must stay the last one", start, cursor);
            }
        }

        type = type == null ? "string" : type;
        factory = factory == null ? CommandManager.getArgumentFactory(type) : factory;

        if (factory == null)
        {
            throw error("Unknown argument type '" + type + "'", cursor - type.length(), cursor - 1);
        }

        args.add(new CommandArgument(required, name, type, factory, list));
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
