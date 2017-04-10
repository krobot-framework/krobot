package fr.litarvan.krobot.command;

public class UserNotFoundException extends Exception
{
    private String user;

    public UserNotFoundException(String user)
    {
        super("Can't find user '" + user + "'");
    }

    public String getUser()
    {
        return user;
    }
}
