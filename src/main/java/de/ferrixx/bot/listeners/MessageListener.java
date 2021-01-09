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
import de.ferrixx.bot.settings.settings;
import de.ferrixx.bot.utils.CoinSystem;
import de.ferrixx.bot.utils.LavaplayerAudioSource;
import de.ferrixx.bot.utils.Mutes;
import de.ferrixx.bot.utils.Warns;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.activity.Activity;
import org.javacord.api.entity.channel.*;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if(compareGroupColors(input, settings.teamlist)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkifVIP(String input) {
        if(compareGroupColors(input, settings.viplist)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onMessageCreate(MessageCreateEvent e) {
        if(e.getMessageAuthor().isBotUser()) return;

        /* CHECK IF PRIVATE OR SERVER CHAT */

        Server server;
        if(e.getServer().isPresent()) {
            server = e.getServer().get();
        } else {
            e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolorerror).setTitle("Bitte benutze Befehle auf einem Server Channel aus und nicht im Privaten Chat!"));
            return;
        }

        if(settings.devMode == true) {
            if(!server.getName().startsWith("Dev")) return;
        }

        /* VARIABLES */

        User messageuser = e.getMessageAuthor().asUser().get();
        Optional<Color> presentedColor = messageuser.getRoleColor(e.getServer().get());
        String[] args = e.getMessageContent().split(" ");


        /* CHECK IF MUTED WHEN NOT UNMUTE */
        System.out.println(messageuser.getIdAsString());
        if(Mutes.getMuteDuration(messageuser.getIdAsString()) != null) {
            if(Mutes.checkifMuted(messageuser.getIdAsString()) == false) {
                if(checkifTeam(presentedColor.toString())) {
                    Mutes.deleteMute(messageuser.getIdAsString());
                    return;
                }
                e.getMessage().delete();
                Timestamp timestamp = Mutes.getMuteDuration(messageuser.getIdAsString());
                messageuser.sendMessage("Du bist derzeit vom ferrixxDE - Community Discord gemutet! Warte noch bis ``" +
                        timestamp.getHours() + ":" + timestamp.getMinutes()
                        + " Uhr`` ab bis du wieder etwas schreiben kannst.");
            } else if(Mutes.checkifMuted(messageuser.getIdAsString()) == true) {
                Mutes.deleteMute(messageuser.getIdAsString());
            }

        } else {

        }

        /* CHATFILTER */

        if(filterChat(e.getMessageContent(), settings.chatfilter)) {
            if(checkifTeam(presentedColor.toString()) || messageuser.isBotOwner()) {
                return;
            }
            e.getMessage().delete();
            Warns.addWarn(messageuser.getIdAsString(), "ChatFilter - Bad Word", e.getMessageContent(), "ferrixxBot");
            WarnMethod(messageuser, e.getChannel(), "ChatFilter", "Bad Word", server);

            if(settings.devMode == false) {
                server.getTextChannelById("794691709912875038").get().sendMessage("ChatFilter Warning cause: " + e.getMessageContent() + " | From " + messageuser.getDisplayName(server) + " #" + messageuser.getDiscriminator());
            }

        }


        /* COINSYSTEM ADD COINS FOR WORDS */
    // was is der error? gibt kein was genau klappt denn ned
        Integer addcoins = 0;
        if(args.length <= 30) {
            addcoins = 30;
        } else if(args.length <= 25) {
            addcoins = 25;
        } else if(args.length <= 20) {
            addcoins = 20;
        } else if(args.length <= 15) {
            addcoins = 15;
        } else if(args.length <= 10) {
            addcoins = 10;
        } else if(args.length <= 5) {
            addcoins = 6;
        } else if(args.length <= 0) {
            addcoins = 2;
        }

        if(checkifVIP(presentedColor.toString())) {
            addcoins = +5;
        }

        if(checkifTeam(presentedColor.toString())) {
            addcoins = +10;
        }

        CoinSystem.updateCoins(messageuser.getIdAsString(), addcoins);

        /* COINSYSTEM CHECK FOR LEVEL */

        EmbedBuilder coinsystem = new EmbedBuilder();
        coinsystem.setColor(settings.embedcolorcoinsystem);

        if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 1000 && CoinSystem.getLevel(messageuser.getIdAsString()) == 9) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 10);
            coinsystem.setTitle("Du bist nun Level 10! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 900 && CoinSystem.getLevel(messageuser.getIdAsString()) == 8) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 9);
            coinsystem.setTitle("Du bist nun Level 9! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 800 && CoinSystem.getLevel(messageuser.getIdAsString()) == 7) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 8);
            coinsystem.setTitle("Du bist nun Level 8! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 700 && CoinSystem.getLevel(messageuser.getIdAsString()) == 6) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 7);
            coinsystem.setTitle("Du bist nun Level 7! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 600 && CoinSystem.getLevel(messageuser.getIdAsString()) == 5) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 6);
            coinsystem.setTitle("Du bist nun Level 6! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 500 && CoinSystem.getLevel(messageuser.getIdAsString()) == 4) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 5);
            coinsystem.setTitle("Du bist nun Level 5! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 400 && CoinSystem.getLevel(messageuser.getIdAsString()) == 3) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 4);
            coinsystem.setTitle("Du bist nun Level 4! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 300 && CoinSystem.getLevel(messageuser.getIdAsString()) == 2) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 3);
            coinsystem.setTitle("Du bist nun Level 3! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 200 && CoinSystem.getLevel(messageuser.getIdAsString()) == 1) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 2);
            coinsystem.setTitle("Du bist nun Level 1! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        } else if(CoinSystem.getCoins(messageuser.getIdAsString()) >= 100 && CoinSystem.getLevel(messageuser.getIdAsString()) == null) {
            CoinSystem.addLevel(messageuser.getIdAsString(), 1);
            coinsystem.setTitle("Du bist nun Level 1! `"+messageuser.getDisplayName(server)+"`");
            e.getChannel().sendMessage(coinsystem);
        }


        /* COMMANDS */

        if(e.getMessageContent().startsWith("!")) {
            System.out.println("The following Command was executed: " + e.getMessageContent());

            if(settings.devMode == false) {
                server.getTextChannelById("794691709912875038").get().sendMessage("The following Command was executed: " + e.getMessageContent() + " | From " + messageuser.getDisplayName(server) + " #" + messageuser.getDiscriminator());
            }

            if (e.getMessageContent().equalsIgnoreCase("!ping")) {
                e.getChannel().sendMessage("Pong!");
            } else if(e.getMessageContent().equalsIgnoreCase("!coins")) {
                e.getChannel().sendMessage("Deine Coins:" + CoinSystem.getCoins(messageuser.getIdAsString()));

            } else if (e.getMessageContent().equalsIgnoreCase("!commands") || e.getMessageContent().equalsIgnoreCase("!help")) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("ferrixx's Bot Info")
                        .setDescription("Der Bot wurde am 27.12.2020 von ferrixx Programmiert.\n" +
                                "Der Bot befindet sich derzeit in der Version " + settings.botversion)
                        .setAuthor("ferrixxBot " + settings.botversion, "http://ferrixx.de/discordbot", settings.ferrixxlogo)
                        .addField("\uD83D\uDCCC Features", "ChatFilter \n CoinFlip for Fun \n CoinSystem \n MusikBot")
                        .addField("\uD83D\uDCDD Commands", "!commands \n !help \n !ping \n !userinfo \n !coinflip \n !play <youtube link>")
                        .setColor(settings.embedcolor)
                        .setFooter(settings.copyright, settings.ferrixxlogo)
                        .setThumbnail(settings.ferrixxlogo);
                e.getChannel().sendMessage(embed);

                if(checkifTeam(presentedColor.toString())) {
                    EmbedBuilder embedteam = new EmbedBuilder()
                            .setTitle("ferrixx's Bot Team Commands")
                            .setDescription("For more Information use e.g. !mute help")
                            .setAuthor("ferrixxBot " + settings.botversion, "http://ferrixx.de/discordbot", settings.ferrixxlogo)
                            .addField("\uD83D\uDCCC Features", "ChatFilter \n Warns \n Mute \n Kick")
                            .addField("\uD83D\uDCDD Commands", "!mute \n !kick \n !warn \n")
                            .setColor(settings.embedcolor)
                            .setFooter(settings.copyright, settings.ferrixxlogo);
                    messageuser.sendMessage(embedteam);
                }

            } else if (e.getMessageContent().startsWith("!userinfo")) {
                if(!e.getMessage().getMentionedUsers().isEmpty()) {
                    User user = e.getMessage().getMentionedUsers().get(0);
                    if(server.getMemberById(user.getIdAsString()).isPresent()) {
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("User Info")
                                .addInlineField("Username", user.getName())
                                .addInlineField("UserID (...#):", user.getDiscriminator()+"\n")
                                .addInlineField("----", "----")
                                .addInlineField("Account erstellt:", user.getCreationTimestamp().toString())
                                .addInlineField("Server beigetreten:", server.getJoinedAtTimestamp(user).toString())
                                .addInlineField("----", "----")
                                .addInlineField("Coins:", CoinSystem.getCoins(user.getIdAsString()).toString())
                                .addInlineField("Level:", CoinSystem.getLevel(user.getIdAsString()).toString())
                                .setColor(settings.embedcolor)
                                .setFooter(settings.copyright, settings.ferrixxlogo);
                        e.getChannel().sendMessage(embed);
                    } else {
                        e.getChannel().sendMessage(new EmbedBuilder().setTitle("Dieser User existiert nicht!").setColor(settings.embedcolorerror));
                    }
                } else {
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("User Info")
                            .addInlineField("Username", e.getMessageAuthor().getName())
                            .addInlineField("UserID (...#):", messageuser.getDiscriminator()+"\n")
                            .addInlineField("----", "----")
                            .addInlineField("Account erstellt:", e.getMessageAuthor().getCreationTimestamp().toString())
                            .addInlineField("Server beigetreten:", server.getJoinedAtTimestamp(messageuser).toString())
                            .addInlineField("----", "----")
                            .addInlineField("Coins:", CoinSystem.getCoins(messageuser.getIdAsString()).toString())
                            .addInlineField("Level:", CoinSystem.getLevel(messageuser.getIdAsString()).toString())
                            .setColor(settings.embedcolor)
                            .setFooter(settings.copyright, settings.ferrixxlogo);
                    e.getChannel().sendMessage(embed);
                }

            } else if(e.getMessageContent().startsWith("!warn")) {
                if (compareGroupColors(presentedColor.toString(), settings.teamlist)) {
                    if (args[1].contains("help")) {
                        e.getMessage().delete();
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("ferrixx's Bot - Warn Help")
                                .setAuthor("ferrixxBot " + settings.botversion, "http://ferrixx.de/discordbot", settings.ferrixxlogo)
                                .addField("\uD83D\uDCCC Features", "Auto Warn beim ChatFilter \n Auto Mute bei/ab 3 Warns \n Auto Ban ab 10 Warns")
                                .addField("\uD83D\uDCDD Commands", "!warns <UserTag> | Tag einen User um seine Warns zu sehen \n !warn <UserTag> <ReasonID> | !warn reasons um alle Reasons zu sehen")
                                .setColor(settings.embedcolor)
                                .setFooter(settings.copyright, settings.ferrixxlogo);
                        messageuser.sendMessage(embed);
                    } else if (args[1].contains("reason")) {
                        if (args.length == 2) {
                            e.getMessage().delete();
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("ferrixx's Bot - Warn Reasons")
                                    .setAuthor("ferrixxBot " + settings.botversion, "http://ferrixx.de/discordbot", settings.ferrixxlogo)
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
                                    .setColor(settings.embedcolor)
                                    .setFooter(settings.copyright, settings.ferrixxlogo);
                            messageuser.sendMessage(embed);
                        }
                    } else if(args.length == 3) {
                            User user1 = e.getMessage().getMentionedUsers().get(0);

                            Optional<Color> warnuser = user1.getRoleColor(e.getServer().get());
                            if(checkifTeam(warnuser.toString())) {
                                messageuser.sendMessage(new EmbedBuilder().setTitle("Du hast keine Rechte ein Teammitglied zu Verwarnen!").setColor(settings.embedcolornoPerms));
                                return;
                            }


                            if (args[2].contains("1")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Spam", e.getMessageContent(), messageuser.getName());
                                    WarnMethod(user1, e.getChannel(), "Team", "Spam", server);

                            } else if (args[2].contains("2")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Bad Words", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Bad Words", server);

                            } else if (args[2].contains("3")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - NSFW Bilder im falschen Chat", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "NSFW Bilder im falschen Chat", server);

                            } else if (args[2].contains("4")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Provokation", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Provokation", server);

                            } else if (args[2].contains("5")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Verhalten", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Verhalten", server);

                            } else if (args[2].contains("6")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Trolling ", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Trolling ", server);

                            } else if (args[2].contains("7")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Channel Hopping", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Channel Hopping", server);

                            } else if (args[2].contains("8")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Beleidigung", e.getMessageContent(), messageuser.getName());
                                Warns.addWarn(user1.getIdAsString(), "Warn - Beleidigung", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Beleidigung - 2 Warns", server);

                            } else if (args[2].contains("9")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Starke Provokation", e.getMessageContent(), messageuser.getName());
                                Warns.addWarn(user1.getIdAsString(), "Warn - Starke Provokation", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Starke Provokation - 2 Warns", server);

                            } else if (args[2].contains("10")) {
                                e.getChannel().bulkDelete(e.getChannel().getMessagesAsStream().filter(message -> message.getAuthor().getId() == user1.getId()).limit(3).collect(Collectors.toList()));
                                e.getMessage().delete();
                                Warns.addWarn(user1.getIdAsString(), "Warn - Datenschutz", e.getMessageContent(), messageuser.getName());
                                Warns.addWarn(user1.getIdAsString(), "Warn - Datenschutz", e.getMessageContent(), messageuser.getName());
                                WarnMethod(user1, e.getChannel(), "Team", "Datenschutz - 2 Warns", server);

                            }

                        } else {
                            User user1 = e.getMessage().getMentionedUsers().get(0);
                            Integer warns = Warns.getWarns(user1.getIdAsString());
                            EmbedBuilder embed = new EmbedBuilder()
                                    .setTitle("Warns von " + user1.getName());
                            if (warns >= 5) {
                                embed.setColor(settings.embedcolorwarns).addField("Warns: ", warns + " Verwarnungen");
                                embed.addField("Information:", "Dieser User war bereits mehrmals vom Chat ausgeschlossen!");
                            } else if (warns == 4) {
                                embed.setColor(settings.embedcolorwarns).addField("Warns: ", warns + " Verwarnungen");
                                embed.addField("Information:", "Dieser User war bereits einmal vom Chat ausgeschlossen!");
                            } else if (warns == 3) {
                                embed.setColor(settings.embedcolorwarns).addField("Warns: ", "drei Verwarnungen");
                                embed.addField("Information:", "bei der Naechsten Verwarnung wird der User mit einer Dauer von 30 Minuten vom Chat ausgeschlossen!");
                            } else if (warns == 2) {
                                embed.setColor(settings.embedcolororange).addField("Warns: ", "zwei Verwarnungen");
                                embed.addField("Information:", "bei der Naechsten Verwarnung wird der User mit einer Dauer von 15 Minuten vom Chat ausgeschlossen!");
                            } else if (warns == 1) {
                                embed.setColor(settings.embedcoloryellow).addField("Warns: ", "eine Verwarnung");
                            } else if (warns == 0) {
                                embed.setColor(settings.embedcolorgreen).addField("Warns: ", "keine Verwarnungen");
                            }
                            e.getChannel().sendMessage(embed);
                        }
                    } else {
                        messageuser.sendMessage(new EmbedBuilder().setTitle("Du hast keine Rechte dazu!").setColor(settings.embedcolornoPerms));
                    }
                } else if (e.getMessageContent().startsWith("!mute")) {
                    if (e.getMessageAuthor().isServerAdmin()) {
                        try {
                            User user1 = e.getMessage().getMentionedUsers().get(0);
                            Integer warns = Warns.getWarns(user1.getIdAsString());
                            if (warns >= 4 && warns <= 10) {
                                if (warns == 9) {
                                    if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 1 Tag gemutet"));
                                    } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " war bereits für 1 Tag gemutet"));
                                    }
                                } else if (warns == 8) {
                                    if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 12 Stunden gemutet"));
                                    } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " war bereits für 12 Stunden gemutet"));
                                    }
                                } else if (warns == 7) {
                                    if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 6 Stunden gemutet"));
                                    } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolororange).setTitle("Dieser User " + user1.getName() + " war bereits für 6 Stunden gemutet"));
                                    }
                                } else if (warns == 6) {
                                    if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 3 Stunden gemutet"));
                                    } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolororange).setTitle("Dieser User " + user1.getName() + " war bereits für 3 Stunden gemutet"));
                                    }
                                } else if (warns == 5) {
                                    if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 1 Stunden gemutet"));
                                    } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcoloryellow).setTitle("Dieser User " + user1.getName() + " war bereits für 1 Stunden gemutet"));
                                    }
                                } else if (warns == 4) {
                                    if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 30 Minuten gemutet"));
                                    } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcoloryellow).setTitle("Dieser User " + user1.getName() + " war bereits für 30 Minuten gemutet"));
                                    }
                                } else if (warns == 3) {
                                     if (Mutes.checkifMuted(user1.getIdAsString()) == false) {
                                         e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolormutes).setTitle("Dieser User " + user1.getName() + " ist aktuell für 15 Minuten gemutet"));
                                     } else if (Mutes.checkifMuted(user1.getIdAsString()) == true) {
                                         e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcoloryellow).setTitle("Dieser User " + user1.getName() + " war bereits für 15 Minuten gemutet"));
                                     }
                                }
                            } else if(warns == 10) {
                                e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolorwarns).setTitle("Dieser User war schon mehrmals gemutet und hat nun einen Ban"));
                            } else if(warns <= 2) {
                                e.getChannel().sendMessage(new EmbedBuilder().setColor(settings.embedcolorgreen).setTitle("Dieser User war noch nicht gemutet"));
                            }
                        } catch (Exception error) {
                            System.out.println("Es ist ein Fehler aufgetreten | Fehler: " + error.getMessage());
                            error.printStackTrace();
                            e.getChannel().sendMessage("Es ist ein Fehler aufgetreten!");
                        }
                    }
                } else if(e.getMessageContent().startsWith("!socialmedia")) {
                    if(checkifTeam(presentedColor.toString())) {
                        EmbedBuilder embed = new EmbedBuilder()
                                .setTitle("ferrixx's SocialMedia")
                                .setColor(settings.embedcolor)
                                .setDescription("Webseite: https://ferrixx.de \n" +
                                                "\n" +
                                                "Steam: https://ferrixx.de/steam\n" +
                                                "Steam [Second]: https://ferrixx.de/steamcs\n" +
                                                "SteamGruppe: https://ferrixx.de/steamgroup\n" +
                                                "\n" +
                                                "Discord: https://discord.gg/mDYH6G8\n" +
                                                "TeamSpeak: https://ferrixx.de/ts\n" +
                                                "\n" +
                                                "Twitter: https://ferrixx.de/twitter\n" +
                                                "YouTube: https://ferrixx.de/youtube\n" +
                                                "Twitch: https://ferrixx.de/twitch\n" +
                                                "\n" +
                                                "DeinServerHost: https://ferrixx.de/hoster\n" +
                                                "Hetzner: https://hetzner.de\n" +
                                                "\n" +
                                                "Soundcloud: https://ferrixx.de/soundcloud\n" +
                                                "Spotify: https://ferrixx.de/spotify\n" +
                                                "\n" +
                                                "Instagram: https://ferrixx.de/instagram\n" +
                                                "Instagram [Random Pictures]: https://ferrixx.de/spaminsta \n" +
                                                "\n" +
                                                "Github: https://ferrixx.de/github\n")
                                .setThumbnail("https://yt3.ggpht.com/-wYS_7qi5f94/AAAAAAAAAAI/AAAAAAAAAnk/2ftDDFUhPKI/s288-mo-c-c0xffffffff-rj-k-no/photo.jpg")
                                .setFooter(settings.copyright);
                        e.getChannel().sendMessage(embed);
                    } else {
                        return;
                    }
                } else if(e.getMessageContent().equalsIgnoreCase("!mostcurrentgame")) {

                } else if(e.getMessageContent().equalsIgnoreCase("!coinflip")) {
                    Random rand = new Random();
                    int int_random = rand.nextInt(3);

                    if(int_random == 1) {
                        e.getMessage().addReaction(EmojiParser.parseToUnicode(":black_large_square:"));
                    } else if(int_random == 2) {
                        e.getMessage().addReaction(EmojiParser.parseToUnicode(":white_large_square:"));
                    }
                } else if(e.getMessageContent().startsWith("!play")) {
                    if(args.length == 2) {
                            if (messageuser.getConnectedVoiceChannel(server).isPresent()) {
                                ServerVoiceChannel channel = messageuser.getConnectedVoiceChannel(server).get();

                                AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
                                playerManager.registerSourceManager(new YoutubeAudioSourceManager());
                                AudioPlayer player = playerManager.createPlayer();

                                AudioSource source = new LavaplayerAudioSource(Main.api, player);

                                channel.connect().thenAccept(audioConnection -> audioConnection.setAudioSource(source)).exceptionally(error -> {
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
                                        e.getChannel().sendMessage(new EmbedBuilder().setTitle("Dieses Video existiert nicht!").setColor(settings.embedcolorerror));
                                    }

                                    @Override
                                    public void loadFailed(FriendlyException exception) {
                                        e.getChannel().sendMessage(new EmbedBuilder().setTitle("Fehler beim Laden der Musik!").setColor(settings.embedcolorerror));

                                    }
                                });
                            } else {
                                e.getChannel().sendMessage(new EmbedBuilder().setTitle("Du befindest dich in keinem Voice-Channel.").setColor(settings.embedcolorerror));
                            }
                    } else {
                        e.getChannel().sendMessage(new EmbedBuilder().setTitle("Du musst einen YouTube link angeben.").setColor(settings.embedcolorerror));
                    }
                }
            }
        }

    public static void WarnMethod(User messageuser, TextChannel channel, String format, String reason, Server server) {

        Integer warns = Warns.getWarns(messageuser.getIdAsString());

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("ferrixxDE - WarnSystem")
                .setColor(settings.embedcolorwarns);
        if(format.equalsIgnoreCase("Team")) {
            embed.addField("`"+ messageuser.getName() + "` du wurdest von einem Teammitglied gewarnt!", "Grund: " + reason);
        } else if(format.equalsIgnoreCase("ChatFilter")) {
            embed.addField(messageuser.getName() + " du wurdest vom ChatFilter gewarnt!", "Geblocktes Wort");
        }

        if (warns >= 10) {
            embed.addField("Verwarnung Nr. " + warns, "Strafe: Ban");
            channel.sendMessage(embed);

            server.banUser(messageuser, 0, "Zu viele Verwarnungen!");

        } else if (warns > 3) {

            if (warns == 9) {
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setDate(unmute.getDate() + 1);
                embed.addField("Verwarnung Nr. " + warns, "Strafe: 1 Tag Mute");
                channel.sendMessage(embed);
                // 24 Stunden Mute
                Mutes.addMute(messageuser.getIdAsString(), format+" - "+reason+" - 1 Tag", unmute, created);
            } else if (warns == 8) {
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setHours(unmute.getHours() + 12);

                embed.addField("Verwarnung Nr. " + warns, "Strafe: 12 Stunden Mute");
                channel.sendMessage(embed);
                // 12 Stunden Mute
                Mutes.addMute(messageuser.getIdAsString(), format+" - "+reason+" - 12 Stunden", unmute, created);
            } else if (warns == 7) {
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setHours(unmute.getHours() + 6);

                embed.addField("Verwarnung Nr. " + warns, "Strafe: 6 Stunden Mute");
                channel.sendMessage(embed);
                // 6 Stunden Mute
                Mutes.addMute(messageuser.getIdAsString(), format+" - "+reason+" - 6 Stunden", unmute, created);
            } else if (warns == 6) {
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setHours(unmute.getHours() + 3);

                embed.addField("Verwarnung Nr. " + warns, "Strafe: 3 Stunden Mute");
                channel.sendMessage(embed);
                // 3 Stunden Mute
                Mutes.addMute(messageuser.getIdAsString(), format+" - "+reason+" - 3 Stunden", unmute, created);
            } else if (warns == 5) {
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setHours(unmute.getHours() + 1);

                embed.addField("Verwarnung Nr. " + warns, "Strafe: 1 Stunden Mute");
                channel.sendMessage(embed);
                // 1 Stunde Mute
                Mutes.addMute(messageuser.getIdAsString(), format+" - "+reason+" - 1 Stunden", unmute, created);
            } else if (warns == 4) {
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setMinutes(unmute.getMinutes() + 30);

                embed.addField("Verwarnung Nr. " + warns, "Strafe: 30 Minuten Mute");
                channel.sendMessage(embed);
                // 30 Minuten Mute
                Mutes.addMute(messageuser.getIdAsString(), format+" - "+reason+" - 30 Minuten", unmute, created);
            } else if (warns == 3) {
                Date date = new Date();
                Timestamp created = new Timestamp(date.getTime());
                Timestamp unmute = new Timestamp(date.getTime());
                unmute.setMinutes(unmute.getMinutes() + 15);

                embed.addField("Verwarnung Nr. " + warns, "Strafe: 15 Minuten Mute");
                channel.sendMessage(embed);
                // 15 Minuten Mute
                Mutes.addMute(messageuser.getIdAsString(), format+" - "+reason+" - 15 Minuten", unmute, created);
            }

        } else if (warns == 1) {

            embed.addField("Verwarnung Nr. " + warns, "Strafe: keine");
            channel.sendMessage(embed);
        } else if (warns <= 2) {

            embed.addField("Verwarnung Nr. " + warns, "Strafe: keine");
            channel.sendMessage(embed);
        }
    }

}
