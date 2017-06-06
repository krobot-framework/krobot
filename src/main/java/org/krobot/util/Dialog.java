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
package org.krobot.util;

import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 * Dialog maker<br><br>
 *
 *
 * Contains functions to make kind of "dialogs" using embeds.<br>
 * It has three types of dialog : info, warning, error.<br>
 * You can always create your own.<br><br>
 *
 * <b>Examples :</b>
 *
 * <pre>
 *     channel.sendMessage(Dialog.info("Some info", "Here is an information")).queue();
 * </pre>
 *
 * <pre>
 *     channel.sendMessage(Dialog.dialog(Color.GREEN, "Status", "Everything is good !")).queue();
 * </pre>
 *
 * @author Litarvan
 * @version 2.1.0
 * @since 2.0.0
 */
public final class Dialog
{
    /**
     * Icon used in info dialogs
     */
    public static final String INFO_ICON = "http://litarvan.github.com/krobot_icons/info_v2.png";

    /**
     * Icon used in warn dialogs
     */
    public static final String WARN_ICON = "http://litarvan.github.com/krobot_icons/warn.png";

    /**
     * Icon used in error dialogs
     */
    public static final String ERROR_ICON = "http://litarvan.github.com/krobot_icons/error.png";

    /**
     * The color used for the info dialogs
     */
    public static final Color INFO_COLOR = Color.decode("0x0094FF");

    /**
     * The color used for the warn dialogs
     */
    public static final Color WARN_COLOR = Color.decode("0xFFBD00");

    /**
     * The color used for the errors dialogs
     */
    public static final Color ERROR_COLOR = Color.decode("0xED1B2E");

    /**
     * Displays an info dialog
     *
     * @param title The title of the dialog
     * @param description The description of the dialog
     *
     * @return The created dialog
     */
    public static MessageEmbed info(String title, String description)
    {
        return dialog(INFO_COLOR, title, description, INFO_ICON);
    }

    /**
     * Display a warning dialog
     *
     * @param title The title of the dialog
     * @param description The description of the dialog
     *
     * @return The created dialog
     */
    public static MessageEmbed warn(String title, String description)
    {
        return dialog(WARN_COLOR, title, description, WARN_ICON);
    }

    /**
     * Displays an error dialog
     *
     * @param title The title of the dialog
     * @param description The description of the dialog
     *
     * @return The created dialog
     */
    public static MessageEmbed error(String title, String description)
    {
        return dialog(ERROR_COLOR, title, description, ERROR_ICON);
    }

    /**
     * Displays a dialog
     *
     * @param color The color of the bar on the dialog' left side
     * @param title The title of the dialog
     * @param description The description of the dialog
     *
     * @return The created dialog
     */
    public static MessageEmbed dialog(Color color, String title, String description)
    {
        return dialog(color, title, description, null);
    }

    /**
     * Displays a dialog
     *
     * @param color The color of the bar on the dialog left side
     * @param title The title of the dialog
     * @param description The description of the dialog
     * @param icon The URL of the dialog icon
     *
     * @return The created dialog
     */
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
