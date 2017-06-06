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

import org.krobot.util.UserUtils;
import java.util.List;
import net.dv8tion.jda.core.entities.User;

/**
 * A Supplied Argument<br><br>
 *
 *
 * An argument that was given to a command.
 * Like an instance of a {@link CommandArgument}.
 *
 * @author Litarvan
 * @version 2.1.0
 * @since 2.0.0
 */
public class SuppliedArgument
{
    private ArgumentType type;

    private User user;
    private String stringValue;
    private int numberValue;
    private List listValue;

    /**
     * A User argument
     *
     * @param user The given user
     */
    public SuppliedArgument(User user)
    {
        this.type = ArgumentType.USER;
        this.user = user;
    }

    /**
     * A String argument
     *
     * @param stringValue The value of the argument
     */
    public SuppliedArgument(String stringValue)
    {
        this.type = ArgumentType.STRING;
        this.stringValue = stringValue;
    }

    /**
     * A number argument
     *
     * @param numberValue The value of the argument
     */
    public SuppliedArgument(int numberValue)
    {
        this.type = ArgumentType.NUMBER;
        this.numberValue = numberValue;
    }

    /**
     * A list argument
     *
     * @param list The given list
     * @param type The type of the list
     */
    public SuppliedArgument(List list, ArgumentType type)
    {
        this.type = type;
        this.listValue = list;
    }

    /**
     * @return The string value of the argument.<br><br>
     *
     * If it is a {@link ArgumentType#USER}, returns its Username.<br>
     * If it is a number, returns its String value.
     */
    public String getAsString()
    {
        switch (this.type)
        {
            case USER:
                return user.getName();
            case STRING:
                return stringValue;
            case NUMBER:
                return String.valueOf(numberValue);
        }

        return null;
    }

    /**
     * @throws IllegalStateException If it is a {@link ArgumentType#USER} or a {@link ArgumentType#STRING}
     *
     * @return The int value of the argument.
     */
    public int getAsNumber()
    {
        switch (this.type)
        {
            case USER:
                throw new IllegalStateException("Cannot convert User argument to Number argument");
            case STRING:
                throw new IllegalStateException("Cannot convert String argument to Number argument");
            case NUMBER:
                return numberValue;
        }

        return 0;
    }

    /**
     * @throws IllegalStateException If it is a {@link ArgumentType#NUMBER}
     *
     * @return The argument as a User.<br><br>
     *
     * If it is a {@link ArgumentType#STRING}, tries to resolve it.
     */
    public User getAsUser()
    {
        switch (this.type)
        {
            case USER:
                return this.user;
            case STRING:
                return UserUtils.resolve(this.stringValue);
            case NUMBER:
                throw new IllegalStateException("Cannot convert Number argument to User argument");
        }

        return null;
    }

    /**
     * @throws IllegalStateException If it isn't a list of user
     *
     * @return The argument as a User list.
     */
    public List<User> getAsUserList()
    {
        if (this.listValue == null)
        {
            throw new IllegalStateException("Cannot convert " + this.type.name().toLowerCase() + " to list");
        }

        if (this.type != ArgumentType.USER)
        {
            throw new IllegalStateException("Cannot convert " + this.type.name().toLowerCase() + " list to User list");
        }

        return listValue;
    }

    /**
     * @throws IllegalStateException If it isn't a list of string
     *
     * @return The argument as a string list.
     */
    public List<String> getAsStringList()
    {
        if (this.listValue == null)
        {
            throw new IllegalStateException("Cannot convert " + this.type.name().toLowerCase() + " to list");
        }

        if (this.type != ArgumentType.STRING)
        {
            throw new IllegalStateException("Cannot convert " + this.type.name().toLowerCase() + " list to String list");
        }

        return listValue;
    }

    /**
     * @throws IllegalStateException If it isn't a list of number
     *
     * @return The argument as a number list.
     */
    public List<Integer> getAsNumberList()
    {
        if (this.listValue == null)
        {
            throw new IllegalStateException("Cannot convert " + this.type.name().toLowerCase() + " to list");
        }

        if (this.type != ArgumentType.NUMBER)
        {
            throw new IllegalStateException("Cannot convert " + this.type.name().toLowerCase() + " list to Number list");
        }

        return listValue;
    }

    @Override
    public String toString()
    {
        StringBuilder string = new StringBuilder("[" + type.name().toLowerCase() + "]: ");

        if (listValue != null)
        {
            string.append("{");

            for (Object object : listValue)
            {
                switch (type)
                {
                    case USER:
                        User user = (User) object;
                        string.append(user.getName()).append("#").append(user.getDiscriminator());
                        break;
                    case NUMBER:
                        string.append(object);
                        break;
                    case STRING:
                        string.append(object);
                        break;
                }

                string.append(", ");
            }

            return string.substring(0, string.length() - 2) + "}";
        }

        switch (type)
        {
            case USER:
                string.append(user.getName()).append("#").append(user.getDiscriminator());
                break;
            case NUMBER:
                string.append(numberValue);
                break;
            case STRING:
                string.append(stringValue);
                break;
        }

        return string.toString();
    }
}
