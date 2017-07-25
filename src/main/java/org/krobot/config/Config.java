package org.krobot.config;

import com.google.common.io.Files;
import com.google.gson.*;
import net.dv8tion.jda.core.utils.IOUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class Config
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private File file;
    private JsonObject config;

    public Config(File file)
    {
        this.file = file;
    }

    private void load()
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

    @Nullable
    public Object get(String key)
    {
        return get(key, null);
    }

    public String get(String key, String def)
    {
        return get(key, def, String.class);
    }

    public <T> T get(String key, T def, Class<T> type)
    {
        T value = gson.fromJson(config.get(key), type);
        return value == null ? def : value;
    }

    public void set(String key, String value)
    {
        set(key, (Object) value);
    }

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

    public String at(String path)
    {
        return at(path, (String) null);
    }

    public String at(String path, String def)
    {
        return at(path, def, String.class);
    }

    public <T> T at(String path, Class<T> type)
    {
        return at(path, null, type);
    }

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

    public <T> T[] append(String field, Class<T[]> classOfArray, T toAppend)
    {
        T[] array = ArrayUtils.add(at(field, classOfArray), toAppend);
        set(field, array);

        return array;
    }
}
