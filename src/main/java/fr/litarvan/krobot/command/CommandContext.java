package fr.litarvan.krobot.command;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class CommandContext
{
    private User user;
    private Message message;
    private TextChannel channel;

    public CommandContext(User user, Message message, TextChannel channel)
    {
        this.user = user;
        this.message = message;
        this.channel = channel;
    }

    public User getUser()
    {
        return user;
    }

    public Message getMessage()
    {
        return message;
    }

    public TextChannel getChannel()
    {
        return channel;
    }
}
