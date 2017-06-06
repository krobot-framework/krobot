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
package org.krobot.config;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import net.dv8tion.jda.core.utils.IOUtil;

/**
 * The JSON Config<br><br>
 *
 *
 * A config made using JSON.<br>
 * To create one, consider using the {@link ConfigProvider#json}
 * methods.
 *
 * @author Litarvan
 * @version 3.0.0
 * @since 2.0.0
 */
public class JSONConfig extends FileConfig
{
    private Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private JsonObject config;

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
        if (this.file == null)
        {
            throw new IllegalStateException("Config file isn't defined");
        }

        load(this.file);

        return this;
    }

    private void load(File file)
    {
        try
        {
            config = new JsonParser().parse(new String(IOUtil.readFully(file))).getAsJsonObject();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't read config", e);
        }
    }

    @Override
    public FileConfig save()
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
        }

        try
        {
            Files.write(gson.toJson(config), file, Charset.defaultCharset());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't save the config", e);
        }

        return this;
    }

    @Override
    public FileConfig defaultIn(File file)
    {
        if (!this.file.exists())
        {
            load(file);
            save();
        }

        return this;
    }

    @Override
    public String get(String key, String def)
    {
        return get(key, def, String.class);
    }

    @Override
    public <T> T get(String key, T def, Class<T> type)
    {
        T value = gson.fromJson(config.get(key), type);
        return value == null ? def : value;
    }

    @Override
    public void set(String key, String value)
    {
        set(key, (Object) value);
    }

    @Override
    public void set(String key, Object value)
    {
        if (key.contains("."))
        {
            try
            {
                String[] split = key.split("\\.");
                JsonObject object = config;

                for (int i = 0; i < split.length - 1; i++)
                {
                    String str = split[i];

                    if (!object.has(str))
                    {
                        object.add(str, new JsonObject());
                    }

                    object = object.getAsJsonObject(str);
                }

                object.add(split[split.length - 1], gson.toJsonTree(value));
            }
            catch (JsonParseException ignored)
            {
            }
        }
        else
        {
            config.add(key, gson.toJsonTree(value));
        }

        if (autoSave)
        {
            save();
        }
    }

    @Override
    public <T> T at(String path, T def, Class<T> type)
    {
        try
        {
            String[] split = path.split("\\.");
            JsonElement el = config;

            for (int i = 0; i < split.length - 1; i++)
            {
                el = el.getAsJsonObject().get(split[i]);

                if (el == null)
                {
                    return null;
                }

                if (!el.isJsonObject())
                {
                    throw new IllegalArgumentException("Field '" + split[i] + "' isn't an object");
                }
            }

            return gson.fromJson(el.getAsJsonObject().get(split[split.length - 1]), type);
        }
        catch (JsonParseException e)
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
