package org.krobot.command;

public class WrongArgumentNumberException extends Exception
{
    private KrobotCommand command;
    private int given;

    public WrongArgumentNumberException(KrobotCommand command, int given)
    {
        super("Wrong number of argument for command '" + command.getLabel() + "', " + given + " were given but " + command.getArguments().length + " are required");

        this.command = command;
        this.given = given;
    }

    public KrobotCommand getCommand()
    {
        return command;
    }

    public int getGiven()
    {
        return given;
    }
}
