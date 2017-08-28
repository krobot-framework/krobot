package org.krobot.config;

import com.google.common.io.Files;
import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.krobot.config.ConfigAccessor.DefaultAccessor;

public class ConfigFolder
{
    private List<ConfigRules> configs;
    private File folder;

    public ConfigFolder(List<ConfigRules> configs, File folder)
    {
        this.configs = configs;
        this.folder = folder;
    }

    public ConfigAccessor config(String path)
    {
        ConfigRules rules = new ConfigRules(new File(folder, path).getPath());
        this.configs.add(rules);

        return new ConfigAccessor(rules);
    }

    public ConfigFolder configs(String... configs)
    {
        Stream.of(configs).forEach(this::config);
        return this;
    }

    public FolderDefaultAccessor withDefaultsIn()
    {
        return new FolderDefaultAccessor(this);
    }

    private List<ConfigRules> getConfigs()
    {
        return configs;
    }

    public static class FolderDefaultAccessor
    {
        private ConfigFolder configFolder;

        FolderDefaultAccessor(ConfigFolder configFolder)
        {
            this.configFolder = configFolder;
        }

        public ConfigFolder classpathFolder(String path)
        {
            if (!path.startsWith("/"))
            {
                path = "/" + path;
            }

            applyDef(path, ConfigRules.PathLocation.CLASSPATH);

            return this.configFolder;
        }

        public ConfigFolder folder(String path)
        {
            applyDef(path, ConfigRules.PathLocation.FILESYSTEM);
            return this.configFolder;
        }

        private void applyDef(String path, ConfigRules.PathLocation location)
        {
            configFolder.getConfigs().forEach(config -> {
                String name = Files.getNameWithoutExtension(config.getPath()) + ".default.json";
                config.setDef(new ConfigRules.DefaultPath(path + (path.endsWith("/") ? "" : "/") + name, location));
            });
        }
    }
}
