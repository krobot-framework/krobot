package org.krobot.module;

import org.apache.commons.lang3.ArrayUtils;
import org.krobot.module.ImportRules.Includes;

import static org.krobot.module.ImportRules.*;
import static org.krobot.module.ImportRules.BridgeTarget.*;

public class ImportAccessor
{
    private ImportRules rule;

    public ImportAccessor(ImportRules rule)
    {
        this.rule = rule;
    }

    public ImportAccessor include(Includes... includes)
    {
        rule.setIncludes(includes);
        return this;
    }

    public ImportAccessor exclude(Includes... excludes)
    {
        for (Includes exclude : excludes)
        {
            rule.setIncludes(ArrayUtils.removeAllOccurences(rule.getIncludes(), exclude));
        }

        return this;
    }

    public ConfigBridgeAccessor bridge(String sourceConfig)
    {
        return new ConfigBridgeAccessor(this, rule, sourceConfig);
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
