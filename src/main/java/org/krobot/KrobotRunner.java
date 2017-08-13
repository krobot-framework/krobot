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
    private String key;
    private int trial;

    public KrobotRunner()
    {
        this.trial = 0;
    }

    public KrobotRunner setKey(String key)
    {
        System.setProperty(PROPERTY_KEY, key);
        return this;
    }

    public KrobotRunner saveKeyIn(String path)
    {
        System.setProperty(PROPERTY_KEY_FILE, path);
        return this;
    }

    public KrobotRunner disableKeySaving()
    {
        System.setProperty(PROPERTY_DISABLE_KEY_SAVING, "true");
        return this;
    }

    public KrobotRunner disableAskingKey()
    {
        System.setProperty(PROPERTY_DISABLE_ASKING_KEY, "true");
        return this;
    }

    public KrobotRunner disableStartMessage()
    {
        System.setProperty(PROPERTY_DISABLE_START_MESSAGE, "true");
        return this;
    }

    public KrobotRunner readKeyFromArgs(String[] args)
    {
        if (args.length > 0)
        {
            return setKey(args[0]);
        }

        return this;
    }

    public KrobotRuntime run(Class<? extends KrobotModule> bot)
    {
        String[] checks = {null, Krobot.PROPERTY_DISABLE_KEY_SAVING, Krobot.PROPERTY_DISABLE_ASKING_KEY};

        while ((key == null || (key = key.trim()).isEmpty()) && trial < 3)
        {
            String check = checks[trial];

            if (check == null || System.getProperty(check) == null || System.getProperty(check).equalsIgnoreCase("true"))
            {
                key = next();
            }

            trial++;
        }

        if (key == null)
        {
            System.err.println("Couldn't find any way to retrieve the bot key\nUnable to launch : all possibles ways are disabled/unusable\n\nYou can set it by passing it as an argument if the bot allows it, or using -Dkrobot.key=thekey as JVM argument");
            System.exit(1);
        }

        if (key.length() < 12)
        {
            System.err.println("The provided key '" + key + "' is way to small\nUnable to launch");
            System.exit(1);
        }

        System.out.println("Using key '" + key.substring(0, key.length() - 10) + "**********'\n");

        return KrobotRuntime.start(bot, key);
    }

    private String next()
    {
        switch (trial)
        {
            case 0:
                return System.getProperty(Krobot.PROPERTY_KEY);
            case 1:
                String path = System.getProperty(Krobot.PROPERTY_KEY_FILE);
                File file = new File(path == null ? ".key" : path);

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

                System.out.print("Enter bot key : ");
                Scanner sc = new Scanner(System.in);
                key = sc.nextLine();

                sc.close();
        }

        return null;
    }
}
