package org.krobot.util;

import org.krobot.command.CommandArgument;

public final class PathCompiler
{
    private String path;

    private String label;
    private CommandArgument[] args;

    public PathCompiler(String path)
    {
        this.path = path;
    }

    public void compile()
    {
        // TODO: THIS :delta:
    }

    public String label()
    {
        return this.label;
    }

    public CommandArgument[] args()
    {
        return this.args;
    }

    public String getPath()
    {
        return path;
    }
}
