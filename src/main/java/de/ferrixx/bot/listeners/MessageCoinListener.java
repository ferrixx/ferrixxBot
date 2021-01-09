package de.ferrixx.bot.listeners;/*
 * Copyright (c) 2021 ferrixxDE - Justin Ippen
 */

import de.ferrixx.bot.settings.Settings;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class MessageCoinListener implements MessageCreateListener {

    @Override
    public void onMessageCreate(MessageCreateEvent e) {
        if (!e.getMessageAuthor().isRegularUser()) return;
        if (e.getServer().isEmpty()) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Settings.embedcolorerror)
                    .setTitle("Bitte benutze Befehle auf einem Server Channel aus und nicht im Privaten Chat!"));
        }
    }
}
