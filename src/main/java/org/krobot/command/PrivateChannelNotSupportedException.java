package org.krobot.command;

public class PrivateChannelNotSupportedException extends Exception
{
    public PrivateChannelNotSupportedException()
    {
        super("This command cannot be called from a private message channel");
    }
}
