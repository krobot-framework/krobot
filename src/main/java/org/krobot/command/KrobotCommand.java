package org.krobot.command;

public class KrobotCommand
{
    private String label;
    private CommandArgument[] arguments;
    private String description;
    private CommandFilter[] filters;
    private KrobotCommand[] subs;

    public KrobotCommand(String label, CommandArgument[] arguments)
    {
        this(label, arguments, new KrobotCommand[] {});
    }

    public KrobotCommand(String label, CommandArgument[] arguments, KrobotCommand[] subs)
    {
        this.label = label;
        this.arguments = arguments;
        this.subs = subs;
    }

    public String getLabel()
    {
        return label;
    }

    public CommandArgument[] getArguments()
    {
        return arguments;
    }

    public String getDescription()
    {
        return description;
    }

    public CommandFilter[] getFilters()
    {
        return filters;
    }

    public KrobotCommand[] getSubCommands()
    {
        return subs;
    }

    void setLabel(String label)
    {
        this.label = label;
    }

    void setArguments(CommandArgument[] arguments)
    {
        this.arguments = arguments;
    }

    void setDescription(String description)
    {
        this.description = description;
    }

    public void setFilters(CommandFilter[] filters)
    {
        this.filters = filters;
    }

    void setSubCommands(KrobotCommand[] subs)
    {
        this.subs = subs;
    }
}
