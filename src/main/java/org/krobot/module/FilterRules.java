package org.krobot.module;

import java.util.ArrayList;
import java.util.List;

public class FilterRules
{
    private Filter filter;

    private String prefix;
    private boolean disabled;
    private List<Handler> handlers;

    public FilterRules(Filter filter)
    {
        this.filter = filter;
        this.handlers = new ArrayList<>();
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public boolean isDisabled()
    {
        return disabled;
    }

    public void setDisabled(boolean disabled)
    {
        this.disabled = disabled;
    }

    public List<Handler> getHandlers()
    {
        return handlers;
    }

    public Filter getFilter()
    {
        return filter;
    }
}
