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

public class KrobotCommand
{
    private String label;
    private String[] aliases;
    private CommandArgument[] arguments;
    private String description;
    private boolean errorMP; //error in private message
    private boolean handleMP; //private message only
    private List<CommandFilter> filters;
    private List<KrobotCommand> subs;
    private CommandHandler handler;


    public KrobotCommand(String label, CommandArgument[] arguments, CommandHandler handler)
    {
        this(label, arguments, handler, new ArrayList<>());
    }

    public KrobotCommand(String label, CommandArgument[] arguments, CommandHandler handler, List<KrobotCommand> subs)
    {
        this(label, arguments, "", new String[] {}, false, false, new ArrayList<>(), handler, new ArrayList<>());
    }

    public KrobotCommand(String label, CommandArgument[] arguments, String description, String[] aliases, boolean errorMP, boolean handleMP, List<CommandFilter> filters, CommandHandler handler, List<KrobotCommand> subs)
    {
        this.label = label;
        this.aliases = aliases;
        this.arguments = arguments;
        this.description = description;
        this.errorMP = errorMP;
        this.handleMP = handleMP;
        this.filters = filters;
        this.subs = subs;
        this.handler = handler;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public void setAliases(String[] aliases)
    {
        this.aliases = aliases;
    }

    public CommandArgument[] getArguments()
    {
        return arguments;
    }

    public void setArguments(CommandArgument[] arguments)
    {
        this.arguments = arguments;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean getErrorMP()
    {
    	return errorMP;
    }

    public void setErrorMP(boolean errorMP)
    {
    	this.errorMP = errorMP;
    }

    public boolean getHandleMP()
    {
    	return handleMP;
    }

    public void setHandleMP(boolean handleMP)
    {
    	this.handleMP = handleMP;
    }

    public List<CommandFilter> getFilters()
    {
        return filters;
    }

    public List<KrobotCommand> getSubCommands()
    {
        return subs;
    }

    public CommandHandler getHandler()
    {
        return handler;
    }

    public void setHandler(CommandHandler handler)
    {
        this.handler = handler;
    }

    @Override
    public String toString()
    {
        return toString("", true);
    }

    /**
     * Convert the command to a displayable string
     * (similar to the {@link PathCompiler} syntax)
     *
     * @param prefix The prefix to add (like tabs)
     * @param subs If the subs should be displayed too
     *
     * @return The generated string
     */
    public String toString(String prefix, boolean subs)
    {
        StringBuilder string = new StringBuilder(prefix + this.label + " ");

        for (CommandArgument argument : arguments)
        {
            string.append(argument).append(" ");
        }

        if (subs && this.subs != null && this.subs.size() != 0)
        {
            for (KrobotCommand sub : this.subs)
            {
                string.append("\n");
                string.append(prefix).append(sub.toString(prefix + this.getLabel() + " ", true));
            }
        }

        return string.toString();
    }
}
