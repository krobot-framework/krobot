package org.krobot.module;

public class SubFilterAccessor extends FilterAccessor
{
    private ImportAccessor importAccessor;

    public SubFilterAccessor(ImportAccessor importAccessor, FilterRules rules)
    {
        super(rules);

        this.importAccessor = importAccessor;
    }

    @Override
    public SubFilterAccessor prefix(String prefix)
    {
        return (SubFilterAccessor) super.prefix(prefix);
    }

    @Override
    public SubFilterAccessor disable()
    {
        return (SubFilterAccessor) super.disable();
    }

    @Override
    public SubFilterAccessor apply(Handler handler)
    {
        return (SubFilterAccessor) super.apply(handler);
    }

    public ImportAccessor then()
    {
        return this.importAccessor;
    }
}
