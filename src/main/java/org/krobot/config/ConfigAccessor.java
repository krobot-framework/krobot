package org.krobot.config;

public class ConfigAccessor
{
    private ConfigRules rules;

    public ConfigAccessor(ConfigRules rules)
    {
        this.rules = rules;
    }

    public ConfigAccessor named(String name)
    {
        this.rules.setName(name);
        return this;
    }

    public DefaultAccessor defaultIn()
    {
        return new DefaultAccessor(rules, this);
    }

    public class DefaultAccessor
    {
        private ConfigRules rules;
        private ConfigAccessor configAccessor;

        DefaultAccessor(ConfigRules rules, ConfigAccessor configAccessor)
        {
            this.rules = rules;
            this.configAccessor = configAccessor;
        }

        public ConfigAccessor classpath(String path)
        {
            if (!path.startsWith("/"))
            {
                path = "/" + path;
            }

            this.rules.setDef(new ConfigRules.DefaultPath(path, ConfigRules.PathLocation.CLASSPATH));
            return this.configAccessor;
        }

        public ConfigAccessor file(String path)
        {
            this.rules.setDef(new ConfigRules.DefaultPath(path, ConfigRules.PathLocation.FILESYSTEM));
            return this.configAccessor;
        }
    }
}
