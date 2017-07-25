package org.krobot.config;

public class ConfigRules
{
    private String path;
    private String name;
    private DefaultPath def;

    public ConfigRules(String path)
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public DefaultPath getDef()
    {
        return def;
    }

    public void setDef(DefaultPath def)
    {
        this.def = def;
    }

    public static class DefaultPath
    {
        private String path;
        private PathLocation location;

        DefaultPath(String path, PathLocation location)
        {
            this.path = path;
            this.location = location;
        }

        public String getPath()
        {
            return path;
        }

        public PathLocation getLocation()
        {
            return location;
        }
    }

    public enum PathLocation
    {
        CLASSPATH,
        FILESYSTEM
    }
}
