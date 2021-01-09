package de.ferrixx.bot.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.vdurmont.emoji.EmojiParser;
import de.ferrixx.bot.main.Main;
import de.ferrixx.bot.settings.Settings;
import de.ferrixx.bot.utils.CoinSystem;
import de.ferrixx.bot.utils.LavaplayerAudioSource;
import de.ferrixx.bot.utils.Mutes;
import de.ferrixx.bot.utils.Warns;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class MessageListener implements MessageCreateListener {

    public static boolean filterChat(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr.toUpperCase()::contains);
    }

    public static boolean compareGroupColors(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    public static boolean checkifTeam(String input) {
        return compareGroupColors(input, Settings.teamlist);
    }

    public static boolean checkifVIP(String input) {
        return compareGroupColors(input, Settings.viplist);
    }

    public static void warnMethod(User messageUser, TextChannel channel, String format, String reason, Server server) {

        int warns = Warns.getWarns(messageUser.getIdAsString());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("ferrixxDE - WarnSystem")
                .setColor(Settings.embedcolorwarns);
        if (format.equalsIgnoreCase("Team")) {
            embed.addField("`" + messageUser.getName() + "` du wurdest von einem Teammitglied gewarnt!", "Grund: " + reason);
        } else if (format.equalsIgnoreCase("ChatFilter")) {
            embed.addField(messageUser.getName() + " du wurdest vom ChatFilter gewarnt!", "Geblocktes Wort");
        }

        if (warns >= 10) {
            embed.addField("Verwarnung Nr. " + warns, "Strafe: Ban");
            channel.sendMessage(embed);

            server.banUser(messageUser, 0, "Zu viele Verwarnungen!");

        } else if (warns > 3) {
            switch (warns) {
                case 9 -> {
                    Date date = new Date();
                    Timestamp created = new Timestamp(date.getTime());
                    Timestamp unmute = new Timestamp(date.getTime());
                    unmute.setDate(unmute.getDate() + 1);
                    embed.addField("Verwarnung Nr. " + warns, "Strafe: 1 Tag Mute");
                    channel.sendMessage(embed);
                    // 24 Stunden Mute
                    Mutes.addMute(messageUser.getIdAsString(), format + " - " + reason + " - 1 Tag", unmute, created);
                }
                case 8 -> {
                    Date date = new Date();
                    Timestamp created = new Timestamp(date.getTime());
                    Timestamp unmute = new Timestamp(date.getTime());
                    unmute.setHours(unmute.getHours() + 12);

                    embed.addField("Verwarnung Nr. " + warns, "Strafe: 12 Stunden Mute");
                    channel.sendMessage(embed);
                    // 12 Stunden Mute
                    Mutes.addMute(messageUser.getIdAsString(), format + " - " + reason + " - 12 Stunden", unmute, created);
                }
                case 7 -> {
                    Date date = new Date();
                    Timestamp created = new Timestamp(date.getTime());
                    Timestamp unmute = new Timestamp(date.getTime());
                    unmute.setHours(unmute.getHours() + 6);

                    embed.addField("Verwarnung Nr. " + warns, "Strafe: 6 Stunden Mute");
                    channel.sendMessage(embed);
                    // 6 Stunden Mute
                    Mutes.addMute(messageUser.getIdAsString(), format + " - " + reason + " - 6 Stunden", unmute, created);
                }
                case 6 -> {
                    Date date = new Date();
                    Timestamp created = new Timestamp(date.getTime());
                    Timestamp unmute = new Timestamp(date.getTime());
                    unmute.setHours(unmute.getHours() + 3);

                    embed.addField("Verwarnung Nr. " + warns, "Strafe: 3 Stunden Mute");
                    channel.sendMessage(embed);
                    // 3 Stunden Mute
                    Mutes.addMute(messageUser.getIdAsString(), format + " - " + reason + " - 3 Stunden", unmute, created);
                }
                case 5 -> {
                    Date date = new Date();
                    Timestamp created = new Timestamp(date.getTime());
                    Timestamp unmute = new Timestamp(date.getTime());
                    unmute.setHours(unmute.getHours() + 1);

                    embed.addField("Verwarnung Nr. " + warns, "Strafe: 1 Stunden Mute");
                    channel.sendMessage(embed);
                    // 1 Stunde Mute
                    Mutes.addMute(messageUser.getIdAsString(), format + " - " + reason + " - 1 Stunden", unmute, created);
                }
                case 4 -> {
                    Date date = new Date();
                    Timestamp created = new Timestamp(date.getTime());
                    Timestamp unmute = new Timestamp(date.getTime());
                    unmute.setMinutes(unmute.getMinutes() + 30);

                    embed.addField("Verwarnung Nr. " + warns, "Strafe: 30 Minuten Mute");
                    channel.sendMessage(embed);
                    // 30 Minuten Mute
                    Mutes.addMute(messageUser.getIdAsString(), format + " - " + reason + " - 30 Minuten", unmute, created);
                }
            }
            /*if (warns == 3) { Always false
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setMinutes(unmute.getMinutes() + 15);

                embed.addField("Verwarnung Nr. " + warns, "Strafe: 15 Minuten Mute");
                channel.sendMessage(embed);
                // 15 Minuten Mute
                Mutes.addMute(messageUser.getIdAsString(), format + " - " + reason + " - 15 Minuten", unmute, created);
            }*/
        } else if (warns <= 2) {
            embed.addField("Verwarnung Nr. " + warns, "Strafe: keine");
            channel.sendMessage(embed);
        }
    }

    @Override
    public void onMessageCreate(MessageCreateEvent e) {
        if (!e.getMessageAuthor().isRegularUser()) return;
        /* CHECK IF PRIVATE OR SERVER CHAT */
        if (e.getMessage().isPrivateMessage()) {
            e.getChannel().sendMessage(new EmbedBuilder()
                    .setColor(Settings.embedcolorerror)
                    .setTitle("Bitte benutze Befehle auf einem Server Channel aus und nicht im Privaten Chat!"));
            return;
        }
        if (Settings.devMode && e.getServer().isPresent() && e.getServer().get().getName().startsWith("Dev")) return;

        e.getServer().ifPresent(server -> e.getMessageAuthor().asUser().ifPresent(messageUser -> {
            /* VARIABLES */
            Optional<Color> presentedColor = messageUser.getRoleColor(server);
            String[] args = e.getMessageContent().split(" ");

            /* CHECK IF MUTED WHEN NOT UNMUTE */
            System.out.println(messageUser.getIdAsString());
            if (Mutes.getMuteDuration(messageUser.getIdAsString()) != null) {
                if (!Mutes.checkifMuted(messageUser.getIdAsString())) {
                    if (checkifTeam(presentedColor.toString())) {
                        Mutes.deleteMute(messageUser.getIdAsString());
                        return;
                    }
                    e.getMessage().delete();
                    Timestamp timestamp = Mutes.getMuteDuration(messageUser.getIdAsString());
                    messageUser.sendMessage(
                            "Du bist derzeit vom ferrixxDE - Community Discord gemutet! Warte noch bis ``" +
                                    timestamp.getHours() + ":" + timestamp.getMinutes() // Deprecated methods!
                                    + " Uhr`` ab bis du wieder etwas schreiben kannst.");
                } else if (Mutes.checkifMuted(messageUser.getIdAsString())) {
                    Mutes.deleteMute(messageUser.getIdAsString());
                }
            }

            /* CHATFILTER */
            if (filterChat(e.getMessageContent(), Settings.chatfilter)) {
                if (checkifTeam(presentedColor.toString()) || messageUser.isBotOwner()) {
                    return;
                }
                e.getMessage().delete();
                Warns.addWarn(messageUser.getIdAsString(),
                        "ChatFilter - Bad Word", e.getMessageContent(),
                        "ferrixxBot");
                warnMethod(messageUser, e.getChannel(), "ChatFilter", "Bad Word", server);

                if (!Settings.devMode && server.getTextChannelById(794691709912875038L).isPresent()) {
                    server.getTextChannelById(794691709912875038L).get().sendMessage(
                            "ChatFilter Warning cause: " + e.getMessageContent() +
                                    " | From " + messageUser.getDisplayName(server) +
                                    " #" + messageUser.getDiscriminator());
                }

            }

            /* COINSYSTEM ADD COINS FOR WORDS */
            // was is der error? gibt kein was genau klappt denn ned
            /* These ifs before didn't make any sense.
            If args.length was 30 or smaller, the first if was always being executed.
            If it was above 30 none were executed.
             */
            int addCoins = 0;
            if (args.length <= 0) {
                addCoins = 2;
            } else if (args.length <= 5) {
                addCoins = 6;
            } else if (args.length <= 10) {
                addCoins = 10;
            } else if (args.length <= 15) {
                addCoins = 15;
            } else if (args.length <= 20) {
                addCoins = 20;
            } else if (args.length <= 25) {
                addCoins = 25;
            } else if (args.length <= 30) {
                addCoins = 30;
            }

            // I assume you meant to add extra coins if the user is a VIP or team member?
            if (checkifVIP(presentedColor.toString())) {
                addCoins += 5;
            }

            if (checkifTeam(presentedColor.toString())) {
                addCoins += 10;
            }

            CoinSystem.updateCoins(messageUser.getIdAsString(), addCoins);

            /* COINSYSTEM CHECK FOR LEVEL */

            EmbedBuilder coinSystem = new EmbedBuilder();
            coinSystem.setColor(Settings.embedcolorcoinsystem);

            if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 1000 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 9) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 10);
                coinSystem.setTitle("Du bist nun Level 10! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 900 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 8) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 9);
                coinSystem.setTitle("Du bist nun Level 9! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 800 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 7) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 8);
                coinSystem.setTitle("Du bist nun Level 8! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 700 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 6) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 7);
                coinSystem.setTitle("Du bist nun Level 7! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 600 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 5) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 6);
                coinSystem.setTitle("Du bist nun Level 6! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 500 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 4) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 5);
                coinSystem.setTitle("Du bist nun Level 5! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 400 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 3) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 4);
                coinSystem.setTitle("Du bist nun Level 4! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 300 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 2) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 3);
                coinSystem.setTitle("Du bist nun Level 3! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 200 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 1) {
                CoinSystem.addLevel(messageUser.getIdAsString(), 2);
                coinSystem.setTitle("Du bist nun Level 1! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            } else if (CoinSystem.getCoins(messageUser.getIdAsString()) >= 100 &&
                    CoinSystem.getLevel(messageUser.getIdAsString()) == 0) { //getLevel() never returns null
                CoinSystem.addLevel(messageUser.getIdAsString(), 1);
                coinSystem.setTitle("Du bist nun Level 1! `" + messageUser.getDisplayName(server) + "`");
                e.getChannel().sendMessage(coinSystem);
            }


            /* COMMANDS */

            if (e.getMessageContent().startsWith("!")) {
                System.out.println("The following Command was executed: " + e.getMessageContent());

                if (!Settings.devMode && server.getTextChannelById(794691709912875038L).isPresent()) {
                    server.getTextChannelById(794691709912875038L).get().sendMessage(
                            "The following Command was executed: " + e.getMessageContent() +
                                    " | From " + messageUser.getDisplayName(server) +
                                    " #" + messageUser.getDiscriminator());
                }

                switch (e.getMessageContent().toLowerCase(Locale.ENGLISH)) {
                    case "!ping" -> e.getChannel().sendMessage("Pong!");
                    case "!coins" -> e.getChannel().sendMessage(
                            "Deine Coins:" + CoinSystem.getCoins(messageUser.getIdAsString()));
                    case "!commands", "!help" -> {
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("ferrixx's Bot Info")
                                .setDescription("Der Bot wurde am 27.12.2020 von ferrixx Programmiert.\n" +
                                        "Der Bot befindet sich derzeit in der Version " + Settings.botversion)
                                .setAuthor("ferrixxBot " + Settings.botversion,
                                        "http://ferrixx.de/discordbot", Settings.ferrixxlogo)
                                .addField("\uD83D\uDCCC Features",
                                        "ChatFilter \n CoinFlip for Fun \n CoinSystem \n MusikBot")
                                .addField("\uD83D\uDCDD Commands",
                                        "!commands \n !help \n !ping \n !userinfo \n !coinflip \n !play <youtube link>")
                                .setColor(Settings.embedcolor)
                                .setFooter(Settings.copyright, Settings.ferrixxlogo)
                                .setThumbnail(Settings.ferrixxlogo);
                        e.getChannel().sendMessage(embed);

                        if (checkifTeam(presentedColor.toString())) {
                            EmbedBuilder embedteam = new EmbedBuilder()
                                    .setTitle("ferrixx's Bot Team Commands")
                                    .setDescription("For more Information use e.g. !mute help")
                                    .setAuthor("ferrixxBot " + Settings.botversion,
                                            "http://ferrixx.de/discordbot", Settings.ferrixxlogo)
                                    .addField("\uD83D\uDCCC Features", "ChatFilter \n Warns \n Mute \n Kick")
                                    .addField("\uD83D\uDCDD Commands", "!mute \n !kick \n !warn \n")
                                    .setColor(Settings.embedcolor)
                                    .setFooter(Settings.copyright, Settings.ferrixxlogo);
                            messageUser.sendMessage(embedteam);
                        }
                    }
                }

                switch (e.getMessageContent().toLowerCase(Locale.ENGLISH).split(" ")[0]) {
                    case "!userinfo" -> {
                        if (!e.getMessage().getMentionedUsers().isEmpty()) {
                            User user = e.getMessage().getMentionedUsers().get(0);
                            if (server.getMemberById(user.getIdAsString()).isPresent()) {
                                EmbedBuilder embed = new EmbedBuilder()
                                        .setTitle("User Info")
                                        .addInlineField("Username", user.getName())
                                        .addInlineField("UserID (...#):", user.getDiscriminator() + "\n")
                                        .addInlineField("----", "----")
                                        .addInlineField("Account erstellt:", user.getCreationTimestamp().toString())
                                        .addInlineField("Server beigetreten:", server.getJoinedAtTimestamp(user).toString())
                                        .addInlineField("----", "----")
                                        .addInlineField("Coins:", CoinSystem.getCoins(user.getIdAsString()).toString())
                                        .addInlineField("Level:", CoinSystem.getLevel(user.getIdAsString()).toString())
                                        .setColor(Settings.embedcolor)
                                        .setFooter(Settings.copyright, Settings.ferrixxlogo);
                                e.getChannel().sendMessage(embed);
                            } else {
                                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Dieser User existiert nicht!").setColor(Settings.embedcolorerror));
                            }
                        } else {
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("User Info")
                                    .addInlineField("Username", e.getMessageAuthor().getName())
                                    .addInlineField("UserID (...#):", messageUser.getDiscriminator() + "\n")
                                    .addInlineField("----", "----")
                                    .addInlineField("Account erstellt:",
                                            e.getMessageAuthor().getCreationTimestamp().toString())
                                    .addInlineField("Server beigetreten:",
                                            server.getJoinedAtTimestamp(messageUser).toString())
                                    .addInlineField("----", "----")
                                    .addInlineField("Coins:",
                                            CoinSystem.getCoins(messageUser.getIdAsString()).toString())
                                    .addInlineField("Level:",
                                            CoinSystem.getLevel(messageUser.getIdAsString()).toString())
                                    .setColor(Settings.embedcolor)
                                    .setFooter(Settings.copyright, Settings.ferrixxlogo);
                            e.getChannel().sendMessage(embed);
                        }
                    }
                    case "!warn" -> {
                        if (compareGroupColors(presentedColor.toString(), Settings.teamlist)) {
                            if (args[1].contains("help")) {
                                e.getMessage().delete();
                                EmbedBuilder embed = new EmbedBuilder()
                                        .setTitle("ferrixx's Bot - Warn Help")
                                        .setAuthor("ferrixxBot " + Settings.botversion,
                                                "http://ferrixx.de/discordbot", Settings.ferrixxlogo)
                                        .addField("\uD83D\uDCCC Features",
                                                "Auto Warn beim ChatFilter\n" +
                                                        "Auto Mute bei/ab 3 Warns\n" +
                                                        "Auto Ban ab 10 Warns")
                                        .addField("\uD83D\uDCDD Commands",
                                                "!warns <UserTag> | Tag einen User um seine Warns zu sehen\n" +
                                                        "!warn <UserTag> <ReasonID> | !warn reasons um alle Reasons zu sehen")
                                        .setColor(Settings.embedcolor)
                                        .setFooter(Settings.copyright, Settings.ferrixxlogo);
                                messageUser.sendMessage(embed);
                            } else if (args[1].contains("reason") && args.length == 2) {
                                e.getMessage().delete();
                                EmbedBuilder embed = new EmbedBuilder()
                                        .setTitle("ferrixx's Bot - Warn Reasons")
                                        .setAuthor("ferrixxBot " + Settings.botversion,
                                                "http://ferrixx.de/discordbot", Settings.ferrixxlogo)
                                        .addField("1", "Spam | 1 Warns")
                                        .addField("2", "Bad Words | 1 Warns")
                                        .addField("3", "NSFW Bilder im falschen Chat | 1 Warns")
                                        .addField("4", "Provokation | 1 Warns")
                                        .addField("5", "Verhalten | 1 Warns")
                                        .addField("6", "Trolling | 1 Warns")
                                        .addField("7", "Channel Hopping | 1 Warns")
                                        .addField("8", "Beleidigung | 2 Warns")
                                        .addField("9", "Starke Provokation | 2 Warns")
                                        .addField("10", "Datenschutz | 2 Warns")
                                        .setColor(Settings.embedcolor)
                                        .setFooter(Settings.copyright, Settings.ferrixxlogo);
                                messageUser.sendMessage(embed);
                            } else if (args.length == 3) {
                                User user = e.getMessage().getMentionedUsers().get(0);

                                Optional<Color> warnUser = user.getRoleColor(server);
                                if (checkifTeam(warnUser.toString())) {
                                    messageUser.sendMessage(new EmbedBuilder()
                                            .setTitle("Du hast keine Rechte ein Teammitglied zu Verwarnen!")
                                            .setColor(Settings.embedcolornoPerms));
                                    return;
                                }

                                if (args[2].contains("1")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Spam", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Spam", server);

                                } else if (args[2].contains("2")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Bad Words", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Bad Words", server);

                                } else if (args[2].contains("3")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - NSFW Bilder im falschen Chat", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "NSFW Bilder im falschen Chat", server);

                                } else if (args[2].contains("4")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Provokation", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Provokation", server);

                                } else if (args[2].contains("5")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Verhalten",
                                            e.getMessageContent(), messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Verhalten", server);

                                } else if (args[2].contains("6")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Trolling ", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Trolling ", server);

                                } else if (args[2].contains("7")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Channel Hopping", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Channel Hopping", server);

                                } else if (args[2].contains("8")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Beleidigung", e.getMessageContent(),
                                            messageUser.getName());
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Beleidigung", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Beleidigung - 2 Warns", server);

                                } else if (args[2].contains("9")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Starke Provokation",
                                            e.getMessageContent(),
                                            messageUser.getName());
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Starke Provokation",
                                            e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Starke Provokation - 2 Warns", server);

                                } else if (args[2].contains("10")) {
                                    e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream()
                                            .filter(message -> message.getAuthor().getId() == user.getId())
                                            .limit(3)
                                            .collect(Collectors.toList()));
                                    e.getMessage().delete();
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Datenschutz", e.getMessageContent(),
                                            messageUser.getName());
                                    Warns.addWarn(user.getIdAsString(),
                                            "Warn - Datenschutz", e.getMessageContent(),
                                            messageUser.getName());
                                    warnMethod(user, e.getChannel(), "Team", "Datenschutz - 2 Warns", server);

                                }
                            } else {
                                User user1 = e.getMessage().getMentionedUsers().get(0);
                                int warns = Warns.getWarns(user1.getIdAsString());
                                EmbedBuilder embed = new EmbedBuilder()
                                        .setTitle("Warns von " + user1.getName());
                                if (warns >= 5) {
                                    embed.setColor(Settings.embedcolorwarns).addField("Warns: ",
                                            warns + " Verwarnungen")
                                            .addField("Information:",
                                                    "Dieser User war bereits mehrmals vom Chat ausgeschlossen!");
                                } else if (warns == 4) {
                                    embed.setColor(Settings.embedcolorwarns)
                                            .addField("Warns: ",
                                                    warns + " Verwarnungen")
                                            .addField("Information:",
                                                    "Dieser User war bereits einmal vom Chat ausgeschlossen!");
                                } else if (warns == 3) {
                                    embed.setColor(Settings.embedcolorwarns).addField("Warns: ", "drei Verwarnungen")
                                            .addField("Information:",
                                                    "bei der Naechsten Verwarnung wird der User mit einer Dauer " +
                                                            "von 30 Minuten vom Chat ausgeschlossen!");
                                } else if (warns == 2) {
                                    embed.setColor(Settings.embedcolororange).addField("Warns: ", "zwei Verwarnungen")
                                            .addField("Information:",
                                                    "bei der Naechsten Verwarnung wird der User mit einer Dauer " +
                                                            "von 15 Minuten vom Chat ausgeschlossen!");
                                } else if (warns == 1) {
                                    embed.setColor(Settings.embedcoloryellow)
                                            .addField("Warns: ", "eine Verwarnung");
                                } else if (warns == 0) {
                                    embed.setColor(Settings.embedcolorgreen)
                                            .addField("Warns: ", "keine Verwarnungen");
                                }
                                e.getChannel().sendMessage(embed);
                            }
                        } else {
                            messageUser.sendMessage(new EmbedBuilder()
                                    .setTitle("Du hast keine Rechte dazu!")
                                    .setColor(Settings.embedcolornoPerms));
                        }
                    }
                    case "!mute" -> {
                        if (e.getMessageAuthor().isServerAdmin()) {
                            User user1 = e.getMessage().getMentionedUsers().get(0);
                            int warns = Warns.getWarns(user1.getIdAsString());
                            if (warns >= 4 && warns <= 10) {
                                switch (warns) {
                                    case 9 -> {
                                        if (!Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 1 Tag gemutet"));
                                        } else if (Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " war bereits für 1 Tag gemutet"));
                                        }
                                    }
                                    case 8 -> {
                                        if (!Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 12 Stunden gemutet"));
                                        } else if (Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " war bereits für 12 Stunden gemutet"));
                                        }
                                    }
                                    case 7 -> {
                                        if (!Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 6 Stunden gemutet"));
                                        } else if (Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolororange).setTitle("Dieser User " + user1.getName() + " war bereits für 6 Stunden gemutet"));
                                        }
                                    }
                                    case 6 -> {
                                        if (!Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 3 Stunden gemutet"));
                                        } else if (Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolororange).setTitle("Dieser User " + user1.getName() + " war bereits für 3 Stunden gemutet"));
                                        }
                                    }
                                    case 5 -> {
                                        if (!Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 1 Stunden gemutet"));
                                        } else if (Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcoloryellow).setTitle("Dieser User " + user1.getName() + " war bereits für 1 Stunden gemutet"));
                                        }
                                    }
                                    case 4 -> {
                                        if (!Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 30 Minuten gemutet"));
                                        } else if (Mutes.checkifMuted(user1.getIdAsString())) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(Settings.embedcoloryellow).setTitle("Dieser User " + user1.getName() + " war bereits für 30 Minuten gemutet"));
                                        }
                                    }
                                }
                                    /* Never gets called because of outer if condition warn >= 4
                                    else if (warns == 3) {
                                        if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 15 Minuten gemutet"));
                                        } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                            e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcoloryellow).setTitle("Dieser User " + user1.getName() + " war bereits für 15 Minuten gemutet"));
                                        }
                                    }
                                } else if (warns == 10) { Always false
                                    e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolorwarns).setTitle("Dieser User war schon mehrmals gemutet und hat nun einen Ban"));
                                }*/
                            } else if (warns <= 2) {
                                e.getChannel().sendMessage(new EmbedBuilder()
                                        .setColor(Settings.embedcolorgreen)
                                        .setTitle("Dieser User war noch nicht gemutet"));
                            }
                        }
                    }
                    case "!socialmedia" -> {
                        if (checkifTeam(presentedColor.toString())) {
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("ferrixx's SocialMedia")
                                    .setColor(Settings.embedcolor)
                                    .setDescription("Webseite: https://ferrixx.de\n\n" +
                                            "Steam: https://ferrixx.de/steam\n" +
                                            "Steam [Second]: https://ferrixx.de/steamcs\n" +
                                            "SteamGruppe: https://ferrixx.de/steamgroup\n\n" +
                                            "Discord: https://discord.gg/mDYH6G8\n" +
                                            "TeamSpeak: https://ferrixx.de/ts\n\n" +
                                            "Twitter: https://ferrixx.de/twitter\n" +
                                            "YouTube: https://ferrixx.de/youtube\n" +
                                            "Twitch: https://ferrixx.de/twitch\n\n" +
                                            "DeinServerHost: https://ferrixx.de/hoster\n" +
                                            "Hetzner: https://hetzner.de\n\n" +
                                            "Soundcloud: https://ferrixx.de/soundcloud\n" +
                                            "Spotify: https://ferrixx.de/spotify\n\n" +
                                            "Instagram: https://ferrixx.de/instagram\n" +
                                            "Instagram [Random Pictures]: https://ferrixx.de/spaminsta \n\n" +
                                            "Github: https://ferrixx.de/github\n")
                                    .setThumbnail("https://yt3.ggpht.com/-wYS_7qi5f94/AAAAAAAAAAI/AAAAAAAAAnk/2ftDDFUhPKI/s288-mo-c-c0xffffffff-rj-k-no/photo.jpg")
                                    .setFooter(Settings.copyright);
                            e.getChannel().sendMessage(embed);
                        }
                    }
                    case "!mostcurrentgame" -> {/*TODO: Empty*/}
                    case "!coinflip" -> {
                        int int_random = new Random().nextInt(2) + 1;
                        if (int_random == 1) {
                            e.getMessage().addReaction(EmojiParser.parseToUnicode(":black_large_square:"));
                        } else {
                            e.getMessage().addReaction(EmojiParser.parseToUnicode(":white_large_square:"));
                        }
                    }
                    case "!play" -> {
                        if (args.length == 2) {
                            if (args[1].startsWith("https://www.youtube.com/watch") ||
                                    args[1].startsWith("https://youtube.com/watch")) {
                                if (messageUser.getConnectedVoiceChannel(server).isPresent()) {
                                    ServerVoiceChannel channel = messageUser.getConnectedVoiceChannel(server).get();

                                    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                                    playerManager.registerSourceManager(new YoutubeAudioSourceManager());
                                    AudioPlayer player = playerManager.createPlayer();

                                    AudioSource source = new LavaplayerAudioSource(Main.api, player);

                                    channel.connect().thenAccept(
                                            audioConnection -> audioConnection.setAudioSource(source))
                                            .exceptionally(error -> {
                                                error.printStackTrace();
                                                return null;
                                            });

                                    playerManager.loadItem(args[1], new AudioLoadResultHandler() {
                                        @Override
                                        public void trackLoaded(AudioTrack track) {
                                            player.playTrack(track);
                                        }

                                        @Override
                                        public void playlistLoaded(AudioPlaylist playlist) {
                                            for (AudioTrack track : playlist.getTracks()) {
                                                player.playTrack(track);
                                            }
                                        }

                                        @Override
                                        public void noMatches() {
                                            e.getChannel().sendMessage(new EmbedBuilder()
                                                    .setTitle("Dieses Video existiert nicht!")
                                                    .setColor(Settings.embedcolorerror));
                                        }

                                        @Override
                                        public void loadFailed(FriendlyException exception) {
                                            e.getChannel().sendMessage(new EmbedBuilder()
                                                    .setTitle("Es ist ein Fehler aufgetreten!")
                                                    .setColor(Settings.embedcolorerror));

                                        }
                                    });
                                } else {
                                    e.getChannel().sendMessage(new EmbedBuilder()
                                            .setTitle("Du befindest dich in keinem Voice-Channel.")
                                            .setColor(Settings.embedcolorerror));
                                }
                            } else {
                                e.getChannel().sendMessage(new EmbedBuilder()
                                        .setTitle("Du musst einen YouTube link angeben.")
                                        .setColor(Settings.embedcolorerror));
                            }
                        } else {
                            e.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Du musst einen YouTube link angeben.")
                                    .setColor(Settings.embedcolorerror));
                        }
                    }
                }
            }
        }));
    }
}
