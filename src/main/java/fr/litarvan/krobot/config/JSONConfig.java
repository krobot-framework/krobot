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

import com.google.common.io.Files;
import fr.litarvan.krobot.ExceptionHandler;
import fr.litarvan.krobot.Krobot;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import net.dv8tion.jda.core.utils.IOUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The JSON Config
 *
 *
 * A config made using JSON.
 * To create one, consider using the {@link ConfigProvider#json}
 * methods.
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public class JSONConfig extends FileConfig
{
    private JSONObject config;

    public JSONConfig()
    {
        super();
    }

    public JSONConfig(File file)
    {
        super(file);
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
            config = new JSONObject(new String(IOUtil.readFully(file)));
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
            Files.write(config.toString(4), file, Charset.defaultCharset());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't save the config", e);
        }

        return this;
    }

    @Override
    public Object get(String key, Object def)
    {
        Object value = config.get(key);
        return value == null ? def : value;
    }

    @Override
    public void set(String key, Object value)
    {
        if (key.contains("."))
        {
            try
            {
                String[] split = key.split("\\.");
                JSONObject object = config;

                for (int i = 0; i < split.length - 1; i++)
                {
                    object = object.getJSONObject(split[i]);
                }

                object.put(split[split.length - 1], value);
            }
            catch (JSONException ignored)
            {
            }
        }
        else
        {
            config.put(key, value);
        }

        if (autoSave)
        {
            save();
        }
    }

    @Override
    public Object at(String path, Object def)
    {
        try
        {
            String[] split = path.split("\\.");
            JSONObject object = config;

            for (int i = 0; i < split.length - 1; i++)
            {
                object = object.getJSONObject(split[i]);
            }

            return object.get(split[split.length - 1]);
        }
        catch (JSONException e)
        {
            return def;
        }
    }

    @Override
    public boolean areObjectsSupported()
    {
        return true;
    }
}
