package fr.litarvan.krobot.command;

import fr.litarvan.krobot.Krobot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupBuilder
{
    private CommandManager commandManager;

    public GroupBuilder(CommandManager commandManager)
    {
        this.commandManager = commandManager;
        this.middlewares = new ArrayList<>();
    }

    private String prefix;
    private Command parent;
    private List<Middleware> middlewares;

    public GroupBuilder prefix(String prefix)
    {
        this.prefix = prefix;
        return this;
    }

    public GroupBuilder parent(Command parent)
    {
        this.parent = parent;
        return this;
    }

    public GroupBuilder middleware(Middleware middleware) // Needed for lambda
    {
        this.middlewares.add(middleware);
        return this;
    }

    public GroupBuilder middlewares(Middleware... middlewares) // 's' needed for lambda
    {
        this.middlewares.addAll(Arrays.asList(middlewares));
        return this;
    }

    public GroupBuilder middlewares(Class<? extends Middleware>... middlewares)
    {
        Middleware[] mwares = new Middleware[middlewares.length];

        for (int i = 0; i < middlewares.length; i++)
        {
            mwares[i] = Krobot.injector().getInstance(middlewares[i]);
        }

        this.middlewares.addAll(Arrays.asList(mwares));

        return this;
    }

    public void apply(Runnable runnable)
    {
        CommandGroup group = new CommandGroup(prefix, parent, middlewares);
        commandManager.push(group);

        runnable.run();

        commandManager.pop();
    }
}
