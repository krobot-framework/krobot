package org.krobot.command.runtime;

import org.krobot.command.KrobotCommand;

public class WrongArgumentNumberException extends Exception
{
    public WrongArgumentNumberException(KrobotCommand command, int given)
    {
        super("Wrong number of argument for command '" + command.getLabel() + "', " + given + " were given but " + command.getArguments().length + " are required");
    }
}
