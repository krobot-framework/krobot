package fr.litarvan.krobot.util;

import fr.litarvan.krobot.Krobot;
import java.util.List;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

public final class UserUtils
{
    private static JDA jda = Krobot.jda();

    public static User resolve(String user)
    {
        List<User> users = jda.getUsersByName(user, true);

        if (users.size() == 0 && user.startsWith("@"))
        {
            users = jda.getUsersByName(user.substring(1), true);
        }

        if (users.size() == 0)
        {
            return jda.getUserById(user);
        }

        return users.get(0);
    }
}
