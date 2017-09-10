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

    public ImportAccessor disableIf(Filter filter)
    {
        FilterRules rules = new FilterRules(filter);
        rules.setDisabled(true);

        this.rules.getFilters().add(rules);

        return this;
    }

    public ImportAccessor asSubsOf(String parentCommandLabel, String defaultCommand)
    {
        rules.setParentCommand(new ParentCommand(parentCommandLabel, defaultCommand));
        return this;
    }
}
