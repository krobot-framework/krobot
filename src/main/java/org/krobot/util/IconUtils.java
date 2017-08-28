package org.krobot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.dv8tion.jda.core.entities.Icon;
import org.fusesource.jansi.Ansi.Color;

public final class IconUtils
{
    private static final ColoredLogger log = ColoredLogger.getLogger("IconUtils");

    public static Icon readFromClasspath(String path)
    {
        path = (path.startsWith("/") ? "" : "/") + path;

        try
        {
            InputStream source = IconUtils.class.getResourceAsStream(path);

            if (source == null)
            {
                throw new FileNotFoundException();
            }

            return Icon.from(source);
        }
        catch (FileNotFoundException e)
        {
            log.warn(Color.YELLOW, "Couldn't find requested icon in classpath at '{}'; ignoring", path);
        }
        catch (IOException e)
        {
            log.warn(Color.YELLOW, "Couldn't read requested icon in classpath at '{}'; ignoring", path);
            e.printStackTrace();
        }

        return null;
    }

    public static Icon readFromFile(String path)
    {
        return readFromFile(new File(path));
    }

    public static Icon readFromFile(File file)
    {
        try
        {
            return Icon.from(file);
        }
        catch (IllegalArgumentException e)
        {
            log.warn(Color.YELLOW, "Couldn't find requested icon at '{}'; ignoring", file);
        }
        catch (IOException e)
        {
            log.warn(Color.YELLOW, "Couldn't read requested icon at '{}'; ignoring", file);
            e.printStackTrace();
        }

        return null;
    }
}
