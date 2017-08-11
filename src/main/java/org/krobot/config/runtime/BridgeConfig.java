package org.krobot.config.runtime;

import com.google.gson.JsonObject;

public class BridgeConfig extends ObjectConfig
{
    private FileConfig root;

    public BridgeConfig(FileConfig root, JsonObject config)
    {
        super(config);
        this.root = root;
    }

    @Override
    public void set(String key, Object value)
    {
        super.set(key, value);
        root.save();
    }
}
