package fr.litarvan.krobot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

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
    public Object get(String key, Object def)
    {
        return properties.getProperty(key, (String) def);
    }

    @Override
    public void set(String key, Object value)
    {
        properties.setProperty(key, (String) value);
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
