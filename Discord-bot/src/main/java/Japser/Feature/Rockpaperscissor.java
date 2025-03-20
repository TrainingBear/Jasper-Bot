package Japser.Feature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static Japser.VariableList.*;
import static Japser.VariableList.gbkembd;

public class Rockpaperscissor {
    public static void directgbkcommand(MessageReceivedEvent event, String yourchoice){
        Random random = new Random();
        byte gbkcomputerchoice = (byte)(random.nextInt(1,4)); //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
        switch(yourchoice){
            case "b"->yourchoice="batu";
            case "g"->yourchoice="gunting";
            case "k"->yourchoice="kertas";
        }
        gbkplayer1username = username;
        directgbkembd(event,gbkcomputerchoice,yourchoice);
    }
    //
    public static void gbkcommand(MessageReceivedEvent event){
        gbkstart = true;
        Random random = new Random();
        if(gbkplayerid2.isEmpty()) {
            gbkcomputerchoice = (byte) (random.nextInt(1, 4)); //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
        }
        gbkembd(event);
    }
    public static void gbkcommand(SlashCommandInteractionEvent event){
        gbkstart = true;
        Random random = new Random();
        if(gbkplayerid2.isEmpty()) {
            gbkcomputerchoice = (byte) (random.nextInt(1, 4)); //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
        }
        gbkembd(event);
    }

    public static void directgbkembd(MessageReceivedEvent event,byte gbkcomputerchoice,String yourchoice){
        EmbedBuilder gbkembd = new EmbedBuilder().setTitle("Batu🗿 Gunting✂️ Kertas📃");
        String computerchoice;
        switch (gbkcomputerchoice){
            case 1-> computerchoice = "🗿";
            case 2-> computerchoice= "📃";
            case 3-> computerchoice= "✂️";
            default-> computerchoice = "";
        }
        switch (yourchoice){
            case"batu"->yourchoice="🗿";
            case"gunting"->yourchoice="✂️";
            case"kertas"->yourchoice="📃";
            default -> yourchoice="";
        }
        if((yourchoice.equals("🗿") && computerchoice.equals("✂️"))||(yourchoice.equals("✂️") && computerchoice.equals("📃"))||(yourchoice.equals("📃") && computerchoice.equals("🗿"))){
            gbkembd.setColor(0xff00f9)
                    .setDescription("Kamu bermain dengan Computer.\nHasil:")
                    .addField("Computer:",computerchoice,true)
                    .addField(gbkplayer1username+":",yourchoice,true)
                    .setFooter("**MENANG**!");
        }
        else if((yourchoice.equals("🗿") && computerchoice.equals("📃"))||(yourchoice.equals("✂️") && computerchoice.equals("🗿"))||(yourchoice.equals("📃") && computerchoice.equals("✂️"))){
            gbkembd.setColor(0xff0e70)
                    .setDescription("Kamu bermain dengan Computer.\nHasil:")
                    .addField("Computer:",computerchoice,true)
                    .addField(gbkplayer1username+":",yourchoice,true)
                    .setFooter("**KALAH**!");
        }
        else{
            gbkembd.setColor(0xb47ca6)
                    .setDescription("Kamu bermain dengan Computer.\nHasil:")
                    .addField("Computer:",computerchoice,true)
                    .addField(gbkplayer1username+":",yourchoice,true)
                    .setFooter("**SERI**!");
        }
        event.getMessage().replyEmbeds(gbkembd.build()).queue();
    }
    public static void directgbkembd(SlashCommandInteractionEvent event,byte gbkcomputerchoice,String yourchoice){
        EmbedBuilder gbkembd = new EmbedBuilder();
        gbkplayer1username = username;
        gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
        String computerchoice;
        switch (gbkcomputerchoice){
            case 1-> computerchoice = "🗿";
            case 2-> computerchoice= "📃";
            case 3-> computerchoice= "✂️";
            default-> computerchoice = "";
        }
        switch (yourchoice){
            case"batu"->yourchoice="🗿";
            case"gunting"->yourchoice="✂️";
            case"kertas"->yourchoice="📃";
            default -> yourchoice="";
        }
        if((yourchoice.equals("🗿") && computerchoice.equals("✂️"))||(yourchoice.equals("✂️") && computerchoice.equals("📃"))||(yourchoice.equals("📃") && computerchoice.equals("🗿"))){
            gbkembd.setColor(0xff00f9);
            gbkembd.setDescription("Kamu bermain dengan Computer.\nHasil:");
            gbkembd.addField("Computer:",computerchoice,true);
            gbkembd.addField(gbkplayer1username+":",yourchoice,true);
            gbkembd.setFooter("**MENANG**!");
        }
        else if((yourchoice.equals("🗿") && computerchoice.equals("📃"))||(yourchoice.equals("✂️") && computerchoice.equals("🗿"))||(yourchoice.equals("📃") && computerchoice.equals("✂️"))){
            gbkembd.setColor(0xff0e70);
            gbkembd.setDescription("Kamu bermain dengan Computer.\nHasil:");
            gbkembd.addField("Computer:",computerchoice,true);
            gbkembd.addField(gbkplayer1username+":",yourchoice,true);
            gbkembd.setFooter("**KALAH**!");
        }
        else{
            gbkembd.setColor(0xb47ca6);
            gbkembd.setDescription("Kamu bermain dengan Computer.\nHasil:");
            gbkembd.addField("Computer:",computerchoice,true);
            gbkembd.addField(gbkplayer1username+":",yourchoice,true);
            gbkembd.setFooter("**SERI**!");
        }
        event.replyEmbeds(gbkembd.build()).queue();
    }
    //
    public static void gbkembd(MessageReceivedEvent event) {
        if(gbkplayerid2.isEmpty()||gbkplayerid2.equals("1329425665954414602")){
            String computerchoice;
            switch (gbkcomputerchoice){
                case 1-> computerchoice = "🗿";
                case 2-> computerchoice= "📃";
                case 3-> computerchoice= "✂️";
                default-> computerchoice = "";
            }
            gbkembd.setColor(0xff00b6);
            gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
            gbkembd.setDescription("Kamu bermain dengan Computer.\nMemilih...");
            gbkembd.getFields().clear();
            gbkembd.addField("Computer:", obfuscated1emoji + obfuscated2emoji + obfuscated3emoji, true);
            gbkembd.addField(gbkplayer1username + ":", obfuscated2emoji + obfuscated3emoji + obfuscated1emoji, true);
            gbkembd.setFooter("");
            event.getChannel().sendMessageEmbeds(gbkembd.build()).addActionRow(
                    Button.secondary("GBKbatu", "🗿")
                    , Button.secondary("GBKgunting", "✂️")
                    , Button.secondary("GBKkertas", "📃")).queue(message -> {
                gbkembd.setDescription("Kamu bermain dengan Computer\nHasil:");
                gbkembd.getFields().clear();
                gbkembd.addField("Computer:",computerchoice, true);
                gbkembd.addField(gbkplayer1username + ":", obfuscated2emoji + obfuscated3emoji + obfuscated1emoji, true);
                gbkembd.setFooter("Waktu Habis!").setColor(0xc23e9c);
                GBKtimer = new Timer();
                GBKtimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        message.editMessageEmbeds(gbkembd.build()).setComponents().queue();
                        GBKtimer.cancel();clearallgbk();
                    }
                }, 10000); // 15 detik
            });
        }else {
            gbkembd.getFields().clear();
            gbkembd.setColor(0x9d0070);
            gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
            gbkembd.setDescription("**Kamu bermain dengan user lain.**\nMenunggu keputusan <@"+gbkplayerid2+">...");
            gbkembd.setFooter("Jika tidak menerima dalam 18 detik maka dianggap tidak menerima");
            event.getChannel().sendMessageEmbeds(gbkembd.build()).addActionRow(
                    Button.success("GBKaccept", "Yes")
                    ,Button.danger("GBKdecline","No")).queue(message -> {
                gbkembd.setDescription("<@"+gbkplayerid2+"> **tidak menjawab...**");
                gbkembd.setFooter("Waktu Habis!");
                GBKtimer = new Timer();
                GBKtimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        message.editMessageEmbeds(gbkembd.build()).setComponents().queue();
                        GBKtimer.cancel();clearallgbk();
                    }
                }, 18000); // 18 detik
            });
        }
    }
    public static void gbkembd(SlashCommandInteractionEvent event) {
        if(gbkplayerid2.isEmpty()||gbkplayerid2.equals("1329425665954414602")){
            String computerchoice;
            switch (gbkcomputerchoice){
                case 1-> computerchoice = "🗿";
                case 2-> computerchoice= "📃";
                case 3-> computerchoice= "✂️";
                default-> computerchoice = "";
            }
            gbkembd.setColor(0xff00b6);
            gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
            gbkembd.setDescription("Kamu bermain dengan Computer.\nMemilih...");
            gbkembd.getFields().clear();
            gbkembd.addField("Computer:", obfuscated1emoji + obfuscated2emoji + obfuscated3emoji, true);
            gbkembd.addField(gbkplayer1username + ":", obfuscated2emoji + obfuscated3emoji + obfuscated1emoji, true);
            gbkembd.setFooter("");
            event.replyEmbeds(gbkembd.build()).addActionRow(
                    Button.secondary("GBKbatu", "🗿")
                    , Button.secondary("GBKgunting", "✂️")
                    , Button.secondary("GBKkertas", "📃")).queue(message -> {
                gbkembd.setDescription("Kamu bermain dengan Computer\nHasil:");
                gbkembd.getFields().clear();
                gbkembd.addField("Computer:",computerchoice, true);
                gbkembd.addField(gbkplayer1username + ":", obfuscated2emoji + obfuscated3emoji + obfuscated1emoji, true);
                gbkembd.setFooter("Waktu Habis!");
                GBKtimer = new Timer();
                GBKtimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        message.editOriginalEmbeds(gbkembd.build()).setComponents().queue();
                        GBKtimer.cancel();clearallgbk();
                    }
                }, 10000); // 15 detik
            });
        }else {
            gbkembd.getFields().clear();
            gbkembd.setColor(0x9d0070);
            gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
            gbkembd.setDescription("**Kamu bermain dengan user lain.**\nMenunggu keputusan <@"+gbkplayerid2+">...");
            gbkembd.setFooter("Jika tidak menerima dalam 18 detik maka dianggap tidak menerima");
            event.replyEmbeds(gbkembd.build()).addActionRow(
                    Button.success("GBKaccept", "Yes")
                    ,Button.danger("GBKdecline","No")).queue(message -> {
                gbkembd.setDescription("<"+gbkplayerid2+"> **tidak menjawab...**");
                gbkembd.setFooter("Waktu Habis!");
                GBKtimer = new Timer();
                GBKtimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        message.editOriginalEmbeds(gbkembd.build()).setComponents().queue();
                        GBKtimer.cancel();clearallgbk();
                    }
                }, 18000); // 18 detik
            });
        }
    }
    //
    public static void clearallgbk(){
        gbkplayerid1="";gbkplayerid2="";gbkplayer1username="";gbkplayer2username="";gbkcomputerchoice =-1;gbkplayer1choice=-1;gbkplayer2choice =-1;gbkstart = false;
    }

    public static String gbkplaceholdercomputerlambda(){
        if (gbkcomputerchoice == 1) {
            return "🗿";
        }else if(gbkcomputerchoice==2){
            return "📃";
        }else if (gbkcomputerchoice==3){
            return "✂️";
        }else{
            return obfuscated2emoji + obfuscated3emoji + obfuscated1emoji;
        }
    }
    public static String gbkplaceholder1lambda() {
        if (gbkplayer1choice == 1) {
            return "🗿";
        }else if(gbkplayer1choice==2){
            return "📃";
        }else if (gbkplayer1choice==3){
            return "✂️";
        }else{
            return obfuscated1emoji + obfuscated2emoji + obfuscated3emoji;
        }
    }
    public static String gbkplaceholder2lambda() {
        if (gbkplayer2choice == 1) {
            return "🗿";
        }else if(gbkplayer2choice==2){
            return "📃";
        }else if (gbkplayer2choice==3){
            return "✂️";
        }else{
            return obfuscated2emoji + obfuscated3emoji + obfuscated1emoji;
        }
    }
    public static void gbkcheckstatus(ButtonInteractionEvent event) {
        if (gbkplayer1choice > 0 && (!gbkplayerid2.isEmpty() && gbkplayer2choice < 0)) {
            gbkembd.setColor(0xff00b6);
            gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
            gbkembd.setDescription("Kamu bermain dengan " + gbkplayer2username + ".\nMemilih...");
            gbkembd.setFooter("");
            gbkembd.getFields().clear();
            gbkembd.addField(gbkplayer2username + ":", obfuscated1emoji + obfuscated2emoji + obfuscated3emoji, true);
            gbkembd.addField(gbkplayer1username + ":", obfuscated2emoji + obfuscated3emoji + obfuscated1emoji + "✅", true);
            event.editMessageEmbeds(gbkembd.build()).queue();
        }
        else if (gbkplayer1choice < 0 && (!gbkplayerid2.isEmpty() && gbkplayer2choice > 0)) {
            gbkembd.setColor(0xff00b6);
            gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
            gbkembd.setDescription("Kamu bermain dengan " + gbkplayer2username + ".\nMemilih...");
            gbkembd.setFooter("");
            gbkembd.getFields().clear();
            gbkembd.addField(gbkplayer2username + ":", obfuscated1emoji + obfuscated2emoji + obfuscated3emoji + "✅", true);
            gbkembd.addField(gbkplayer1username + ":", obfuscated2emoji + obfuscated3emoji + obfuscated1emoji, true);
            event.editMessageEmbeds(gbkembd.build()).queue();
        }
        else {
            //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
            if((gbkplayer1choice==1&&(gbkplayer2choice==3||gbkcomputerchoice==3))
                    ||(gbkplayer1choice==2&&(gbkplayer2choice==1||gbkcomputerchoice==1))
                    ||(gbkplayer1choice==3&&(gbkplayer2choice==2||gbkcomputerchoice==2))) {
                if (gbkplayer2username!=null&&!gbkplayer2username.isEmpty()) {
                    gbkembd.setColor(0xff00b6);
                    gbkembd.setDescription("Kamu bermain dengan " + gbkplayer2username + "\nHasil:");
                    gbkembd.getFields().clear();
                    gbkembd.addField(gbkplayer2username + ":", gbkplaceholder2lambda(), true);
                    gbkembd.setFooter(gbkplayer1username + " Menang!");
                } else {
                    gbkembd.setColor(0xff00f9)
                            .setDescription("Kamu bermain dengan Computer.\nHasil:");
                    gbkembd.getFields().clear();
                    gbkembd.addField("Computer:",gbkplaceholdercomputerlambda(),true)
                            .setFooter("MENANG!");
                }
                gbkembd.addField(gbkplayer1username + ":", gbkplaceholder1lambda(), true);
                event.editMessageEmbeds(gbkembd.build()).setComponents().queue();
                clearallgbk();
            }
            else if((gbkplayer1choice==1&&(gbkplayer2choice==2||gbkcomputerchoice==2))
                    ||(gbkplayer1choice==2&&(gbkplayer2choice==3||gbkcomputerchoice==3))
                    ||(gbkplayer1choice==3&&(gbkplayer2choice==1||gbkcomputerchoice==1))) {
                if (gbkplayer2username!=null&&!gbkplayer2username.isEmpty()) {
                    gbkembd.setColor(0xff00b6)
                            .setDescription("Kamu bermain dengan " + gbkplayer2username + "\nHasil:");
                    gbkembd.getFields().clear();
                    gbkembd.addField(gbkplayer2username + ":", gbkplaceholder2lambda(), true)
                            .setFooter(gbkplayer2username + " Menang!");
                } else {
                    gbkembd.setColor(0xff0e70)
                            .setDescription("Kamu bermain dengan Computer.\nHasil:");
                    gbkembd.getFields().clear();
                    gbkembd.addField("Computer:",gbkplaceholdercomputerlambda(),true)
                            .setFooter("KALAH!");
                }
                gbkembd.addField(gbkplayer1username + ":", gbkplaceholder1lambda(), true);
                event.editMessageEmbeds(gbkembd.build()).setComponents().queue();
                clearallgbk();
            }else{
                if (!gbkplayer2username.isEmpty()) {
                    gbkembd.setDescription("Kamu bermain dengan " + gbkplayer2username + "\nHasil:");
                    gbkembd.getFields().clear();
                    gbkembd.addField(gbkplayer2username + ":", gbkplaceholder2lambda(), true);
                    gbkembd.setFooter("SERI!");
                } else {
                    gbkembd.setDescription("Kamu bermain dengan Computer.\nHasil:");
                    gbkembd.getFields().clear();
                    gbkembd.addField("Computer:",gbkplaceholdercomputerlambda(),true);
                    gbkembd.setFooter("SERI!");
                }
                gbkembd.setColor(0xb47ca6);
                gbkembd.addField(gbkplayer1username + ":", gbkplaceholder1lambda(), true);
                event.editMessageEmbeds(gbkembd.build()).setComponents().queue();
                clearallgbk();
            }
        }
    }

}
