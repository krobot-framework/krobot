package fr.litarvan.krobot.command;

public class BadSyntaxException extends Exception
{
    public BadSyntaxException()
    {
        super("Bad command syntax");
    }
}
