package fr.litarvan.krobot.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class ConfigProvider
{
    private static final Logger LOGGER = LogManager.getLogger("ConfigProvider");

    private Map<String, Config> configs = new HashMap<>();

    public FileConfig from(String file)
    {
        return from(new File(file));
    }

    public FileConfig from(File file)
    {
        return json(file);
    }

    public JSONConfig json(File file)
    {
        return json(file, file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    public JSONConfig json(File file, String name)
    {
        return register(new JSONConfig(file), name);
    }

    public PropertiesConfig properties(File file)
    {
        return properties(file, file.getName().substring(0, file.getName().lastIndexOf(".")));
    }

    public PropertiesConfig properties(File file, String name)
    {
        return register(new PropertiesConfig(file), name);
    }

    public <T extends FileConfig> T register(T config, String name)
    {
        LOGGER.info("Loaded config -> " + name + " (" + config.getFile().getAbsolutePath() + ")");
        return (T) register((Config) config, name);
    }

    public <T extends Config> T register(T config, String name)
    {
        configs.put(name, config);
        return config;
    }

    public Config get(String name)
    {
        return configs.get(name);
    }

    public Object at(String path)
    {
        int index = path.indexOf(".");
        return get(path.substring(0, index)).at(path.substring(index + 1));
    }
}
