/*
 * Copyright 2017 The Krobot Contributors
 *
 * This file is part of Krobot.
 *
 * Krobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krobot.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.krobot.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import net.dv8tion.jda.api.entities.Icon;
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
