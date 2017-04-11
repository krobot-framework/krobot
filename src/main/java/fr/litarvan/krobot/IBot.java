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
package fr.litarvan.krobot;

import com.google.inject.Module;

/**
 * A Krobot Discord bot<br><br>
 *
 *
 * A Discord bot that uses Krobot, just contains an initialization
 * method.<br><br>
 *
 * To start it, use {@link Krobot#start(String, Class, Module...)}
 *
 * @author Litarvan
 * @version 2.0.0
 * @since 2.0.0
 */
public interface IBot
{
    /**
     * Initializes the bot, called just after setting up JDA
     */
    void init();
}
