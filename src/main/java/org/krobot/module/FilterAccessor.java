/*
 * Copyright 2017 The Krobot Contributors
 *
 * This file is part of Krobot.
 *
 * Krobot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Krobot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Krobot.  If not, see <http://www.gnu.org/licenses/>.
 */
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
