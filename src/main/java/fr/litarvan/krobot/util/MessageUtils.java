package fr.litarvan.krobot.util;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;

public final class MessageUtils
{
    public static final int MAX_MESSAGE_CHARS = 1999;

    public static PrivateChannel privateChannel(User user)
    {
        if (!user.hasPrivateChannel())
        {
            try
            {
                return user.openPrivateChannel().submit().get();
            }
            catch (InterruptedException | ExecutionException ignored)
            {
            }
        }

        return user.getPrivateChannel();
    }

    public static String[] splitMessage(String message)
    {
        return splitMessage(message, MAX_MESSAGE_CHARS);
    }

    public static String[] splitMessage(String message, int limit)
    {
        ArrayList<String> messages = new ArrayList<>();

        while (message.length() > limit)
        {
            messages.add(message.substring(0, limit));
            message = message.substring(limit, message.length());
        }

        messages.add(message);

        return messages.toArray(new String[messages.size()]);
    }
}
