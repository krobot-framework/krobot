package fr.litarvan.krobot.util;

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
