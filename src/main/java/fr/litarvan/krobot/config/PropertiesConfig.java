/*
 * Copyright 2017 Adrien "Litarvan" Navratil
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
package fr.litarvan.krobot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 * The Properties Config
 *
 *
 * A Config made using Java Properties.
 * To create one, consider using the {@link ConfigProvider#properties}
 * methods.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public class PropertiesConfig extends FileConfig
{
    private Properties properties = new Properties();

    public PropertiesConfig()
    {
    }

    public PropertiesConfig(File file)
    {
        super(file);
    }

    @Override
    public String get(String key, String def)
    {
        return properties.getProperty(key, def);
    }

    @Override
    public void set(String key, String value)
    {
        properties.setProperty(key, value);

        if (autoSave)
        {
            save();
        }
    }

    @Override
    public boolean areObjectsSupported()
    {
        return false;
    }

    @Override
    public FileConfig load()
    {
        if (file == null)
        {
            throw new IllegalStateException("Config file isn't defined");
        }

        try
        {
            properties.load(new FileInputStream(file));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't read config", e);
        }

        return this;
    }

    @Override
    public FileConfig save()
    {
        try
        {
            properties.store(new FileOutputStream(file), "Krobot generated config\n");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't save config", e);
        }

        return this;
    }
}
