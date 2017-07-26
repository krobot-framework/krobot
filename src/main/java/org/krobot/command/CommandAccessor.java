package org.krobot.command;

import org.apache.commons.lang3.ArrayUtils;
import org.krobot.KrobotModule;
import org.krobot.util.PathCompiler;

import java.util.Arrays;

public class CommandAccessor
{
    private KrobotModule module;
    private KrobotCommand command;

    CommandAccessor(KrobotModule module, KrobotCommand command)
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

        KrobotCommand sub = new KrobotCommand(compiler.label(), compiler.args());

        KrobotCommand[] merged = ArrayUtils.add(this.getCommand().getSubCommands(), sub);
        this.getCommand().setSubCommands(merged);

        return new SubCommandAccessor(module, this, sub);
    }

    public KrobotCommand getCommand()
    {
        return command;
    }

    public static class SubCommandAccessor extends CommandAccessor
    {
        private CommandAccessor parent;

        SubCommandAccessor(KrobotModule module, CommandAccessor parent, KrobotCommand child)
        {
            super(module, child);

            this.parent = parent;
        }

        public CommandAccessor then()
        {
            return this.parent;
        }
    }
}
