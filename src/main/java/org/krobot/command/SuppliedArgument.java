package org.krobot.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuppliedArgument
{
    private static final Logger logger = LogManager.getLogger("SuppliedArgument");

    private Object value;
    private String[] matches;

    public SuppliedArgument(Object value)
    {
        this.value = value;
    }

    public SuppliedArgument(String[] matches)
    {
        this.matches = matches;
    }

    public int asInt()
    {
        if (value instanceof String)
        {
            logger.warn("Trying to get a string as an integer, parsing it");
            return Integer.parseInt(asString());
        }

        return as(int.class);
    }

    public float asFloat()
    {
        if (value instanceof String)
        {
            logger.warn("Trying to get a string as an float, parsing it");
            return Float.parseFloat(asString());
        }

        return as(float.class);
    }

    public long asLong()
    {
        if (value instanceof String)
        {
            logger.warn("Trying to get a string as an long, parsing it");
            return Long.parseLong(asString());
        }

        return as(long.class);
    }

    public double asDouble()
    {
        if (value instanceof String)
        {
            logger.warn("Trying to get a double as an double, parsing it");
            return Double.parseDouble(asString());
        }

        return as(double.class);
    }

    public String asString()
    {
        if (value == null)
        {
            logger.warn("Trying to get regex matches as a string; using the first match");
            return matches[0];
        }

        if (!(value instanceof String))
        {
            logger.warn("Trying to get a '" + value.getClass().getSimpleName() + "' argument as a string; using .toString()");
            return value.toString();
        }

        return as(String.class);
    }

    public <T> T as(Class<T> type)
    {
        if (value == null)
        {
            logger.error("Tried to get a regex argument as a value, returning null -> this is bad, '" + type.getSimpleName() + "' was requested");
            return null;
        }

        try
        {
            return (T) value;
        }
        catch (ClassCastException e)
        {
            String real = value.getClass().getSimpleName();
            String trial = type.getSimpleName();

            String from = real.equalsIgnoreCase(trial) ? value.getClass().getName() : real;
            String to = real.equalsIgnoreCase(trial) ? type.getName() : trial;

            throw new IllegalArgumentException("Argument conversion failure, tried to convert '" + from + "' to '" + to + "'", e);
        }
    }

    public String[] asMatches()
    {
        if (matches == null)
        {
            logger.error("Tried to get a value argument as regex matches, returning null -> this is bad");
            return null;
        }

        return this.matches;
    }
}
