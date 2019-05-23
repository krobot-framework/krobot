package org.krobot.console;

import java.util.ArrayList;
import java.util.List;
import org.krobot.command.PathCompiler;
import org.krobot.runtime.KrobotRuntime;

public class KrobotConsole
{
    private KrobotRuntime runtime;

    private List<ComputedConsoleCommand> commands;
    private ConsoleProcessor processor;

    private boolean processing;

    public KrobotConsole(KrobotRuntime runtime)
    {
        this.runtime = runtime;

        this.commands = new ArrayList<>();
        this.processor = new ConsoleProcessor(this, new ConsoleHighlighter(this), new ConsoleCompleter(this));
    }

    public void start()
    {
        this.processor.start();
    }

    public void register(Class<? extends ConsoleCommand> command)
    {
        register(runtime.getInjector().getInstance(command));
    }

    public void register(ConsoleCommand command)
    {
        PathCompiler compiler = new PathCompiler(command.getPath());
        compiler.compile();

        commands.add(new ComputedConsoleCommand(compiler.label(), compiler.args(), command));
    }

    public List<ComputedConsoleCommand> getCommands()
    {
        return commands;
    }

    public boolean isProcessing()
    {
        return processing;
    }

    void setProcessing(boolean processing)
    {
        this.processing = processing;
    }

    public KrobotRuntime getRuntime()
    {
        return runtime;
    }
}
