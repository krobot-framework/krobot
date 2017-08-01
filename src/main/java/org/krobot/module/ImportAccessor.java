package org.krobot.module;

import org.apache.commons.lang3.ArrayUtils;
import org.krobot.module.ImportRules.Includes;

import static org.krobot.module.ImportRules.*;
import static org.krobot.module.ImportRules.BridgeTarget.*;

public class ImportAccessor
{
    private ImportRules rules;

    public ImportAccessor(ImportRules rules)
    {
        this.rules = rules;
    }

    public ImportAccessor include(Includes... includes)
    {
        rules.setIncludes(includes);
        return this;
    }

    public ImportAccessor exclude(Includes... excludes)
    {
        for (Includes exclude : excludes)
        {
            rules.setIncludes(ArrayUtils.removeAllOccurences(rules.getIncludes(), exclude));
        }

        return this;
    }

    public ConfigBridgeAccessor bridge(String sourceConfig)
    {
        return new ConfigBridgeAccessor(this, rules, sourceConfig);
    }

    public SubFilterAccessor when(Filter filter)
    {
        FilterRules rules = new FilterRules(filter);
        this.rules.getFilters().add(rules);

        return new SubFilterAccessor(this, rules);
    }

    public static class ConfigBridgeAccessor
    {
        private ImportAccessor importAccessor;
        private ImportRules rules;
        private String source;

        ConfigBridgeAccessor(ImportAccessor importAccessor, ImportRules rules, String source)
        {
            this.importAccessor = importAccessor;
            this.rules = rules;
            this.source = source;
        }

        public ImportAccessor to(String targetConfig)
        {
            rules.getBridges().add(new ConfigBridge(source, targetConfig, CONFIG));
            return importAccessor;
        }

        public ImportAccessor at(String targetPath)
        {
            rules.getBridges().add(new ConfigBridge(source, targetPath, PATH));
            return importAccessor;
        }
    }
}
