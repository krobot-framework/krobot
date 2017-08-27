package org.krobot.command;

public class KrobotCommand
{
    private String label;
    private String[] aliases;
    private CommandArgument[] arguments;
    private String description;
    private CommandFilter[] filters;
    private KrobotCommand[] subs;
    private ICommandHandler handler;

    public KrobotCommand(String label, CommandArgument[] arguments, ICommandHandler handler)
    {
        this(label, arguments, handler, new KrobotCommand[] {});
    }

    public KrobotCommand(String label, CommandArgument[] arguments, ICommandHandler handler, KrobotCommand[] subs)
    {
        this.label = label;
        this.arguments = arguments;
        this.handler = handler;
        this.subs = subs;
    }

    public KrobotCommand(String label, CommandArgument[] arguments, String description, String[] aliases, CommandFilter[] filters, ICommandHandler handler, KrobotCommand[] subs)
    {
        this.label = label;
        this.aliases = aliases;
        this.arguments = arguments;
        this.description = description;
        this.filters = filters;
        this.subs = subs;
        this.handler = handler;
    }

    public String getLabel()
    {
        return label;
    }

    public String[] getAliases()
    {
        return aliases;
    }

    public void setAliases(String[] aliases)
    {
        this.aliases = aliases;
    }

    public CommandArgument[] getArguments()
    {
        return arguments;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public CommandFilter[] getFilters()
    {
        return filters;
    }

    public void setFilters(CommandFilter[] filters)
    {
        this.filters = filters;
    }

    public KrobotCommand[] getSubCommands()
    {
        return subs;
    }

    public void setSubCommands(KrobotCommand[] subs)
    {
        this.subs = subs;
    }

    public ICommandHandler getHandler()
    {
        return handler;
    }
}
