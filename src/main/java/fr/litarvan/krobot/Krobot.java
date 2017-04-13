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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main Krobot Class<br><br>
 *
 *
 * Contains the framework base like JDA, the bot, and the Injector.<br>
 * Used to start a bot, and as Guice module.<br><br>
 *
 * Use {@link #start(String, Class, Module...)} to start a bot.
 *
 * @author Litarvan
 * @version 2.1.1
 * @since 2.0.0
 */
public class Krobot extends AbstractModule
{
    /**
     * The Krobot version
     */
    public static final String VERSION = "2.1.1";

    private static final Logger LOGGER = LogManager.getLogger("Krobot");

    private static boolean running = false;
    private static IBot bot;
    private static Injector injector;
    private static JDA jda;

    /**
     * Start the bot
     *
     * @param token The Discord bot token
     * @param botCl The class of the bot to start
     * @param modules The Guice modules to use
     *
     * @throws LoginException If JDA dropped one because of a bad bot token
     * @throws InterruptedException If JDA dropped one
     * @throws RateLimitedException If JDA dropped one
     */
    public static void start(String token, Class<? extends IBot> botCl, Module... modules) throws LoginException, InterruptedException, RateLimitedException
    {
        System.out.println();

        LOGGER.info("                                     ");
        LOGGER.info("         hMMMMM:      -dMMMMMMs`     ");
        LOGGER.info("        +MMMMMs     oNMMMMMd-        ");
        LOGGER.info("       .MMMMMm   -dMMMMMN+           ");
        LOGGER.info("       +MMMMMo.yMMMMMNo`             ");
        LOGGER.info("       dMMMMMyMMMMMMy`               ");
        LOGGER.info("      oMMMMMMMMMMMMMMy               ");
        LOGGER.info("      mMMMMMMNs-mMMMMMo              ");
        LOGGER.info("     -MMMMMNo`  .NMMMMM+             ");
        LOGGER.info("    sMMMMMd       yMMMMMN.           ");
        LOGGER.info("   sMMMMM+        `dMMMMMd`          ");
        LOGGER.info("   /+++++`         `++++++-          ");
        LOGGER.info("                                     \n");

        LOGGER.info("Starting Krobot v" + VERSION);
        LOGGER.debug("Using bot " + botCl.getName());

        if (running)
        {
            throw new RuntimeException("Already up");
        }

        running = true;

        LOGGER.info("Loading JDA...\n");

        jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        jda.setEventManager(new AnnotatedEventManager());

        System.out.println();

        LOGGER.info("Creating app...");

        injector = Guice.createInjector(ArrayUtils.add(modules, new Krobot()));
        bot = injector.getInstance(botCl);

        jda.addEventListener(bot);

        LOGGER.info("Loading app...\n");

        bot.init();

        System.out.println();

        LOGGER.info("-> Started\n");
    }

    @Override
    protected void configure()
    {
        bind(JDA.class).toInstance(jda);
    }

    public static Injector injector()
    {
        return injector;
    }

    public static JDA jda()
    {
        return jda;
    }

    public static IBot bot()
    {
        return bot;
    }
}
