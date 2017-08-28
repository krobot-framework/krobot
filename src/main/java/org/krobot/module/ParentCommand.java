package org.krobot.module;

public class ParentCommand
{
    private String parentLabel;
    private String defaultSub;

    public ParentCommand(String parentLabel, String defaultSub)
    {
        this.parentLabel = parentLabel;
        this.defaultSub = defaultSub;
    }

    public String getParentLabel()
    {
        return parentLabel;
    }

    public String getDefaultSub()
    {
        return defaultSub;
    }
}
