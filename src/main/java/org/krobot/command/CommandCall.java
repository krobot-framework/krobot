package org.krobot.command;

public class CommandCall
{
    private KrobotCommand command;
    private boolean cancelled;

    public CommandCall(KrobotCommand command)
    {
        this.command = command;
    }

    public KrobotCommand getCommand()
    {
        return command;
    }

    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    public boolean isCancelled()
    {
        return cancelled;
    }
}
