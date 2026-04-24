package Japser.ListenerEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Japser.Feature.Blackjack.blackjackcommand;
import static Japser.Feature.Calculator.mathcommand;
import static Japser.Feature.Calculator.mathinputconvert;
import static Japser.Feature.Chess.*;
import static Japser.Feature.Coinflip.coinflipcommand;
import static Japser.Feature.Coinflip.coinflipguesscommand;
import static Japser.Feature.Diceroll.dicerollcommand;
import static Japser.Feature.Diceroll.dicerollguesscommand;
import static Japser.Feature.Help.helpcommand;
import static Japser.Feature.Rockpaperscissor.directgbkcommand;
import static Japser.Feature.Rockpaperscissor.gbkcommand;
import static Japser.Feature.Weather.weathercommand;
import static Japser.VariableList.*;
import static Japser.VariableList.username;

public class ListenerMessage extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        //LIST VARIABEL
        TIME = LocalDateTime.now();
        String TIMESTAMP = TIME.getYear()+"-"+TIME.getMonthValue()+"-"+TIME.getDayOfMonth()+"||"+TIME.getHour()+":"+TIME.getMinute()+":"+TIME.getSecond();

        String inputuser = event.getMessage().getContentRaw().toLowerCase();
        username = event.getAuthor().getName();
        displayname = event.getAuthor().getEffectiveName();
        channelname = event.getChannel().getName();
        if(event.isFromGuild()|| adminid.equals(event.getAuthor().getId())){
            if(inputuser.startsWith("!")||inputuser.startsWith("=")){
                if(!event.getChannel().getId().equals("1350804802115993702")) {
                    // ||------------------------- List command -----------------------------||
                    if (inputuser.equals("!jhelp") || inputuser.equals("!jh")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        helpcommand(event); //METHOD
                    }
                    else if (inputuser.startsWith("!jhelp ") || inputuser.startsWith("!jh ")) {
                        String helptype = null;
                        if (inputuser.startsWith("!jhelp ")) {
                            helptype = inputuser.substring(7);
                        } else if (inputuser.startsWith("!jh ")) {
                            helptype = inputuser.substring(4);
                        }
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        helpcommand(event, helptype);//METHOD
                    }
                    // ||------------------------- MATH -----------------------------||
                    else if (inputuser.startsWith("!math ")) {
                        System.out.println(YYEAR + "-" + MMONTH + "-" + DDAY + "||" + HHOUR + ":" + MMINUTE + ":" + SSECOND + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        String mathinput;
                        mathinput = inputuser.substring(6);
                        try {
                            mathcommand(mathinput, event); //METHOD
                        } catch (NumberFormatException e) {
                            EmbedBuilder errormathembd = new EmbedBuilder();
                            errormathembd.setColor(0xab00ff);
                            errormathembd.setTitle("ERROR! Kalkulator");
                            errormathembd.setDescription(mathinputconvert(mathinput) + "\n**Angka terlalu besar!**");
                            event.getMessage().replyEmbeds(errormathembd.build()).queue();
                        }
                    }
                    else if (inputuser.equals("!math")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        EmbedBuilder errormathembdmsarg = new EmbedBuilder().setColor(0xab00ff)
                                .setTitle("Apakah kamu menggunakan command **!math** ?").setDescription("Kamu tidak menambahkan operasi matematikanya");
                        event.getMessage().replyEmbeds(errormathembdmsarg.build()).queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
                        event.getMessage().delete().queueAfter(8, TimeUnit.SECONDS);
                    }
                    else if (inputuser.startsWith("= ")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        String mathinput;
                        mathinput = inputuser.substring(2);
                        try {
                            mathcommand(mathinput, event); //METHOD
                        } catch (NumberFormatException e) {
                            EmbedBuilder errormathembd = new EmbedBuilder();
                            errormathembd.setColor(0xab00ff);
                            errormathembd.setTitle("ERROR! Kalkulator");
                            errormathembd.setDescription(mathinputconvert(mathinput) + "\n**Angka terlalu besar!**");
                            event.getMessage().replyEmbeds(errormathembd.build()).queue();
                        }
                    }
                    else if (inputuser.equals("=")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        EmbedBuilder errormathembdmsarg = new EmbedBuilder().setColor(0xab00ff)
                                .setTitle("Apakah kamu menggunakan command **=** ?").setDescription("Kamu tidak menambahkan operasi matematikanya");
                        event.getMessage().replyEmbeds(errormathembdmsarg.build()).queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
                        event.getMessage().delete().queueAfter(8, TimeUnit.SECONDS);
                    }
                    // ||----------------------------- Coin flip ------------------------||
                    else if (inputuser.equals("!coinflip") || inputuser.equals("!cf")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        coinflipcommand(event);
                    } //METHOD
                    else if (inputuser.startsWith("!coinflip ") || inputuser.startsWith("!cf ")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        String cfguess = inputuser.startsWith("!coinflip ") ? "!coinflip " : "!cf ";
                        String cfselect = inputuser.substring(cfguess.length());
                        switch (cfselect) {
                            case "h" -> cfselect = "Head";
                            case "t" -> cfselect = "Tail";
                        }
                        coinflipguesscommand(cfselect, event); //METHOD
                    }
                    //||------------------------------ Dice roll ---------------------------||
                    else if (inputuser.equals("!diceroll") || inputuser.equals("!dr")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        dicerollcommand(event);
                    } //METHOD
                    else if (inputuser.startsWith("!diceroll ") || inputuser.startsWith("!dr ")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        String drguess = inputuser.startsWith("!diceroll ") ? "!diceroll " : "!dr ";
                        String drguessnum = inputuser.substring(drguess.length());
                        dicerollguesscommand(drguessnum, event); //METHOD
                    }
                    //||-------------------------------- BlackJack game -------------------------------||
                    else if (inputuser.startsWith("!blackjack") || inputuser.startsWith("!bj")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        blackjackcommand(event, inputuser);
                    }
                    //||-------------------------------- ROCK PAPER SCISSORS ---------------------------------||
                    else if (inputuser.equals("!batu") || inputuser.equals("!gunting") || inputuser.equals("!kertas") || inputuser.equals("!b") || inputuser.equals("!g") || inputuser.equals("!k")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        String yourchoice = inputuser.substring(1).toLowerCase();
                        directgbkcommand(event, yourchoice);
                    } else if (inputuser.startsWith("!gbk")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        EmbedBuilder gbkerrorembd = new EmbedBuilder();
                        gbkplayerid2 = "";
                        if (!gbkstart) {
                            gbkplayerid1 = event.getAuthor().getId();
                            gbkplayer1username = username;
                            Pattern pattern = Pattern.compile("<@\\d+>");
                            Matcher matcher = pattern.matcher(inputuser);
                            if (matcher.find()) {
                                gbkplayerid2 = matcher.group().replaceAll("[^0-9]", "");
                                gbkplayer2username = getusernicknamefromguild(gbkplayerid2);
                            } // Taro id di long
                            matcher.reset(); // Reset matcher untuk mencocokkan ulang
                            byte count = 0;
                            while (matcher.find()) {
                                count++;
                                if (count == 2) {
                                    break;
                                }
                            } //cari klo ada 2
                            if (count == 2) {
                                gbkerrorembd.setColor(0xab00ff);
                                gbkerrorembd.setTitle("**ERROR!** Batu🗿 Gunting✂️ Kertas📃");
                                gbkerrorembd.setDescription("Tolong input 1 user saja");
                                event.getMessage().replyEmbeds(gbkerrorembd.build()).queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
                            } //klo 2?
                            else {
                                if (gbkplayerid2.equals(gbkplayerid1)) {
                                    gbkerrorembd.setColor(0xab00ff);
                                    gbkerrorembd.setTitle("**ERROR!** Batu🗿 Gunting✂️ Kertas📃");
                                    gbkerrorembd.setDescription("Setress main sama diri sendiri...");
                                    event.getMessage().replyEmbeds(gbkerrorembd.build()).queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
                                } else {
                                    gbkcommand(event);
                                }
                            }
                        } else {
                            gbkerrorembd.setColor(0xab00ff);
                            gbkerrorembd.setTitle("**ERROR!** Batu🗿 Gunting✂️ Kertas📃");
                            gbkerrorembd.setDescription("User lain sedang bermain...");
                            event.getMessage().replyEmbeds(gbkerrorembd.build()).queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
                        }
                    }
                    //||------------------------------------- WEATHER --------------------------------------||
                    else if (inputuser.startsWith("!weather") || inputuser.startsWith("!cuaca") || inputuser.startsWith("!w")) {
                        System.out.println(TIMESTAMP + " (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                        String inputcity;
                        if (inputuser.startsWith("!cuaca")) {
                            inputcity = (inputuser.startsWith("!cuaca")) ? "!cuaca " : "!c ";
                        } else {
                            inputcity = (inputuser.startsWith("!weather")) ? "!weather " : "!w ";
                        }
                        String city = inputuser.substring(inputcity.length()).replaceAll(" ", "%20");
                        weathercommand(event, city);
                    }
                }
                //||--------------------------------- CHESS --------------------------------||
                else if(inputuser.startsWith("!chess")||inputuser.startsWith("!ch") ){
                    System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                    if(!event.getChannel().getId().equals("1350804802115993702")){
                        EmbedBuilder chesserrorembd = new EmbedBuilder().setColor(0xab00ff)
                                .setTitle("ERROR! Chess").setDescription("Mau main? di channel <#1350804802115993702>, minta sama <@781098203746795531> kalau ga ada akses");
                        event.getMessage().replyEmbeds(chesserrorembd.build()).queue(msg -> msg.delete().queueAfter(5,TimeUnit.SECONDS));
                        event.getMessage().delete().queueAfter(5,TimeUnit.SECONDS);
                        return;
                    }
                    chesscommand(event, inputuser);
                }
                else event.getMessage().delete().queue();
            }
            //||--------------------------------- CHESS --------------------------------||
            else if(event.getChannel().getId().equals("1350804802115993702")){
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : " + inputuser);
                if(inputuser.startsWith("m ")) chessmove(event, inputuser, event.getAuthor().getId());
                else if(inputuser.startsWith("p ")) pawnpromote(event, inputuser,event.getAuthor().getId());
                else event.getMessage().delete().queue();
            }
        }
        else{
            System.out.println(TIMESTAMP+" ("+channelname+").("+displayname+")("+username+") : "+inputuser);
            if(inputuser.startsWith("!")||inputuser.startsWith("=")) event.getMessage()
                    .reply("tidak menerima command di private messages, silahkan input di server https://discord.com/invite/fbAZSd3Hf2").queue();
        }
    }
}
