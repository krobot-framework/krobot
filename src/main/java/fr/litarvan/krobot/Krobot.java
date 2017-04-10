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

public class Krobot extends AbstractModule
{
    public static final String VERSION = "1.0.0";

    private static final Logger LOGGER = LogManager.getLogger("Krobot");

    private static boolean running = false;
    private static IBot bot;
    private static Injector injector;
    private static JDA jda;

    public static void start(String token, Class<? extends IBot> botCl, Module... modules) throws LoginException, InterruptedException, RateLimitedException
    {
        System.out.println();

        LOGGER.info("                                     ");
        LOGGER.info("         .:::::.         -::::::-    ");
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
        LOGGER.info("                                     ");

        System.out.println();

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
