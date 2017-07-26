package org.krobot.command;

import org.krobot.KrobotModule;
import org.krobot.util.PathCompiler;

import java.util.ArrayList;
import java.util.List;

public class CommandManager
{
    private KrobotModule module;
    private String prefix;
    private List<KrobotCommand> commands;

    public CommandManager(KrobotModule module)
    {
        this.module = module;
        this.commands = new ArrayList<>();
    }

    public CommandAccessor make(String path, ICommandHandler handler)
    {
        PathCompiler compiler = new PathCompiler(path);
        compiler.compile();

        KrobotCommand command = new KrobotCommand(compiler.label(), compiler.args());
        commands.add(command);

        return new CommandAccessor(module, command);
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
}
