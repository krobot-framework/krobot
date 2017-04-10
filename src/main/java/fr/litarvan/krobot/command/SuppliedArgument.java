package fr.litarvan.krobot.command;

import fr.litarvan.krobot.util.UserUtils;
import java.util.List;
import net.dv8tion.jda.core.entities.User;

public class SuppliedArgument
{
    private ArgumentType type;

    private User user;
    private String stringValue;
    private int numberValue;
    private List listValue;

    public SuppliedArgument(User user)
    {
        this.type = ArgumentType.USER;
        this.user = user;
    }

    public SuppliedArgument(String stringValue)
    {
        this.type = ArgumentType.STRING;
        this.stringValue = stringValue;
    }

    public SuppliedArgument(int numberValue)
    {
        this.type = ArgumentType.NUMBER;
        this.numberValue = numberValue;
    }

    public SuppliedArgument(List list, ArgumentType type)
    {
        this.type = type;
        this.listValue = list;
    }

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

    public int getAsNumber()
    {
        switch (this.type)
        {
            case USER:
                throw new IllegalStateException("Cannot convert User argument to Number argument");
            case STRING:
                return Integer.parseInt(stringValue);
            case NUMBER:
                return numberValue;
        }

        return 0;
    }

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
                        string.append(((User) object).getDiscriminator());
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
