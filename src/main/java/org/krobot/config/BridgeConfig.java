package org.krobot.config;

public class BridgeConfig implements Config
{
    private ConfigProvider target;
    private String path;

    public BridgeConfig(ConfigProvider target, String path)
    {
        this.target = target;
        this.path = path;
    }

    @Override
    public <T> T get(String key, T def, Class<T> type)
    {
        return at(key, def, type);
    }

    @Override
    public void set(String key, Object value)
    {
        target.set(path + "." + key, value);
    }

    @Override
    public <T> T at(String path, T def, Class<T> type)
    {
        return target.at(this.path + "." + path, def, type);
    }
}
