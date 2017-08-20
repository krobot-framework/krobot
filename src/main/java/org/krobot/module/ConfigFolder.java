package org.krobot.module;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;
import org.krobot.config.ConfigAccessor;
import org.krobot.config.ConfigRules;

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
}
