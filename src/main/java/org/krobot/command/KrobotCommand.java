package org.krobot.command;

public class KrobotCommand
{
    private String label;
    private String[] aliases;
    private CommandArgument[] arguments;
    private String description;
    private CommandFilter[] filters;
    private KrobotCommand[] subs;
    private CommandHandler handler;

    public KrobotCommand(String label, CommandArgument[] arguments, CommandHandler handler)
    {
        this(label, arguments, handler, new KrobotCommand[] {});
    }

    public KrobotCommand(String label, CommandArgument[] arguments, CommandHandler handler, KrobotCommand[] subs)
    {
        this.label = label;
        this.arguments = arguments;
        this.handler = handler;
        this.subs = subs;
    }

    public KrobotCommand(String label, CommandArgument[] arguments, String description, String[] aliases, CommandFilter[] filters, CommandHandler handler, KrobotCommand[] subs)
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

    public CommandHandler getHandler()
    {
        return handler;
    }

    @Override
    public String toString()
    {
        return toString("", true);
    }

    /**
     * Convert the command to a displayable string
     * (similar to the {@link PathCompiler} syntax)
     *
     * @param prefix The prefix to add (like tabs)
     * @param subs If the subs should be displayed too
     *
     * @return The generated string
     */
    public String toString(String prefix, boolean subs)
    {
        StringBuilder string = new StringBuilder(prefix + this.label + " ");

        for (CommandArgument argument : arguments)
        {
            string.append(argument).append(" ");
        }

        if (subs && this.subs != null && this.subs.length != 0)
        {
            for (KrobotCommand sub : this.subs)
            {
                string.append("\n");
                string.append(prefix).append(sub.toString(prefix + this.getLabel() + " ", true));
            }
        }

        return string.toString();
    }
}
