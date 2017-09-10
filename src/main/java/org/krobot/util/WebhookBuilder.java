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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.Webhook;
import net.dv8tion.jda.core.requests.Request;
import net.dv8tion.jda.core.requests.Requester;
import net.dv8tion.jda.core.requests.Response;
import net.dv8tion.jda.core.requests.RestAction;
import net.dv8tion.jda.core.requests.Route;
import net.dv8tion.jda.core.requests.restaction.WebhookAction;
import okhttp3.RequestBody;

public class WebhookBuilder
{
    private static final Gson gson = new GsonBuilder().create();

    private transient Webhook webhook;

    private String content;
    private String username;

    @SerializedName("avatar_url")
    private String avatarUrl;

    private boolean tts;
    private List<MessageEmbed> embeds;

    protected WebhookBuilder(Webhook webhook)
    {
        this.webhook = webhook;
    }

    public WebhookBuilder setContent(String content)
    {
        this.content = content;
        return this;
    }

    public WebhookBuilder setUsername(String username)
    {
        this.username = username;
        return this;
    }

    public WebhookBuilder setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public WebhookBuilder setTts(boolean tts)
    {
        this.tts = tts;
        return this;
    }

    public WebhookBuilder addEmbed(EmbedBuilder embed)
    {
        return addEmbed(embed.build());
    }

    public RestAction<Void> execute()
    {
        RequestBody body = RequestBody.create(Requester.MEDIA_TYPE_JSON, gson.toJson(this));

        return new RestAction<Void>(webhook.getJDA(), Route.Webhooks.EXECUTE_WEBHOOK.compile(webhook.getId(), webhook.getToken()), body)
        {
            @Override
            protected void handleResponse(Response response, Request<Void> request)
            {
                if (response.isOk())
                {
                    request.onSuccess(null);
                }
                else
                {
                    request.onFailure(response);
                }
            }
        };
    }

    public WebhookBuilder addEmbed(MessageEmbed embed)
    {
        embeds.add(embed);
        return this;
    }

    public static WebhookBuilder from(Webhook webhook)
    {
        return new WebhookBuilder(webhook);
    }

    public static WebhookBuilder from(String name, TextChannel channel)
    {
        return from(name, channel, null);
    }

    public static WebhookBuilder from(String name, TextChannel channel, Icon icon)
    {
        for (Webhook hook : channel.getWebhooks().complete())
        {
            if (hook.getName().equals(name))
            {
                return from(hook);
            }
        }

        WebhookAction builder = channel.getGuild().getController().createWebhook(channel, name);

        if (icon != null)
        {
            builder.setAvatar(icon);
        }

        return from(builder.complete());
    }
}
