package org.krobot.command;

public class BadArgumentTypeException extends Exception
{
    private String value;
    private String type;

    public BadArgumentTypeException(String value, String type)
    {
        this.value = value;
        this.type = type;
    }

    public BadArgumentTypeException(String s, String value, String type)
    {
        super(s);

        this.value = value;
        this.type = type;
    }
}
