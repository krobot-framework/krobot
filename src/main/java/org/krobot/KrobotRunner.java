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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.krobot.runtime.KrobotRuntime;

import static org.krobot.Krobot.*;

public class KrobotRunner
{
    private String token;
    private int trial;

    public KrobotRunner()
    {
        this.trial = 0;
    }

    public KrobotRunner setToken(String token)
    {
        System.setProperty(PROPERTY_TOKEN, token);
        return this;
    }

    public KrobotRunner saveTokenIn(String path)
    {
        System.setProperty(PROPERTY_TOKEN_FILE, path);
        return this;
    }

    public KrobotRunner disableTokenSaving()
    {
        System.setProperty(PROPERTY_DISABLE_TOKEN_SAVING, "true");
        return this;
    }

    public KrobotRunner disableAskingToken()
    {
        System.setProperty(PROPERTY_DISABLE_ASKING_TOKEN, "true");
        return this;
    }

    public KrobotRunner disableStartMessage()
    {
        System.setProperty(PROPERTY_DISABLE_START_MESSAGE, "true");
        return this;
    }

    public KrobotRunner disableStateBar()
    {
        System.setProperty(PROPERTY_DISABLE_STATE_BAR, "true");
        return this;
    }

    public KrobotRunner readTokenFromArgs(String[] args)
    {
        if (args.length > 0)
        {
            return setToken(args[0]);
        }

        return this;
    }

    public KrobotRuntime run(Class<? extends KrobotModule> bot)
    {
        String[] checks = {null, Krobot.PROPERTY_DISABLE_TOKEN_SAVING, Krobot.PROPERTY_DISABLE_ASKING_TOKEN};

        while ((token == null || (token = token.trim()).isEmpty()) && trial < 3)
        {
            String check = checks[trial];

            if (check == null || System.getProperty(check) == null || System.getProperty(check).equalsIgnoreCase("true"))
            {
                token = next();
            }

            trial++;
        }

        if (token == null)
        {
            System.err.println("Couldn't find any way to retrieve the bot token\nUnable to launch : all possibles ways are disabled/unusable\n\nYou can set it by passing it as an argument if the bot allows it, or using -Dkrobot.token=thetoken as JVM argument");
            System.exit(1);
        }

        if (token.length() < 20)
        {
            System.err.println("The provided token '" + token + "' is way to small\nUnable to launch");
            System.exit(1);
        }

        System.out.println("Using token '" + token.substring(0, token.length() - 20) + "********************'\n");

        return KrobotRuntime.start(bot, token);
    }

    private String next()
    {
        switch (trial)
        {
            case 0:
                return System.getProperty(Krobot.PROPERTY_TOKEN);
            case 1:
                String path = System.getProperty(Krobot.PROPERTY_TOKEN_FILE);
                File file = new File(path == null ? ".token" : path);

                try
                {
                    return Files.readFirstLine(file, Charset.defaultCharset());
                }
                catch (IOException ignored)
                {
                }
            case 2:
                if (System.console() == null)
                {
                    return null;
                }

                System.out.print("Enter bot token : ");
                Scanner sc = new Scanner(System.in);
                token = sc.nextLine();

                sc.close();
        }

        return null;
    }
}
