package jasper.Feature;

import jasper.FeatureData.ChessData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static jasper.VariableList.*;

public class Chess {
    public static void chesscommand(MessageReceivedEvent event, String inputuser){
        String chessgamemasterid = event.getAuthor().getId();
        String[] commandlength = inputuser.split("\\s+");
        if (chessplayerinfo.containsKey(chessgamemasterid)) {
            chesssenderrormessg(event, "Kamu sedang bermain di game lain...");
            return;
        }
        else if (commandlength.length>3){
            chesssenderrormessg(event, "Command tidak valid");
            return;
        }
        else if(chessgameidlists.size()>=3){ //>=n n= total slot game
            chesssenderrormessg(event, "Slot game penuh");
            return;
        }
        for(int i=0; i <= chessgameidlists.size();i++){
            String gameid = String.valueOf(i);
            if(!chessgameidlists.containsKey(gameid)){
                ChessData chessData = new ChessData();
                chessData.inputplayer(chessgamemasterid);
                chessplayerinfo.put(chessgamemasterid,gameid);
                Pattern patternuser = Pattern.compile("<@(\\d+)>");
                var matcheruser = patternuser.matcher(inputuser);
                if (matcheruser.find()) {
                    String anotherUserId = matcheruser.group(1);
                    if (chessplayerinfo.containsKey(anotherUserId) || anotherUserId.equals(chessgamemasterid)
                            || getusernicknamefromguild(anotherUserId).equals("*???*")) {
                        chesssenderrormessg(event, anotherUserId.equals(chessgamemasterid) ? "Jangan masukkan dirimu sendiri"
                                : getusernicknamefromguild(anotherUserId).equals("*???*") ? "Masukkan user yang terdapat di server ini"
                                : "User **" + getusernicknamefromguild(anotherUserId) + "** sedang bermain di game lain");
                        chessremovegamelists(gameid);
                        return;
                    }
                    chessData.inputplayer(anotherUserId, "accpending");
                    chessplayerinfo.put(anotherUserId, gameid);
                    if (matcheruser.find()) {
                        chesssenderrormessg(event, "Tolong input maksimal 1 user");
                        chessremovegamelists(gameid);
                        return;
                    }
                } else {
                    chesssenderrormessg(event, "Input user, tidak bisa sendiri");
                    chessremovegamelists(gameid);
                    return;
                }
                Pattern patterntimer = Pattern.compile("(?:timer|t|waktu|w):(\\d+)",Pattern.CASE_INSENSITIVE);
                var matchertimer = patterntimer.matcher(inputuser);
                if(matchertimer.find()){
                    try {
                        short num = Short.parseShort(matchertimer.group(1));
                        if (num > 900) {
                            chesssenderrormessg(event, "Waktu kebanyakan, max 900 detik (15 menit)");
                            chessremovegamelists(gameid);
                            return;
                        }
                        chessData.setTimer(num);
                    }catch(NumberFormatException e){
                        chesssenderrormessg(event, "Waktu kebanyakan, max 900 detik (15 menit)");
                        chessremovegamelists(gameid);
                        return;
                    }
                    if(matchertimer.find()){
                        chesssenderrormessg(event, "Input satu timer saja");
                        chessremovegamelists(gameid);
                        return;
                    }
                }
                chesstimerrunning=false; //reset timer
                chessstarttimer();
                chessembd(event,chessData,gameid);
                return;
            }
        }
    }

    private static void chessembd(MessageReceivedEvent event, ChessData chessData, String gameid){
        StringBuilder playerlist = new StringBuilder();
        for (var playerEntry : chessData.getPlayerlist().entrySet()){
            playerlist.append(playerEntry.getValue().getUsername()).append(" ")
                    .append("idle".equals(playerEntry.getValue().getStatus()) ?" ✅":" ...").append("\n");
        }
        EmbedBuilder chessembd = new EmbedBuilder().setColor(0xff00b6)
                .setTitle("♟️ Chess")
                .setFooter("Pending...")
                .setDescription("Menunggu jawaban user...\ndengan waktu permainan: "+chessData.getTimerDisplay()+"\n"
                        +playerlist);
        Message sendmssg = event.getChannel().sendMessageEmbeds(chessembd.build()).complete();
        String chesschnlid = sendmssg.getChannelId();
        String chessmsgid = sendmssg.getId();

        chessData.putmessageinfo(gameid,chesschnlid,chessmsgid,chessembd);
        chessmessagechannelidlists.computeIfAbsent(chesschnlid, k -> new HashMap<>()).put(chessmsgid, gameid);
        chessgameidlists.put(gameid, chessData);

        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("ChessAcc", "Terima"));
        buttons.add(Button.danger("ChessDeny", "Tolak"));
        sendmssg.editMessageEmbeds(chessembd.build()).setComponents(ActionRow.of(buttons)).queueAfter(2,TimeUnit.SECONDS);
    }

    public static void chessremovegamelists(String gameid) {
        try{
            var players = (Map<String, ChessData.ChessPlayerInfo>) chessgameidlists.get(gameid).getPlayerlist();
            ChessData info = chessgameidlists.get(gameid);
            if(info!=null) chessmessagechannelidlists.get(info.getChannelid()).remove(info.getMessageid()); //ngapus
            players.keySet().forEach(chessplayerinfo::remove);
            chessgameidlists.remove(gameid);
        }catch(NullPointerException ignored){}
    }

    private static void chesssenderrormessg(MessageReceivedEvent event, String message) {
        EmbedBuilder chesserrorembd = new EmbedBuilder().setColor(0xab00ff).setTitle("ERROR! Chess").setDescription(message);
        event.getMessage().replyEmbeds(chesserrorembd.build()).queue(msg -> msg.delete().queueAfter(8, TimeUnit.SECONDS));
        event.getMessage().delete().queueAfter(8,TimeUnit.SECONDS);
    }
    public static void chesssenderrormessg(MessageReceivedEvent event, String message,byte timer) {
        EmbedBuilder chesserrorembd = new EmbedBuilder().setColor(0xab00ff).setTitle("ERROR! Chess").setDescription(message);
        event.getMessage().replyEmbeds(chesserrorembd.build()).queue(msg -> msg.delete().queueAfter(timer, TimeUnit.SECONDS));
        event.getMessage().delete().queueAfter(timer,TimeUnit.SECONDS);
    }
    private static void chessstarttimer(){
        new Thread(()->{
            chesstimerrunning=true;
            while(chesstimerrunning){
                try{
                    if (chessgameidlists.isEmpty()) {
                        Thread.sleep(1000); // Tunggu 1 detik klo g ad gem
                        continue;
                    }
                    for(var Entry : chessgameidlists.entrySet()){
                        String j = Entry.getKey();
                        ChessData ChessData = Entry.getValue();
                        TextChannel channel = guild.getTextChannelById(ChessData.getChannelid());
                        if (channel == null) {
                            System.out.println("Channel tidak ditemukan.");
                            return;
                        }
                        if(ChessData.getGameStatus().equals("waiting")){
                            ChessData.setAccTimer((byte) (ChessData.getAccTimer()-1));
                            if(ChessData.getAccTimer() < 1){
                                String userdidntacc = "";
                                for(var entry : ChessData.getPlayerlist().entrySet()){
                                    if("accpending".equals(entry.getValue().getStatus())) userdidntacc=entry.getValue().getUsername();
                                }
                                EmbedBuilder chessembd = Entry.getValue().getMessageembd().setDescription(userdidntacc+" Tidak menjawab ;(")
                                        .setFooter("Waktu Habis").setColor(0xab5291);
                                channel.retrieveMessageById(ChessData.getMessageid())
                                        .queue(msg -> msg.editMessageEmbeds(chessembd.build()).setComponents().queue()
                                                , e -> System.out.println("BJ ERROR " + e.getMessage())
                                        );//below this until end of method are removing data
                                chessremovegamelists(j);
                            }
                        }else if(ChessData.getGameStatus().equals("playing")){
                            EmbedBuilder chssembd = ChessData.getMessageembd().clearFields();
                            chssembd.clearFields();
                            ChessData.getplayer(ChessData.getWhoturnid())
                                    .setTimer((short) (ChessData.getplayer(ChessData.getWhoturnid()).getTimer()-1));

                            if(ChessData.getplayer(ChessData.getWhoturnid()).getTimer() < 1){
                                chssembd.setFooter(ChessData.getplayer(ChessData.getWhoturnid()).getUsername()+" Kehabisan waktu!").setColor(0xab5291);
                                for(var entry : ChessData.getPlayerlist().entrySet()){
                                    chssembd.addField(entry.getValue().getUsername()
                                            ,entry.getValue().convertPlayerInfoToString(),false);
                                }
                                channel.retrieveMessageById(ChessData.getMessageid())
                                        .queue(msg -> msg.editMessageEmbeds(chssembd.build()).setComponents().queue()
                                                , e -> System.out.println("BJ ERROR " + e.getMessage())
                                        );
                                chessremovegamelists(j);
                            }
                            else if(ChessData.getplayer(ChessData.getWhoturnid()).getTimer() < 7){
                                for(var entry : ChessData.getPlayerlist().entrySet()){
                                    chssembd.addField(entry.getValue().getUsername()
                                            ,entry.getValue().convertPlayerInfoToString(),false);
                                }
                                channel.retrieveMessageById(ChessData.getMessageid())
                                        .queue(msg -> msg.editMessageEmbeds(chssembd.build()).queue()
                                                , e -> System.out.println("BJ ERROR " + e.getMessage())
                                        );
                            }
                            else if(ChessData.getplayer(ChessData.getWhoturnid()).getTimer()%10==0 ){
                                for(var entry : ChessData.getPlayerlist().entrySet()){
                                    chssembd.addField(entry.getValue().getUsername()
                                            ,entry.getValue().convertPlayerInfoToString(),false);
                                }
                                channel.retrieveMessageById(ChessData.getMessageid())
                                        .queue(msg -> msg.editMessageEmbeds(chssembd.build()).queue()
                                                , e -> System.out.println("BJ ERROR " + e.getMessage())
                                        );
                            }
                        }
                    }
                    Thread.sleep(1000); // Tunggu 1 detik before loop
                } catch (ConcurrentModificationException ignored){}
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        },"ChessTimerThread").start();
    }

    public static void chessmove(MessageReceivedEvent event, String inputuser, String userid){
        String[] commandlength = inputuser.split("\\s+");
        if(chessplayerinfo.containsKey(event.getAuthor().getId())) {
            if (commandlength.length > 4) {
                chesssenderrormessg(event, "Command tidak valid", (byte) 5);
                return;
            }
            ChessData chessData;
            try{
                chessData = chessgameidlists.get(chessplayerinfo.get(userid));
            }catch(NullPointerException e){
                e.getStackTrace();
                return;
            }

            if (userid.equals(chessData.getWhoturnid())) {
                String piece = pieceidentifier(commandlength[1]);
                if (piece.isEmpty()) {
                    chesssenderrormessg(event, "Masukkan bidak yang valid", (byte) 5);
                    return;
                }

                switch (commandlength.length) {
                    case 4 -> {
                        String from = commandlength[2].toLowerCase();
                        String to = commandlength[3].toLowerCase();
                        if (commandlength[2].length() > 2 || commandlength[3].length() > 2 ||
                                !from.matches(".*[1-8].*") || !from.matches(".*[a-h].*") ||
                                !to.matches(".*[1-8].*") || !to.matches(".*[a-h].*")) {
                            chesssenderrormessg(event, "Masukkan letak bidak yang valid", (byte) 5);
                            return;
                        }
                        if (commandlength[2].equalsIgnoreCase(commandlength[3])) {
                            chesssenderrormessg(event, "Jangan masukkan letak yang sama", (byte) 5);
                            return;
                        }
                        chessData.move(piece, from, to, event);
                    }
                    case 2 -> chessData.move(piece, "", "", event);
                    default -> {
                        String coordinate = commandlength[2].toLowerCase();
                        if (commandlength[2].length() > 2 ||
                                !coordinate.matches(".*[1-8].*") || !coordinate.matches(".*[a-h].*")) {
                            chesssenderrormessg(event, "Masukkan letak bidak yang valid", (byte) 5);
                            return;
                        }
                        chessData.move(piece, "", coordinate, event);
                    }
                }
            }
            else chesssenderrormessg(event, "bukan giliran kamu", (byte) 3);
        }  else event.getMessage().delete().queue();
    }
    public static void pawnpromote(MessageReceivedEvent event, String inputuser, String userid){
        String[] commandlength = inputuser.split("\\s+");
        if(chessplayerinfo.containsKey(event.getAuthor().getId())) {
            ChessData chessData;
            try{
                chessData = chessgameidlists.get(chessplayerinfo.get(userid));
            }catch(NullPointerException e){
                e.getStackTrace();
                return;
            }
            if (userid.equals(chessData.getWhoturnid())) {
                String piece = pieceidentifier(commandlength[1]);
                switch(piece) {
                    case "", "pawn", "longcastle", "shortcastle", "king" -> {
                        chesssenderrormessg(event, "Masukkan nama bidak yang valid untuk promosi pion", (byte) 5);
                        return;
                    }
                }
                chessData.move(piece, "", "", event);
            }
        } else event.getMessage().delete().queue();
    }

    private static String pieceidentifier(String piecename){
        switch (piecename.toLowerCase()) {
            case "p", "pawn", "pion" -> {
                return "pawn";
            }
            case "kn", "knight", "kuda" -> {
                return "knight";
            }
            case "b", "bishop", "gajah" -> {
                return "bishop";
            }
            case "r", "rook", "benteng" -> {
                return "rook";
            }
            case "q", "queen", "ratu" -> {
                return "queen";
            }
            case "ki","king","raja" -> {
                return "king";
            }
            case "lcastle","longcastle","lc" ->{
                return "longcastle";
            }
            case "scastle","shortcastle","sc"->{
                return "shortcastle";
            }
            default -> {
                return "";
            }
        }
    }
}
