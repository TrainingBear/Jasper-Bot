package Japser.ListenerEvent;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static Japser.Feature.Calculator.mathcommand;
import static Japser.Feature.Calculator.mathinputconvert;
import static Japser.Feature.Coinflip.coinflipcommand;
import static Japser.Feature.Coinflip.coinflipguesscommand;
import static Japser.Feature.Diceroll.dicerollcommand;
import static Japser.Feature.Diceroll.dicerollguesscommand;
import static Japser.Feature.Help.helpcommand;
import static Japser.Feature.Rockpaperscissor.directgbkembd;
import static Japser.Feature.Rockpaperscissor.gbkcommand;
import static Japser.Feature.Weather.weathercommand;
import static Japser.VariableList.*;
import static Japser.VariableList.username;

public class onSlashCommandListener extends ListenerAdapter {
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
        TIME = LocalDateTime.now();
        String TIMESTAMP = TIME.getYear()+"-"+TIME.getMonthValue()+"-"+TIME.getDayOfMonth()+"||"+TIME.getHour()+":"+TIME.getMinute()+":"+TIME.getSecond();

        channelname=event.getChannel().getName();
        displayname=event.getUser().getEffectiveName();
        String commandname = event.getName();
        username=event.getUser().getName();
        String iduser=event.getUser().getId();
        switch (event.getName()) {
            case "jhelp":
                String helptype = event.getOption("help_type") != null ? Objects.requireNonNull(event.getOption("help_type")).getAsString() : "";
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + helptype);
                if (!helptype.isEmpty()) {
                    helpcommand(event, helptype);
                }
                else {
                    helpcommand(event);
                }
                break;
            case "math":
                String mathinput = event.getOption("mathinput") != null ? Objects.requireNonNull(event.getOption("mathinput")).getAsString() : "";
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + mathinput);
                try {
                    mathcommand(mathinput, event);
                }catch(NumberFormatException e){
                    EmbedBuilder errormathembd = new EmbedBuilder();
                    errormathembd.setColor(0xab00ff);
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput) + "\n**Angka terlalu besar!**");
                    event.replyEmbeds(errormathembd.build()).queue();
                }
                break;
            case "coinflip":
                String guessCoinflip = event.getOption("guess") != null ? Objects.requireNonNull(event.getOption("guess")).getAsString() : "";
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + guessCoinflip);
                if (!guessCoinflip.isEmpty()) {
                    coinflipguesscommand(guessCoinflip, event);
                }
                else {
                    coinflipcommand(event);
                }
                break;
            case "diceroll":
                String guessDice = event.getOption("guess") != null ? Objects.requireNonNull(event.getOption("guess")).getAsString() : "";
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + guessDice);
                if (!guessDice.isEmpty()) {
                    dicerollguesscommand(guessDice, event);
                }
                else {
                    dicerollcommand(event);
                }
                break;
            /*case "blackjack":
                String player2= event.getOption("player2") != null ? Objects.requireNonNull(event.getOption("player2")).getAsString() : null;
                String player3= event.getOption("player3") != null ? Objects.requireNonNull(event.getOption("player3")).getAsString() : null;
                String player4= event.getOption("player4") != null ? Objects.requireNonNull(event.getOption("player4")).getAsString() : null;
                blackjackcommand(event, player2, player3, player4);
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + player2+" "+ player3+" "+ player4);
                break;*/
            case "batuguntingkertas":
                String choice = event.getOption("pilih") != null ? Objects.requireNonNull(event.getOption("pilih")).getAsString() : "";
                String user = event.getOption("user") != null ? Objects.requireNonNull(event.getOption("user")).getAsString() : "";
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + choice + user);
                EmbedBuilder gbkerrorembd = new EmbedBuilder();
                if(!choice.isEmpty()&&!user.isEmpty()){
                    event.reply("Pilih salah satu saja, Pilih untuk pilih langsung, User untuk main dengan user lain").setEphemeral(true).queue();
                }
                else if (user.isEmpty()&&!choice.isEmpty()){
                    Random random = new Random();
                    byte gbkcomputerchoice = (byte)(random.nextInt(1,4)); //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
                    directgbkembd(event, gbkcomputerchoice,choice);
                }
                else{
                    if(!gbkstart){
                        gbkplayerid1=iduser;
                        gbkplayerid2= user;
                        gbkplayer1username=username;
                        if(!user.isEmpty()) {
                            gbkplayer2username = getusernicknamefromguild(user);
                        }
                        if (gbkplayerid2.equals(gbkplayerid1)) {
                            gbkerrorembd.setColor(0xab00ff);
                            gbkerrorembd.setTitle("**ERROR!** Batu🗿 Gunting✂️ Kertas📃");
                            gbkerrorembd.setDescription("Setress main sama diri sendiri...");
                            event.replyEmbeds(gbkerrorembd.build()).queue(message -> message.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
                        }
                        else {
                            gbkcommand(event);
                        }
                    }
                    else{
                        gbkerrorembd.setColor(0xab00ff);
                        gbkerrorembd.setTitle("**ERROR!** Batu🗿 Gunting✂️ Kertas📃");
                        gbkerrorembd.setDescription("User lain sedang bermain...");
                        event.replyEmbeds(gbkerrorembd.build()).queue(message -> message.deleteOriginal().queueAfter(8,TimeUnit.SECONDS));
                    }
                }
                break;
            case "cuaca":
                String city = event.getOption("kota") != null ? Objects.requireNonNull(event.getOption("kota")).getAsString() : "";
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + city);
                city = city.replaceAll(" ","%20").toLowerCase();
                weathercommand(event,city);
                break;
            case "say":
                String say = event.getOption("say") != null ? Objects.requireNonNull(event.getOption("say")).getAsString() : "";
                System.out.println(TIMESTAMP+" (" + channelname + ").(" + displayname + ")(" + username + ") : /" + commandname + " " + say);
                if (!iduser.equals("781098203746795531")) {
                    event.reply("Kamu tidak diizinkan memakai command ini!").setEphemeral(true).queue();
                    break;
                }
                    event.reply(say).queue();
                break;
            default:
                break;
        }
    }
}
