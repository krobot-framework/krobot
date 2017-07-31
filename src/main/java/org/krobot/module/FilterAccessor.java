package org.krobot.module;

public class FilterAccessor
{
    private FilterRules rules;

    public FilterAccessor(FilterRules rules)
    {
        this.rules = rules;
    }

    public FilterAccessor prefix(String prefix)
    {
        rules.setPrefix(prefix);
        return this;
    }

    public FilterAccessor disable()
    {
        rules.setDisabled(true);
        return this;
    }

    public FilterAccessor apply(Handler handler)
    {
        rules.getHandlers().add(handler);
        return this;
    }
}
