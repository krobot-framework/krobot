package fr.litarvan.krobot.config;

public interface Config
{
    Object get(String key, Object def);
    void set(String key, Object value);

    default Object get(String key)
    {
        return get(key, null);
    }

    default Object at(String path, Object def)
    {
        return get(path, def);
    }

    default Object at(String path)
    {
        return at(path, null);
    }

    boolean areObjectsSupported();
}
