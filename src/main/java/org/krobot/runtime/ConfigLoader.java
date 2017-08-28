package org.krobot.runtime;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.commons.lang3.tuple.Pair;
import org.fusesource.jansi.Ansi.Color;
import org.krobot.KrobotModule;
import org.krobot.config.ConfigRules;
import org.krobot.config.ConfigRules.DefaultPath;
import org.krobot.config.BridgeConfig;
import org.krobot.config.FileConfig;
import org.krobot.module.ImportRules.ConfigBridge;
import org.krobot.util.ColoredLogger;

public class ConfigLoader
{
    private static final ColoredLogger log = ColoredLogger.getLogger("ConfigLoader");

    private RuntimeModule module;

    public ConfigLoader(RuntimeModule module)
    {
        this.module = module;
    }

    public void load()
    {
        // Bridges
        module.getComputed().getBridges().forEach(this::load);

        // Configs
        module.getComputed().getConfigs().forEach(this::load);
    }

    public void load(ConfigRules rules)
    {
        // If a bridge exists for this config, not doing anything
        if (module.getConfig().has(rules.getName()))
        {
            return;
        }

        String path = rules.getPath();

        if (!path.contains("."))
        {
            path += ".json";
        }

        if (!path.contains("/"))
        {
            path = "./" + path;
        }

        File file = new File(path);
        File def = null;

        String name = rules.getName();

        if (name == null)
        {
            name = file.getName();

            if (name.contains("."))
            {
                name = name.substring(0, name.lastIndexOf('.'));
            }
        }

        if (!file.exists())
        {
            file.getParentFile().mkdirs();

            if (rules.getDef() == null)
            {
                createEmpty(file);
            }
            else
            {
                def = getDefaultFile(rules.getDef());

                if (def == null)
                {
                    log.error(Color.RED, "Unable to find default file at {} (for config '{}')", rules.getDef(), name);
                    log.error(Color.RED, "Skipping default file of config '{}'", name);

                    createEmpty(file);
                }
                else
                {
                    try
                    {
                        Files.copy(def, file);
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("Exception while copying default configuration of config '" + name + "' (at " + def + ")", e);
                    }
                }
            }
        }

        try
        {
            module.getConfig().register(name, new FileConfig(file));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Exception while loading config '" + name + "' (at " + file + ")", e);
        }

        log.info("Loaded config '{}' ({}) {}",
                 name,
                 file,
                 (rules.getDef() == null || (rules.getDef() != null && def == null) ? "as empty config" : (def != null ? "from default file '" + def + "'" : "from file '" + file + "'")));
    }

    public void load(Pair<ConfigBridge, KrobotModule> pair)
    {
        ConfigBridge bridge = pair.getLeft();

        RuntimeModule target = KrobotRuntime.get().getRuntimeModule(pair.getRight().getClass());
        module.getConfig().register(bridge.getConfig(), new BridgeConfig(target.getConfig(), bridge.getDest()));

        log.info("Defined bridge from {}#{} <---- to ----> {}#{}",
                 module.getComputed().getModule().getClass().getName(),
                 bridge.getConfig(),

                 bridge.getDest(),
                 target.getComputed().getModule().getClass().getName());
    }

    private void createEmpty(File file)
    {
        try
        {
            Files.write("{}", file, Charset.defaultCharset());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error while creating empty config in '" + file + "'", e);
        }
    }

    private File getDefaultFile(DefaultPath def)
    {
        switch (def.getLocation())
        {
            case FILESYSTEM:
                return new File(def.getPath());
            case CLASSPATH:
                try
                {
                    URL res = module.getClass().getResource(def.getPath());

                    if (res == null)
                    {
                        return null;
                    }

                    return new File(res.toURI());
                }
                catch (URISyntaxException ignored)
                {
                }
        }

        return null;
    }
}
