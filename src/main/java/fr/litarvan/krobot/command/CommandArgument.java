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

/**
 * A Command Argument
 *
 *
 * Represents a definition of an argument that can be given
 * to a command.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public class CommandArgument
{
    private String key;
    private boolean optional;
    private boolean list;
    private String[] choices;
    private ArgumentType type;

    /**
     * An argument
     *
     * @param key The key of the argument (to retrieve it then)
     * @param optional If it is optional or required
     * @param list If it is a list
     * @param type The type of the argument
     */
    public CommandArgument(String key, boolean optional, boolean list, ArgumentType type)
    {
        this.key = key;
        this.optional = optional;
        this.list = list;
        this.type = type;
    }

    /**
     * An fixed "choices" argument
     *
     * @param key The key of the argument (to retrieve it then)
     * @param optional If it is optional or required
     * @param choices The choices that the user can type
     */
    public CommandArgument(String key, boolean optional, String[] choices)
    {
        this.key = key;
        this.optional = optional;
        this.list = false;
        this.choices = choices;
        this.type = ArgumentType.STRING;
    }

    /**
     * @return The key of the argument (to retrieve it then)
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return If it is optional or required
     */
    public boolean isOptional()
    {
        return optional;
    }

    /**
     * @return If it is a list
     */
    public boolean isList()
    {
        return list;
    }

    /**
     * @return (In case of a fixed choices argument) The choices
     * that the user can type
     */
    public String[] getChoices()
    {
        return choices;
    }

    /**
     * @return The type of the argument
     */
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
