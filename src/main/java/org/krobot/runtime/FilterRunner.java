package org.krobot.runtime;

import java.util.stream.Stream;
import org.krobot.KrobotModule;
import org.krobot.command.runtime.MessageContext;
import org.krobot.module.FilterRules;
import org.krobot.runtime.ModuleLoader.ComputedModule;

public class FilterRunner
{
    private ComputedModule[] modules;

    public FilterRunner(ComputedModule[] modules)
    {
        this.modules = modules;
    }

    public void runFilters(MessageContext context)
    {

        getEnabledModules(context)
            .forEach(module -> module.getModule().getFilters().stream()
                                     .filter(filter -> filter.getFilter().filter(context))
                                     .map(FilterRules::getHandlers)
                                     .forEach(hs -> hs.forEach(h -> h.handle(context))));
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
