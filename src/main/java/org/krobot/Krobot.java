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
package org.krobot;

import org.krobot.runtime.KrobotRuntime;

public final class Krobot
{
    public static final String VERSION = "3.0.0";

    public static final String PROPERTY_TOKEN = "krobot.key";
    public static final String PROPERTY_TOKEN_FILE = "krobot.keyFile";
    public static final String PROPERTY_DISABLE_TOKEN_SAVING = "krobot.disableKeySaving";
    public static final String PROPERTY_DISABLE_ASKING_TOKEN = "krobot.disableAskingKey";
    public static final String PROPERTY_DISABLE_START_MESSAGE = "krobot.disableStartMessage";
    public static final String PROPERTY_DISABLE_STATE_BAR = "krobot.disableStateBar";

    public static KrobotRunner create()
    {
        return new KrobotRunner();
    }

    public static KrobotRuntime getRuntime()
    {
        return KrobotRuntime.get();
    }
}
