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
package org.krobot.runtime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.lang3.tuple.Pair;
import org.fusesource.jansi.Ansi.Color;
import org.krobot.KrobotModule;
import org.krobot.config.BridgeConfig;
import org.krobot.config.ConfigRules;
import org.krobot.config.ConfigRules.DefaultPath;
import org.krobot.config.FileConfig;
import org.krobot.module.ImportRules.ConfigBridge;
import org.krobot.util.ColoredLogger;

import com.google.common.io.Files;

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
        InputStream def = null;

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
                    	Files.write(toByteArray(def), file);
                        ///Files.copy(def, file);
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
                 (rules.getDef() == null || (rules.getDef() != null && !file.exists() && def == null) ? "as empty config" : (def != null ? "from default file '" + def + "'" : "from file '" + file + "'")));
    }

    public void load(Pair<ConfigBridge, KrobotModule> pair)
    {
        ConfigBridge bridge = pair.getLeft();

        RuntimeModule target = KrobotRuntime.get().getRuntimeModule(pair.getRight().getClass());
        module.getConfig().register(bridge.getDest(), new BridgeConfig(target.getConfig(), bridge.getConfig()));

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

    private InputStream getDefaultFile(DefaultPath def)
    {
        switch (def.getLocation())
        {
            case FILESYSTEM:
			try {
				return new FileInputStream(def.getPath());
			} catch (FileNotFoundException ignored) {}
            case CLASSPATH:
            	return module.getClass().getResourceAsStream(def.getPath());
        }

        return null;
    }
    
	private byte[] toByteArray(InputStream in) throws IOException {

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) != -1)
			os.write(buffer, 0, len);

		return os.toByteArray();
	}
}
