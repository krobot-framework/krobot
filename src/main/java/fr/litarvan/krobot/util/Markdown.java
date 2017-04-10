/*
 * Copyright 2017 Adrien "Litarvan" Navratil
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
package fr.litarvan.krobot.util;

/**
 * Markdown util
 *
 *
 * Functions to use Discord-markdown in messages.
 *
 * Example :
 *
 * <pre>
 *     channel.sendMessage("I am a " + Markdown.bold("bot")).queue();
 * </pre>
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public final class Markdown
{
    public static final String BOLD_MODIFIER = "**";
    public static final String ITALIC_MODIFIER = "_";
    public static final String UNDERLINE_MODIFIER = "__";
    public static final String CODE_MODIFIER = "```";
    public static final String STRIKEOUT_MODIFIER = "~~";
    public static final String SMALL_CODE_MODIFIER = "`";

    public static String bold(String string)
    {
        return markdown(string, BOLD_MODIFIER);
    }

    public static String italic(String string)
    {
        return markdown(string, ITALIC_MODIFIER);
    }

    public static String underline(String string)
    {
        return markdown(string, UNDERLINE_MODIFIER);
    }

    public static String strikeout(String string)
    {
        return markdown(string, STRIKEOUT_MODIFIER);
    }

    public static String smallCode(String string)
    {
        return markdown(string, SMALL_CODE_MODIFIER);
    }

    public static String code(String string)
    {
        return code(string, "");
    }

    public static String code(String string, String lang)
    {
        return CODE_MODIFIER + lang + "\n" + string + "\n" + CODE_MODIFIER;
    }

    public static String markdown(String string, String modifier)
    {
        return modifier + string + modifier;
    }
}
