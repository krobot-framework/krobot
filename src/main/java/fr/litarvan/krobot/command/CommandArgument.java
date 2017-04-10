/*
 * Copyright 2017 Adrien "Litarvan" Navratil
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
package fr.litarvan.krobot.command;

public class CommandArgument
{
    private String key;
    private boolean optional;
    private boolean list;
    private String[] choices;
    private ArgumentType type;

    public CommandArgument(String key, boolean optional, boolean list, ArgumentType type)
    {
        this.key = key;
        this.optional = optional;
        this.list = list;
        this.type = type;
    }

    public CommandArgument(String key, boolean optional, String[] choices)
    {
        this.key = key;
        this.optional = optional;
        this.list = false;
        this.choices = choices;
        this.type = ArgumentType.STRING;
    }

    public String getKey()
    {
        return key;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public boolean isList()
    {
        return list;
    }

    public String[] getChoices()
    {
        return choices;
    }

    public ArgumentType getType()
    {
        return type;
    }

    @Override
    public String toString()
    {
        String start = (optional ? "[" : "<");
        String end = (optional ? "]" : ">");

        if (choices != null)
        {
            return start + String.join("|", choices) + end;
        }

        return start + this.key + ":" + this.type.name().toLowerCase() + (list ? "..." : "") + end;
    }
}
