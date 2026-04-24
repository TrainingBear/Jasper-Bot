package Japser.ListenerEvent;

import Japser.FeatureData.BlackjackData;
import Japser.FeatureData.ChessData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static Japser.Feature.Blackjack.*;
import static Japser.Feature.Chess.chessremovegamelists;
import static Japser.Feature.Rockpaperscissor.*;
import static Japser.VariableList.*;

public class ButtonInteraction extends ListenerAdapter {
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        String messageid = event.getMessageId();
        String channelid = event.getChannelId();
        User userwhoclick = event.getUser();
        //||---------------------------------------------- BLACKJACK BUTTON SHIT -------------------------------------------||
        switch (buttonId){
            case "BJAcc" ->{
                String gameid;
                try{
                    gameid = bjmessagechannelidlists.get(channelid).get(messageid);
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                BlackjackData BlackjackData = blackjackgameidlists.get(gameid);
                if (!"accpending".equals(BlackjackData.getplayer(userwhoclick.getId()).getstatus())
                        ||BlackjackData.getplayer(userwhoclick.getId()).getstatus()==null) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                BlackjackData.getplayer(userwhoclick.getId()).changestatus("idle"); //ngubah status ke idle
                HashMap<String, Japser.FeatureData.BlackjackData.Blackjackplayerinfo> playerlist = BlackjackData.getPlayerlist();
                StringBuilder infogame = new StringBuilder().append("Menunggu jawaban user...\n");
                for(var Playerinfo : playerlist.entrySet()){
                    String status = Playerinfo.getValue().getstatus();
                    infogame.append(getusernicknamefromguild(Playerinfo.getKey()))
                            .append(": ").append("idle".equals(status) ? "✅\n" : "decline".equals(status) ? "❌\n" : "...\n");
                }
                boolean allAccepting = playerlist.values().stream().allMatch(player -> {
                    String status = player.getstatus();
                    return "idle".equals(status) || "decline".equals(status);
                });
                if (!allAccepting) {
                    EmbedBuilder bjembd = new EmbedBuilder()
                            .setColor(0xff00b6)
                            .setTitle("BlackJack")
                            .setDescription(infogame.toString())
                            .setFooter("Pending...");
                    blackjackgameidlists.get(gameid).putMessageembd(bjembd);
                    event.editMessageEmbeds(bjembd.build()).queue();
                    break;
                }
                playerlist.entrySet().removeIf(entry -> {
                    boolean isDeclined = "decline".equals(entry.getValue().getstatus());
                    if (isDeclined) bjplayerinfo.remove(entry.getKey());
                    return isDeclined;
                });
                /////////////
                playerlist.forEach((key, value) -> BlackjackData.getplayer(key).changestatus("playing")); //ngubah status > playing
                EmbedBuilder blackjackembd = new EmbedBuilder()
                        .setColor(0xff00b6)
                        .setTitle("BlackJack")
                        .setDescription(infogame)
                        .setFooter("Ingame...");
                bjcheckstatus(event,gameid,playerlist,blackjackembd);
            } //FOR BLACKJACK
            case "BJDeny" ->{
                String gameid;
                try{
                    gameid = bjmessagechannelidlists.get(channelid).get(messageid);
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                BlackjackData BlackjackData = blackjackgameidlists.get(gameid);
                if (!"accpending".equals(BlackjackData.getplayer(userwhoclick.getId()).getstatus())
                        ||BlackjackData.getplayer(userwhoclick.getId()).getstatus()==null) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                BlackjackData.getplayer(userwhoclick.getId()).changestatus("decline"); //ngubah status ke decline
                HashMap<String, Japser.FeatureData.BlackjackData.Blackjackplayerinfo> playerlist = BlackjackData.getPlayerlist();
                StringBuilder infogame = new StringBuilder().append("Menunggu jawaban user...\n");
                for(var Playerinfo : playerlist.entrySet()){
                    String status = Playerinfo.getValue().getstatus();
                    infogame.append(getusernicknamefromguild(Playerinfo.getKey()))
                            .append(": ").append("idle".equals(status) ? "✅\n" : "decline".equals(status) ? "❌\n" : "...\n");
                }
                boolean allAccepting = playerlist.values().stream().allMatch(player -> {
                    String status = player.getstatus();
                    return "idle".equals(status) || "decline".equals(status);
                });
                if (!allAccepting) {
                    EmbedBuilder bjembd = new EmbedBuilder()
                            .setColor(0xff00b6)
                            .setTitle("BlackJack")
                            .setDescription(infogame.toString())
                            .setFooter("Pending...");
                    blackjackgameidlists.get(gameid).putMessageembd(bjembd);
                    event.editMessageEmbeds(bjembd.build()).queue();
                    break;
                }
                playerlist.keySet().removeIf(key -> "decline".equals(playerlist.get(key).getstatus()) && bjplayerinfo.remove(key) != null); //hapus yang nolak
                System.out.println(playerlist);
                if(playerlist.size()==1){
                    String remainingplayer = getusernicknamefromguild(playerlist.entrySet().iterator().next().getKey());
                    EmbedBuilder bjembd = blackjackgameidlists.get(gameid).getMessageembd();
                    bjembd.setColor(0xab00ff).setDescription("Sepertinya ga ada yang mau main sama "+remainingplayer+" :'(").setFooter("");
                    event.editMessageEmbeds(bjembd.build()).setComponents().queue(msg ->
                            msg.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
                    bjremovegamelists(gameid);
                }
                else{
                    playerlist.forEach((key, value) -> BlackjackData.getplayer(key).changestatus("playing")); //ngubah status > playing
                    EmbedBuilder blackjackembd = new EmbedBuilder()
                            .setColor(0xff00b6)
                            .setTitle("BlackJack")
                            .setDescription(infogame)
                            .setFooter("Ingame...");
                    bjcheckstatus(event,gameid,playerlist,blackjackembd);
                }
            }
            case "BJCheckCard" ->{
                String gameid;
                try{
                    gameid = bjmessagechannelidlists.get(channelid).get(messageid);
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                String status = blackjackgameidlists.get(gameid).getplayer(userwhoclick.getId()).getstatus();
                if (!"playing".equals(status) && !"completed".equals(status)
                        && !"bust".equals(status)) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                var cards = (ArrayList<String>) blackjackgameidlists.get(gameid).getplayer(userwhoclick.getId()).getcardlist();
                int totalcard = cards.stream()
                        .mapToInt(card -> switch (card) {
                            case "A1" -> 1;
                            case "A11" -> 11;
                            case "J", "K", "Q" -> 10;
                            default -> Integer.parseInt(card);
                        }).sum();
                event.reply("Kartu kamu [`"+totalcard+"`]: "+String.join("+", cards)).setEphemeral(true).queue();
            }
            case "BJHit" ->{
                String gameid;
                Map<String, BlackjackData.Blackjackplayerinfo> players;
                try{
                    gameid = bjmessagechannelidlists.get(channelid).get(messageid);
                    players = blackjackgameidlists.get(gameid).getPlayerlist();
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                if (!"playing".equals(players.get(userwhoclick.getId()).getstatus())||players.get(userwhoclick.getId()).getstatus()==null) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                if(players.containsKey("computer")){
                    List<String> cards = players.get("computer").getcardlist();
                    int totalcard = cards.stream()
                            .mapToInt(card -> switch (card) {
                                case "A1" -> 1;
                                case "A11" -> 11;
                                case "J", "K", "Q" -> 10;
                                default -> Integer.parseInt(card);
                            }).sum();
                    while(totalcard<17){
                        bjpickcard(cards);
                        for(String infocard : cards){
                            totalcard += switch (infocard) {
                                case "A1" -> 1;
                                case "A11" -> 11;
                                case "J", "K", "Q" -> 10;
                                default -> Integer.parseInt(infocard);
                            };
                        }
                    }
                    players.get("computer").changestatus("completed");
                }
                List<String> cards = players.get(userwhoclick.getId()).getcardlist();
                EmbedBuilder bjembd = blackjackgameidlists.get(gameid).getMessageembd();
                bjpickcard(cards);
                bjcheckstatus(event, gameid, players, bjembd);
            }
            case "BJStand" ->{
                String gameid;
                Map<String, BlackjackData.Blackjackplayerinfo> players;
                try{
                    gameid = bjmessagechannelidlists.get(channelid).get(messageid);
                    players = blackjackgameidlists.get(gameid).getPlayerlist();
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                if (!"playing".equals(players.get(userwhoclick.getId()).getstatus())||players.get(userwhoclick.getId()).getstatus()==null) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                if(players.containsKey("computer")){
                    List<String> cards = players.get("computer").getcardlist();
                    int totalcard = cards.stream()
                            .mapToInt(card -> switch (card) {
                                case "A1" -> 1;
                                case "A11" -> 11;
                                case "J", "K", "Q" -> 10;
                                default -> Integer.parseInt(card);
                            }).sum();
                    while(totalcard<17){
                        bjpickcard(cards);
                        for(String infocard : cards){
                            totalcard += switch (infocard) {
                                case "A1" -> 1;
                                case "A11" -> 11;
                                case "J", "K", "Q" -> 10;
                                default -> Integer.parseInt(infocard);
                            };
                        }
                    }
                    players.get("computer").changestatus("completed");
                }
                players.get(userwhoclick.getId()).changestatus("completed");
                EmbedBuilder bjembd = blackjackgameidlists.get(gameid).getMessageembd();
                bjcheckstatus(event, gameid, players, bjembd);
            }
            case "GBKaccept" ->{
                if(userwhoclick.getId().equals(gbkplayerid2)){
                    GBKtimer.cancel();
                    gbkembd.setColor(0xff00b6);
                    gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
                    gbkembd.setDescription("Kamu bermain dengan "+gbkplayer2username+".\nMemilih...");
                    gbkembd.setFooter("");
                    gbkembd.getFields().clear();
                    gbkembd.addField(gbkplayer2username+":", obfuscated1emoji + obfuscated2emoji + obfuscated3emoji, true);
                    gbkembd.addField(gbkplayer1username + ":", obfuscated2emoji + obfuscated3emoji + obfuscated1emoji, true);
                    event.editMessageEmbeds(gbkembd.build()).setComponents(ActionRow.of(
                            Button.secondary("GBKbatu", "🗿")
                            , Button.secondary("GBKgunting", "✂️")
                            , Button.secondary("GBKkertas", "📃"))).queue(message -> {
                        GBKtimer = new Timer();
                        GBKtimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                gbkembd.setDescription("Kamu bermain dengan "+gbkplayer2username+"\nHasil:");
                                gbkembd.getFields().clear();
                                gbkembd.addField(gbkplayer2username+":", gbkplaceholder2lambda(), true);
                                gbkembd.addField(gbkplayer1username + ":", gbkplaceholder1lambda(), true);
                                gbkembd.setFooter("Waktu Habis!");
                                message.editOriginalEmbeds(gbkembd.build()).setComponents().queue();
                                GBKtimer.cancel();clearallgbk();
                            }
                        }, 15000); // 15 detik
                    });
                }
                else if (userwhoclick.getId().equals(gbkplayerid1)){
                    event.reply("Kamu tidak bisa memencet ini, hanya yang kamu request yang bisa").setEphemeral(true).queue();
                }
                else{
                    event.reply("Bukan kamu yang main").setEphemeral(true).queue();
                }
            }  //FOR ROCK PAPER SCISSORS
            case "GBKdecline"->{
                if(userwhoclick.getId().equals(gbkplayerid2)){
                    GBKtimer.cancel();
                    gbkembd.setColor(0xff0089);
                    gbkembd.setTitle("Batu🗿 Gunting✂️ Kertas📃");
                    gbkembd.setDescription("<@"+gbkplayerid2+"> menolak... how sad ;l");
                    gbkembd.setFooter("");
                    event.editMessageEmbeds(gbkembd.build()).setComponents().queue(message-> message.deleteOriginal().queueAfter(10, TimeUnit.SECONDS));
                    clearallgbk();
                }
                else if (userwhoclick.getId().equals(gbkplayerid1)){
                    event.reply("Kamu tidak bisa memencet ini, hanya yang kamu request yang bisa").setEphemeral(true).queue();
                }
                else{
                    event.reply("Bukan kamu yang main").setEphemeral(true).queue();
                }
            }
            case "GBKbatu" ->{
                if(userwhoclick.getId().equals(gbkplayerid1)){
                    gbkplayer1choice = 1;
                }else if(userwhoclick.getId().equals(gbkplayerid2)){
                    gbkplayer2choice = 1;
                }
                gbkcheckstatus(event);
            }
            case "GBKkertas" ->{
                if(userwhoclick.getId().equals(gbkplayerid1)){
                    gbkplayer1choice = 2;
                }else if(userwhoclick.getId().equals(gbkplayerid2)){
                    gbkplayer2choice = 2;
                }
                gbkcheckstatus(event);
            }
            case "GBKgunting" ->{
                if(userwhoclick.getId().equals(gbkplayerid1)){
                    gbkplayer1choice = 3;
                }else if(userwhoclick.getId().equals(gbkplayerid2)){
                    gbkplayer2choice = 3;
                }
                gbkcheckstatus(event);
            }
            case "ChessAcc" ->{
                String gameid;
                try{
                    gameid = chessmessagechannelidlists.get(channelid).get(messageid);
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                ChessData ChessData = chessgameidlists.get(gameid);
                if(!"accpending".equals(ChessData.getplayer(userwhoclick.getId()).getStatus())) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                ChessData.getPlayerlist().forEach((entry, player) -> player.setStatus("playing"));

                Japser.FeatureData.ChessData.start();
                EmbedBuilder chessembd = ChessData.getMessageembd().setColor(0xff00b6)
                        .setDescription(ChessData.convertInfoToString()).setFooter("");
                ChessData.getPlayerlist().values().forEach(entry ->
                        chessembd.addField(entry.getUsername(), entry.convertPlayerInfoToString(), false)
                );
                List<Button> buttons = new ArrayList<>();
                buttons.add(Button.secondary("ChessDraw", "Seri"));
                buttons.add(Button.danger("ChessResign", "Menyerah"));
                ChessData.changeMessageEmbd(chessembd);
                event.editMessageEmbeds(chessembd.build()).setComponents(ActionRow.of(buttons)).queue();
            } //FOR CHESSSSSSSSSSSSSSSS
            case "ChessDeny" ->{
                String gameid;
                try{
                    gameid = chessmessagechannelidlists.get(channelid).get(messageid);
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                ChessData ChessData = chessgameidlists.get(gameid);
                if(!"accpending".equals(ChessData.getplayer(userwhoclick.getId()).getStatus())) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                EmbedBuilder chessembd = ChessData.getMessageembd().setColor(0xab00ff).setDescription(event.getUser().getName()+" menolak ;(").setFooter("");
                event.editMessageEmbeds(chessembd.build()).setComponents().queue(msg ->
                        msg.deleteOriginal().queueAfter(8, TimeUnit.SECONDS));
                chessremovegamelists(gameid);
            }
            case "ChessResign"->{
                String gameid;
                try{
                    gameid = chessmessagechannelidlists.get(channelid).get(messageid);
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                ChessData ChessData = chessgameidlists.get(gameid);
                if(!ChessData.getPlayerlist().containsKey(userwhoclick.getId())) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                EmbedBuilder chessembd = new EmbedBuilder().setFooter(ChessData.getplayer(userwhoclick.getId()).getUsername()+" Resign️").setColor(0xab00ff);
                chessembd.setDescription(ChessData.convertInfoToString());

                ChessData.getplayer(userwhoclick.getId()).setUsername(ChessData.getplayer(userwhoclick.getId()).getUsername()+" Resign️🏳️");
                ChessData.getPlayerlist().values().forEach(entry ->
                        chessembd.addField(entry.getUsername(), entry.convertPlayerInfoToString(), false)
                );
                event.editMessageEmbeds(chessembd.build()).setComponents().queue();
                chessremovegamelists(gameid);
            }
            case "ChessDraw" ->{
                String gameid;
                try{
                    gameid = chessmessagechannelidlists.get(channelid).get(messageid);
                }
                catch (NullPointerException e){
                    somethingwentwrong(event);
                    break;
                }
                ChessData ChessData = chessgameidlists.get(gameid);
                if(!ChessData.getPlayerlist().containsKey(userwhoclick.getId())) {
                    event.reply("Kamu tidak bisa berinteraksi dengan tombol ini...").setEphemeral(true).queue();
                    break;
                }
                ChessData.getplayer(userwhoclick.getId()).setUsername(ChessData.getplayer(userwhoclick.getId()).getUsername()+" Offer Draw");
                ChessData.getplayer(userwhoclick.getId()).setStatus("draw");
                boolean alldraw = ChessData.getPlayerlist().values().stream().allMatch(player -> "draw".equals(player.getStatus()));
                if(alldraw){
                    EmbedBuilder chessembd = new EmbedBuilder().setFooter("Semuanya setuju untuk Draw!").setColor(0xff0089);
                    chessembd.setDescription(ChessData.convertInfoToString());
                    ChessData.getPlayerlist().values().forEach(entry ->
                            chessembd.addField(entry.getUsername(), entry.convertPlayerInfoToString(), false)
                    );
                    event.editMessageEmbeds(chessembd.build()).setComponents().queue();
                    chessremovegamelists(gameid);
                }
                else{
                    EmbedBuilder chessembd = new EmbedBuilder().setDescription(ChessData.convertInfoToString()).setColor(0xff00b6);
                    ChessData.getPlayerlist().values().forEach(entry ->
                            chessembd.addField(entry.getUsername(), entry.convertPlayerInfoToString(), false)
                    );
                    event.editMessageEmbeds(chessembd.build()).queue();
                }
            }
        }
        //||----------------------------------------------------------------------------------------------------------------||
    }
    private void somethingwentwrong(ButtonInteractionEvent event){
        event.reply("Something went wrong...\nKemungkinan ada terjadi sesuatu dengan bot atau permainan tidak ditemukan").setEphemeral(true).queue();
        event.getMessage().delete().queue();
    }
}
