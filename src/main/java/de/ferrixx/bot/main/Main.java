package de.ferrixx.bot.main;

import de.ferrixx.bot.listeners.MessageListener;
import de.ferrixx.bot.settings.privates;
import de.ferrixx.bot.settings.settings;
import de.ferrixx.bot.utils.MySQL;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.util.logging.FallbackLoggerConfiguration;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class Main {

    public static DiscordApi api;

    public static void main(String[] args) throws LoginException, IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Which Mode you wanna Start? - Type 'dev' for Dev Mode or 'normal' for Normal Mode");

        String mode = reader.readLine();

        if(mode.equalsIgnoreCase("normal") || mode.equalsIgnoreCase("2")) {
            /* Live Bot */

            settings.devMode = false;

            FallbackLoggerConfiguration.setTrace(true);
            api = new DiscordApiBuilder().setToken(privates.token).login().join();
            api.updateActivity(ActivityType.PLAYING, settings.botversion+" | Bugs? Melde dich im Discord Channel 'bugs'! :)");
            MySQL.connect();

        } else {
            /* Entwicklungs Bot */

            settings.devMode = true;

            FallbackLoggerConfiguration.setDebug(true);
            FallbackLoggerConfiguration.setTrace(true);
            api = new DiscordApiBuilder().setToken(privates.devtoken).login().join();
            api.updateActivity(ActivityType.PLAYING, "An mir wird derzeit Programmiert!");
            MySQL.devconnect();
        }

        api.addListener(new MessageListener());

        System.out.println("Invite the Bot using the following Link: " + api.createBotInvite());
    }


}
