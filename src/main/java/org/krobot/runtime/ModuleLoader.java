package org.krobot.runtime;

import com.google.inject.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.krobot.KrobotModule;
import org.krobot.config.ConfigRules;
import org.krobot.module.FilterRules;
import org.krobot.module.ImportRules.ConfigBridge;
import org.krobot.module.ImportRules.Includes;
import org.krobot.module.Include;

public class ModuleLoader
{
    private static final Logger log = LogManager.getLogger("Loader");

    private List<ComputedModule> modules;

    public ModuleLoader()
    {
        this.modules = new ArrayList<>();
    }

    public ComputedModule load(Class<? extends KrobotModule> moduleClass)
    {
        log.info("(" + moduleClass.getName() + ") Pre-initialization...");

        ComputedModule module = null;

        for (ComputedModule computed : modules)
        {
            if (computed.getModule().getClass() == moduleClass)
            {
                module = computed;
            }
        }

        if (module == null)
        {
            KrobotModule instance = null;

            try
            {
                instance = moduleClass.newInstance();
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                log.fatal("Error while creating instance of module " + moduleClass.getName() + "; remember modules must have an empty constructor", e);
                System.exit(1);
            }

            module = new ComputedModule(instance);
            modules.add(module);
        }

        importFromAnnotation(module);
        preInit(module);

        return module;
    }

    private void importFromAnnotation(ComputedModule module)
    {
        Include include = module.getModule().getClass().getAnnotation(Include.class);

        if (include == null)
        {
            return;
        }

        Stream.of(include.imports()).forEach(this::load);
    }

    public void preInit(ComputedModule module)
    {
        module.getModule().preInit();

        // Importing everything

        module.getFilters().addAll(module.getModule().getFilters());
        module.getConfigs().addAll(module.getModule().getConfigs());
        module.getGuiceModules().addAll(module.getModule().getGuiceModules());

        module.getModule().getImports().forEach(rules -> {
            ComputedModule loaded = load(rules.getModule());

            List<Pair<ConfigBridge, KrobotModule>> bridges = new ArrayList<>();
            rules.getBridges().forEach(bridge -> bridges.add(new ImmutablePair<>(bridge, module.getModule())));

            if (loaded.getIncludes() != null)
            {
                log.warn("The module " + loaded.getModule().getClass().getName() + " was imported using from(...) multiple times");
                log.warn("If you defined inclusions/exclusions, they may differ from what you excepted");
            }

            loaded.getBridges().addAll(bridges);
            loaded.getFilters().addAll(rules.getFilters());
            loaded.setIncludes(rules.getIncludes());
        });

        // Cleaning for further use
        module.getFilters().removeIf(item -> true);
        module.getGuiceModules().removeIf(item -> true);
    }

    public List<ComputedModule> getModules()
    {
        return modules;
    }

    public static class ComputedModule
    {
        private KrobotModule module;

        private List<FilterRules> filters;
        private List<ConfigRules> configs;
        private List<Pair<ConfigBridge, KrobotModule>> bridges;
        private List<Module> guiceModules;

        private Includes[] includes;

        public ComputedModule(KrobotModule module)
        {
            this.module = module;

            this.filters = new ArrayList<>();
            this.configs = new ArrayList<>();
            this.bridges = new ArrayList<>();
            this.guiceModules = new ArrayList<>();

            this.includes = null;
        }

        public KrobotModule getModule()
        {
            return module;
        }

        public List<FilterRules> getFilters()
        {
            return filters;
        }

        public List<ConfigRules> getConfigs()
        {
            return configs;
        }

        public List<Pair<ConfigBridge, KrobotModule>> getBridges()
        {
            return bridges;
        }

        public List<Module> getGuiceModules()
        {
            return guiceModules;
        }

        public Includes[] getIncludes()
        {
            return includes;
        }

        public void setIncludes(Includes[] includes)
        {
            this.includes = includes;
        }
    }
}
