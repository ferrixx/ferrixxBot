package de.ferrixx.bot.listeners;/*
 * Copyright (c) 2021 ferrixxDE - Justin Ippen
 */

import de.ferrixx.bot.settings.settings;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MessageCoinListener implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent e) {
        if(e.getMessageAuthor().isBotUser()) return;

        String[] args = e.getMessageContent().split(" ");

        Server server;
        if(e.getServer().isPresent()) {
            server = e.getServer().get();
        } else {
            e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolorerror).setTitle("Bitte benutze Befehle auf einem Server Channel aus und nicht im Privaten Chat!"));
            return;
        }

    }
}
