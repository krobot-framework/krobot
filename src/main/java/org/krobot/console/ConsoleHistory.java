package org.krobot.console;

import java.io.IOException;
import java.time.Instant;
import java.util.ListIterator;
import org.jline.reader.History;
import org.jline.reader.LineReader;

public class ConsoleHistory implements History
{
    @Override
    public void attach(LineReader reader)
    {

    }

    @Override
    public void load() throws IOException
    {

    }

    @Override
    public void save() throws IOException
    {

    }

    @Override
    public void purge() throws IOException
    {

    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public int index()
    {
        return 0;
    }

    @Override
    public int first()
    {
        return 0;
    }

    @Override
    public int last()
    {
        return 0;
    }

    @Override
    public String get(int index)
    {
        return null;
    }

    @Override
    public void add(Instant time, String line)
    {

    }

    @Override
    public ListIterator<Entry> iterator(int index)
    {
        return null;
    }

    @Override
    public String current()
    {
        return null;
    }

    @Override
    public boolean previous()
    {
        return false;
    }

    @Override
    public boolean next()
    {
        return false;
    }

    @Override
    public boolean moveToFirst()
    {
        return false;
    }

    @Override
    public boolean moveToLast()
    {
        return false;
    }

    @Override
    public boolean moveTo(int index)
    {
        return false;
    }

    @Override
    public void moveToEnd()
    {

    }
}
