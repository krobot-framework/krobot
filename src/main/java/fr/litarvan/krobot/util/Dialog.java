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

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

public final class Dialog
{
    public static final String INFO_ICON = "http://litarvan.github.com/krobot_icons/info.png";
    public static final String WARN_ICON = "http://litarvan.github.com/krobot_icons/warn.png";
    public static final String ERROR_ICON = "http://litarvan.github.com/krobot_icons/error.png";

    public static MessageEmbed info(String title, String description)
    {
        return dialog(Color.decode("0x0094FF"), title, description, INFO_ICON);
    }

    public static MessageEmbed warn(String title, String description)
    {
        return dialog(Color.decode("0xFFBD00"), title, description, WARN_ICON);
    }

    public static MessageEmbed error(String title, String description)
    {
        return dialog(Color.decode("0xED1B2E"), title, description, ERROR_ICON);
    }

    public static MessageEmbed dialog(Color color, String title, String description)
    {
        return dialog(color, title, description, null);
    }

    public static MessageEmbed dialog(Color color, String title, String description, String icon)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setColor(color);

        builder.setTitle(title, null);
        builder.setDescription(description);

        if (icon != null)
        {
            builder.setThumbnail(icon);
        }

        return builder.build();
    }
}
