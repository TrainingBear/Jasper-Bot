package Japser.Feature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

import static Japser.VariableList.random;

public class Diceroll {
    public static void dicerollcommand(MessageReceivedEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,dicenumber);
    }
    public static void dicerollcommand(SlashCommandInteractionEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,dicenumber);
    }
    //
    public static void dicerollguesscommand(String drguessnum,MessageReceivedEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,drguessnum,dicenumber);
    }
    public static void dicerollguesscommand(String drguessnum,SlashCommandInteractionEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,drguessnum,dicenumber);
    }

    public static void Drembd(MessageReceivedEvent event,int dicenumber){
        int time = random.nextInt(4,7);
        EmbedBuilder drembd = new EmbedBuilder();
        drembd.setColor(0x9d0070);
        drembd.setTitle("Dadu sedang di kocok...");
        drembd.setDescription("Dice menghadap angka ...");
        event.getMessage().replyEmbeds(drembd.build()).queue(sentmessage ->{
            drembd.setColor(0xff00b6);
            drembd.setTitle("Dadu sudah di kocok!");
            drembd.setDescription("Dice menghadap angka **"+dicenumber+"**");
            sentmessage.editMessageEmbeds(drembd.build()).queueAfter(time, TimeUnit.SECONDS);});
    }
    public static void Drembd(SlashCommandInteractionEvent event,int dicenumber){
        int time = random.nextInt(4,7);
        EmbedBuilder drembd = new EmbedBuilder();
        drembd.setColor(0x9d0070);
        drembd.setTitle("Dadu sedang di kocok...");
        drembd.setDescription("Dice menghadap angka ...");
        event.replyEmbeds(drembd.build()).queue(sentmessage ->{
            drembd.setColor(0xff00b6);
            drembd.setTitle("Dadu sudah di kocok!");
            drembd.setDescription("Dice menghadap angka **"+dicenumber+"**");
            sentmessage.editOriginalEmbeds(drembd.build()).queueAfter(time, TimeUnit.SECONDS);});
    }
    //
    public static void Drembd(MessageReceivedEvent event,String drguessnum,int dicenum){
        int time = random.nextInt(4,7);
        EmbedBuilder drgembd = new EmbedBuilder();
        if(drguessnum.contains(",")||drguessnum.contains(".")){
            drgembd.setColor(0xab00ff); drgembd.setTitle("Diceroll");drgembd.setDescription("**TIDAK BISA DESIMAL!** Pilih angka 1-6");
            event.getMessage().replyEmbeds(drgembd.build()).queue(message -> message.delete().queueAfter(8,TimeUnit.SECONDS));
        }else if(!drguessnum.matches("[1-6]")){
            drgembd.setColor(0xab00ff); drgembd.setTitle("Diceroll");drgembd.setDescription("**Error!** Pilih angka 1-6");
            event.getMessage().replyEmbeds(drgembd.build()).queue(message -> message.delete().queueAfter(8,TimeUnit.SECONDS));
        }
        else{
            if(Integer.parseInt(drguessnum)==dicenum){
                drgembd.setColor(0x9d0070);
                drgembd.setTitle("Dadu sedang di kocok...");
                drgembd.setDescription("Dice menghadap angka ...");
                event.getMessage().replyEmbeds(drgembd.build()).queue(sentmessage->{
                    drgembd.setColor(0xff00f9);
                    drgembd.setTitle("Dadu sudah di kocok!");
                    drgembd.setDescription("**BENAR!** Dadu menghadap angka **"+dicenum+"**");
                    sentmessage.editMessageEmbeds(drgembd.build()).queueAfter(time,TimeUnit.SECONDS);});
            }else{
                drgembd.setColor(0x9d0070);
                drgembd.setTitle("Dadu sedang di kocok...");
                drgembd.setDescription("Dice menghadap angka ...");
                event.getMessage().replyEmbeds(drgembd.build()).queue(sentmessage->{
                    drgembd.setColor(0xff0089);
                    drgembd.setTitle("Dadu sudah di kocok!");
                    drgembd.setDescription("**SALAH!** Dadu menghadap angka **"+dicenum+"**");
                    sentmessage.editMessageEmbeds(drgembd.build()).queueAfter(time,TimeUnit.SECONDS);});
            }
        }
    }
    public static void Drembd(SlashCommandInteractionEvent event,String drguessnum,int dicenum){
        int time = random.nextInt(4,7);
        EmbedBuilder drgembd = new EmbedBuilder();
        if(drguessnum.contains(",")||drguessnum.contains(".")){
            drgembd.setColor(0xab00ff); drgembd.setTitle("Diceroll");drgembd.setDescription("**TIDAK BISA DESIMAL!** Pilih angka 1-6");
            event.replyEmbeds(drgembd.build()).queue(message -> message.deleteOriginal().queueAfter(8,TimeUnit.SECONDS));
        }else if(!drguessnum.matches("[1-6]")){
            drgembd.setColor(0xab00ff); drgembd.setTitle("Diceroll");drgembd.setDescription("**Error!** Pilih angka 1-6");
            event.replyEmbeds(drgembd.build()).queue(message -> message.deleteOriginal().queueAfter(8,TimeUnit.SECONDS));
        }
        else{
            if(Integer.parseInt(drguessnum)==dicenum){
                drgembd.setColor(0x9d0070);
                drgembd.setTitle("Dadu sedang di kocok...");
                drgembd.setDescription("Dice menghadap angka ...");
                event.replyEmbeds(drgembd.build()).queue(sentmessage->{
                    drgembd.setColor(0xff00f9);
                    drgembd.setTitle("Dadu sudah di kocok!");
                    drgembd.setDescription("**BENAR!** Dadu menghadap angka **"+dicenum+"**");
                    sentmessage.editOriginalEmbeds(drgembd.build()).queueAfter(time,TimeUnit.SECONDS);});
            }else{
                drgembd.setColor(0x9d0070);
                drgembd.setTitle("Dadu sedang di kocok...");
                drgembd.setDescription("Dice menghadap angka ...");
                event.replyEmbeds(drgembd.build()).queue(sentmessage->{
                    drgembd.setColor(0xff0089);
                    drgembd.setTitle("Dadu sudah di kocok!");
                    drgembd.setDescription("**SALAH!** Dadu menghadap angka **"+dicenum+"**");
                    sentmessage.editOriginalEmbeds(drgembd.build()).queueAfter(time,TimeUnit.SECONDS);});
            }
        }
    }
}
