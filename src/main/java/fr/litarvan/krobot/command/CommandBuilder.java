package fr.litarvan.krobot.command;

import fr.litarvan.krobot.Krobot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class CommandBuilder
{
    // [^\s]+
    // \B(\[|\()\w*(\.\.\.)?(\]|\))

    private CommandManager commandManager;

    public CommandBuilder(CommandManager commandManager)
    {
        this.commandManager = commandManager;
        this.arguments = new ArrayList<>();
        this.middlewares = new ArrayList<>();
    }

    private String label;
    private List<CommandArgument> arguments;
    private Command parent;
    private CommandHandler handler;
    private List<Middleware> middlewares;

    // \B(\[|\()\w+(:\w+)?(\.\.\.)?(\]|\))

    public CommandBuilder path(String path)
    {
        String[] split = path.split(" ");
        this.label = split[0];

        for (String arg : ArrayUtils.subarray(split, 1, split.length))
        {
            boolean optional = false;
            boolean list = false;

            if (arg.equals("*"))
            {
                arg = "[dynamic:string...]";
            }

            if (arg.startsWith("["))
            {
                optional = true;
            }

            arg = arg.substring(1, arg.length() - 1);

            if (arg.contains("|"))
            {
                String[] choices = arg.split("\\|");
                this.arguments.add(new CommandArgument(arg, optional, choices));

                continue;
            }

            String type = "string";

            if (arg.contains(":"))
            {
                String[] spl = arg.split(":");

                arg = spl[0];
                type = spl[1];
            }

            if (type.endsWith("..."))
            {
                list = true;
                type = type.substring(0, type.length() - 3);
            }

            ArgumentType argumentType = ArgumentType.valueOf(type.toUpperCase());
            this.arguments.add(new CommandArgument(arg, optional, list, argumentType));
        }

        return this;
    }

    public CommandBuilder label(String label)
    {
        this.label = label;
        return this;
    }

    public CommandBuilder arg(CommandArgument arg)
    {
        this.arguments.add(arg);
        return this;
    }

    public CommandBuilder parent(Command parent)
    {
        this.parent = parent;
        return this;
    }

    public CommandBuilder handler(CommandHandler handler)
    {
        this.handler = handler;
        return this;
    }

    public CommandBuilder middleware(Middleware middleware) // Needed for lambda
    {
        this.middlewares.add(middleware);
        return this;
    }

    public CommandBuilder middlewares(Middleware... middlewares) // 's' needed for lambda
    {
        this.middlewares.addAll(Arrays.asList(middlewares));
        return this;
    }

    public CommandBuilder middlewares(Class<? extends Middleware>... middlewares)
    {
        Middleware[] mwares = new Middleware[middlewares.length];

        for (int i = 0; i < middlewares.length; i++)
        {
            mwares[i] = Krobot.injector().getInstance(middlewares[i]);
        }

        this.middlewares.addAll(Arrays.asList(mwares));

        return this;
    }

    public Command build()
    {
        return new Command(this.label,
                           this.arguments.toArray(new CommandArgument[this.arguments.size()]),
                           this.middlewares.toArray(new Middleware[this.middlewares.size()]),
                           this.handler);
    }

    public Command register()
    {
        Command command = build();

        if (parent != null)
        {
            parent.sub(command);
        }
        else
        {
            commandManager.register(command);
        }

        return command;
    }
}
