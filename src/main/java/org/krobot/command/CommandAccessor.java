package org.krobot.command;

import org.apache.commons.lang3.ArrayUtils;
import org.krobot.KrobotModule;
import org.krobot.util.PathCompiler;

import java.util.Arrays;

public class CommandAccessor
{
    private KrobotModule module;
    private KrobotCommand command;

    public CommandAccessor(KrobotModule module, KrobotCommand command)
    {
        this.module = module;
        this.command = command;
    }

    public CommandAccessor description(String desc)
    {
        command.setDescription(desc);
        return this;
    }

    public CommandAccessor filter(Class<? extends CommandFilter>... filters)
    {
        return filter(Arrays.stream(filters)
                .map(filter -> module.injector().getInstance(filter))
                .toArray(CommandFilter[]::new));
    }

    public CommandAccessor filter(CommandFilter... filter)
    {
        command.setFilters(ArrayUtils.addAll(command.getFilters(), filter));
        return this;
    }

    public SubCommandAccessor sub(String path, ICommandHandler handler)
    {
        PathCompiler compiler = new PathCompiler(path);
        compiler.compile();

        KrobotCommand sub = new KrobotCommand(compiler.label(), compiler.args(), handler);

        KrobotCommand[] merged = ArrayUtils.add(command.getSubCommands(), sub);
        this.command.setSubCommands(merged);

        return new SubCommandAccessor(module, this, sub);
    }

    public static class SubCommandAccessor extends CommandAccessor
    {
        private CommandAccessor parent;

        SubCommandAccessor(KrobotModule module, CommandAccessor parent, KrobotCommand child)
        {
            super(module, child);

            this.parent = parent;
        }

        @Override
        public SubCommandAccessor description(String desc)
        {
            return (SubCommandAccessor) super.description(desc);
        }

        @Override
        public SubCommandAccessor filter(Class<? extends CommandFilter>... filters)
        {
            return (SubCommandAccessor) super.filter(filters);
        }

        @Override
        public SubCommandAccessor filter(CommandFilter... filter)
        {
            return (SubCommandAccessor) super.filter(filter);
        }

        public CommandAccessor then()
        {
            return this.parent;
        }
    }
}
