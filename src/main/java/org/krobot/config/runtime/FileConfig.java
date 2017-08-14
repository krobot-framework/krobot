package org.krobot.config.runtime;

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

public class FileConfig implements Config
{
    protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private File file;
    private JsonObject config;

    public FileConfig(File file) throws IOException
    {
        this.file = file;
        this.config = new JsonParser().parse(Files.toString(file, Charset.defaultCharset())).getAsJsonObject();
    }

    @Override
    public <T> T get(String key, T def, Class<T> type)
    {
        T value = gson.fromJson(config.get(key), type);
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

        save();
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

    private void save()
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
    }

    public File getFile()
    {
        return file;
    }
}
