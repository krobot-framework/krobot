package org.krobot.command;

import org.apache.commons.lang3.ArrayUtils;
import org.krobot.KrobotModule;
import org.krobot.util.PathCompiler;

public class CommandAccessor
{
    private KrobotModule module;
    private Command command;

    CommandAccessor(KrobotModule module, Command command)
    {
        this.module = module;
        this.command = command;
    }

    public CommandAccessor description(String desc)
    {
        command.setDescription(desc);
        return this;
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

        Command sub = new Command(compiler.label(), compiler.args());

        Command[] merged = ArrayUtils.add(this.getCommand().getSubCommands(), sub);
        this.getCommand().setSubCommands(merged);

        return new SubCommandAccessor(module, this, sub);
    }

    public Command getCommand()
    {
        return command;
    }

    public static class SubCommandAccessor extends CommandAccessor
    {
        private CommandAccessor parent;

        SubCommandAccessor(KrobotModule module, CommandAccessor parent, Command child)
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
