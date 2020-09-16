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
package org.krobot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.krobot.Krobot;
import org.krobot.MessageContext;

public class Interact
{
    public static final long DEFAULT_TIMEOUT = 15_000L;

    public static final String YES = "\u2705";
    public static final String NO = "\u274e";

    private Message message;
    private User author;
    private List<InteractAction> actions;

    private boolean thenDelete = false;

    protected Interact(Message message, User author, long timeout)
    {
        this.message = message;
        this.author = author;
        this.actions = new ArrayList<>();

        Krobot.getRuntime().jda().addEventListener(this);

        if (timeout > 0)
        {
            new Thread(() -> {
                try
                {
                    Thread.sleep(timeout);
                }
                catch (InterruptedException ignored)
                {
                }

                if (this.message == null)
                {
                    return;
                }

                this.message.delete().queue();
                remove(Krobot.getRuntime().jda());
            }).start();
        }
    }

    public Interact thenDelete()
    {
        this.thenDelete = true;
        return this;
    }

    public Interact on(String emote, Consumer<MessageContext> runnable)
    {
        message.addReaction(emote).queue();
        addAction(new InteractAction(emote, runnable));

        return this;
    }

    public Interact on(Emote emote, Consumer<MessageContext> runnable)
    {
        message.addReaction(emote).queue();
        addAction(new InteractAction(emote, runnable));

        return this;
    }

    @SubscribeEvent
    protected void onReaction(MessageReactionAddEvent event)
    {
        if (!event.getMessageId().equals(this.message.getId()) || actions == null || event.getUser() instanceof SelfUser)
        {
            return;
        }

        if (author != null && event.getUserIdLong() != author.getIdLong())
        {
            return;
        }

        ReactionEmote reaction = event.getReactionEmote();
        MessageContext context = new MessageContext(event.getJDA(), event.retrieveUser().complete(), this.message, event.getTextChannel());

        for (InteractAction action : this.actions)
        {
            if (reaction.isEmote() && action.emote != null && reaction.getEmote().getId().equals(action.emote.getId())
                || !reaction.isEmote() && action.stringEmote != null && reaction.getName().equals(action.stringEmote))
            {
                action.runnable.accept(context);

                if (thenDelete)
                {
                    this.message.delete().queue();
                    this.remove(event.getJDA());
                }
            }
        }
    }

    @SubscribeEvent
    protected void onDelete(MessageDeleteEvent event)
    {
        if (!event.getMessageId().equals(this.message.getId()) || actions == null)
        {
            return;
        }

        remove(event.getJDA());
    }

    protected void remove(JDA jda)
    {
        jda.removeEventListener(this);

        message = null;
        actions = null;
    }

    protected void addAction(InteractAction action)
    {
        this.actions.add(action);
    }

    public static Interact from(RestAction<Message> message)
    {
        return from(message, DEFAULT_TIMEOUT);
    }

    public static Interact from(RestAction<Message> message, long timeout)
    {
        return from(message.complete(), timeout);
    }

    public static Interact from(CompletableFuture<Message> message)
    {
        return from(message, DEFAULT_TIMEOUT);
    }

    public static Interact from(CompletableFuture<Message> message, long timeout)
    {
        try
        {
            return from(message.get());
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Interact from(CompletableFuture<Message> message, User author)
    {
        try
        {
            return from(message.get(), author);
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Interact from(Message message)
    {
        return from(message, DEFAULT_TIMEOUT);
    }

    public static Interact from(Message message, User author)
    {
        return from(message, author, DEFAULT_TIMEOUT);
    }

    public static Interact from(Message message, long timeout)
    {
        return from(message, null, timeout);
    }

    public static Interact from(Message message, User author, long timeout)
    {
        return new Interact(message, author, timeout);
    }

    public static class InteractAction
    {
        private String stringEmote;
        private Emote emote;

        private Consumer<MessageContext> runnable;

        public InteractAction(String stringEmote, Consumer<MessageContext> runnable)
        {
            this.stringEmote = stringEmote;
            this.runnable = runnable;
        }

        public InteractAction(Emote emote, Consumer<MessageContext> runnable)
        {
            this.emote = emote;
            this.runnable = runnable;
        }
    }
}
