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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krobot.KrobotModule;
import org.krobot.MessageContext;
import org.krobot.module.FilterRules;
import org.krobot.runtime.ModuleLoader.ComputedModule;

public class FilterRunner
{
    private static final Logger log = LogManager.getLogger("FilterRunner");

    private KrobotRuntime runtime;
    private ComputedModule[] modules;

    public FilterRunner(KrobotRuntime runtime, ComputedModule[] modules)
    {
        this.runtime = runtime;
        this.modules = modules;
    }

    public void runFilters(MessageContext context)
    {
        getEnabledModules(context)
            .forEach(module -> module.getFilters().stream()
                                     .filter(filter -> filter.getFilter().filter(context))
                                     .map(FilterRules::getHandlers)
                                     .forEach(hs -> hs.forEach(h -> h.handle(context))));
    }

    public String getPrefix(MessageContext context)
    {
        String prefix = runtime.getPrefix();

        List<String> results = runtime.getRootModule().getFilters().stream()
                                      .filter(filter -> filter.getFilter().filter(context))
                                      .map(FilterRules::getPrefix)
                                      .collect(Collectors.toList());

        if (results.size() > 1)
        {
            log.error("Multiple filters in root module applied a prefix modification, base prefix will be kept");
            log.error("This is definitely a development error, please fix this");
        }
        else if (results.size() != 0)
        {
            prefix = results.get(0);
        }

        return prefix;
    }

    public boolean isDisabled(MessageContext context, KrobotModule module)
    {
        return Stream.of(modules)
                     .filter(m -> m.getModule() == module)
                     .findFirst()
                     .map(m -> m.getFilters().stream()
                                .filter(f -> f.getFilter().filter(context))
                                .anyMatch(FilterRules::isDisabled))
                     .orElse(false);
    }

    public Stream<ComputedModule> getEnabledModules(MessageContext context)
    {
        return Stream.of(modules)
                     .filter(module -> !isDisabled(context, module.getModule()));
    }
}
