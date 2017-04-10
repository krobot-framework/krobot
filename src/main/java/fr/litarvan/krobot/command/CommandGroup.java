package fr.litarvan.krobot.command;

import fr.litarvan.krobot.command.Command;
import fr.litarvan.krobot.command.Middleware;
import java.util.List;

public class CommandGroup
{
    private String prefix;
    private Command parent;
    private List<Middleware> middlewares;

    public CommandGroup(String prefix, Command parent, List<Middleware> middlewares)
    {
        this.prefix = prefix;
        this.parent = parent;
        this.middlewares = middlewares;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public Command getParent()
    {
        return parent;
    }

    public List<Middleware> getMiddlewares()
    {
        return middlewares;
    }
}
