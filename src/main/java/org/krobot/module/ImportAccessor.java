package org.krobot.module;

import org.apache.commons.lang3.ArrayUtils;
import org.krobot.module.ImportRules.Includes;

import static org.krobot.module.ImportRules.*;

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

    public ImportAccessor bridge(String sourceConfig, String targetConfig)
    {
        rules.getBridges().add(new ConfigBridge(sourceConfig, targetConfig));
        return this;
    }

    public SubFilterAccessor when(Filter filter)
    {
        FilterRules rules = new FilterRules(filter);
        this.rules.getFilters().add(rules);

        return new SubFilterAccessor(this, rules);
    }
}
