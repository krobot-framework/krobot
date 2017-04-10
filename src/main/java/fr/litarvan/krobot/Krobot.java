package fr.litarvan.krobot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.List;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;
import org.apache.commons.lang3.ArrayUtils;

public class Krobot extends AbstractModule
{
    private static boolean running = false;
    private static IBot bot;
    private static Injector injector;
    private static JDA jda;

    public static void start(String token, Class<? extends IBot> botCl, Module... modules) throws LoginException, InterruptedException, RateLimitedException
    {
        if (running)
        {
            throw new RuntimeException("Already up");
        }

        running = true;

        jda = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
        jda.setEventManager(new AnnotatedEventManager());

        injector = Guice.createInjector(ArrayUtils.add(modules, new Krobot()));
        bot = injector.getInstance(botCl);

        jda.addEventListener(bot);

        bot.init();
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
