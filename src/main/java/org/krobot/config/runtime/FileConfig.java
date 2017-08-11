package org.krobot.config.runtime;

import com.google.common.io.Files;
import com.google.gson.*;
import net.dv8tion.jda.core.utils.IOUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileConfig extends ObjectConfig
{
    private File file;
    private JsonObject config;

    public FileConfig(File file) throws IOException
    {
        super(new JsonParser().parse(new String(IOUtil.readFully(file))).getAsJsonObject());
    }

    @Override
    public void set(String key, Object value)
    {
        super.set(key, value);
        save();
    }

    void save()
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
}
