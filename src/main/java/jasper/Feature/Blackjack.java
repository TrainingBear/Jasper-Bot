package jasper.Feature;

import jasper.FeatureData.BlackjackData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static jasper.VariableList.*;

public class Blackjack {
    public static void blackjackcommand(MessageReceivedEvent event, String inputuser) {
        String bjgamemaster = event.getAuthor().getId();
        if(blackjackgameidlists.size()>=5){ // >=n n= total slot game
            bjsenderrormessg(event, "Slot game penuh");
            return;
        }else if (bjplayerinfo.containsKey(bjgamemaster)) { //klo lu di gem laen
            bjsenderrormessg(event, "Kamu sedang bermain di game lain...");
            return;
        }
        //nyari game id
        Pattern pattern = Pattern.compile("<@(\\d+)>");
        for(int i=0;i<=blackjackgameidlists.size();i++){
            String gameid = String.valueOf(i);
            if(!blackjackgameidlists.containsKey(gameid)) {
                BlackjackData BlackjackData = new BlackjackData();
                BlackjackData.inputplayer(bjgamemaster);
                bjplayerinfo.put(bjgamemaster, gameid);
                var matcher = pattern.matcher(inputuser);
                byte mcount = 0;
                while (matcher.find()) {
                    if (++mcount > 3) {
                        bjsenderrormessg(event, "Tolong input maksimal 3 user");
                        bjremovegamelists(gameid);
                        return;
                    }
                    String anotherUserId = matcher.group(1);
                    if (bjplayerinfo.containsKey(anotherUserId) || anotherUserId.equals(bjgamemaster)
                            || getusernicknamefromguild(anotherUserId).equals("*???*")) {
                        bjsenderrormessg(event, anotherUserId.equals(bjgamemaster) ? "Jangan masukkan dirimu sendiri"
                                : bjplayerinfo.containsKey(anotherUserId) ? "Jangan masukkan 2 user yang sama"
                                : getusernicknamefromguild(anotherUserId).equals("*???*") ? "Masukkan user yang terdapat di server ini"
                                : "User **" + getusernicknamefromguild(anotherUserId) + "** sedang bermain di game lain");
                        bjremovegamelists(gameid);
                        return;
                    }
                    BlackjackData.setTimer(80);
                    BlackjackData.inputplayer(anotherUserId, "accpending");
                    bjplayerinfo.put(anotherUserId, gameid);
                }
                if (BlackjackData.getplayersize() == 1) {
                    BlackjackData.inputplayer("computer", "accpending");
                }
                bjtimerrunning=false; //reset timer
                bjstarttimerstart();
                blackjackembd(event,BlackjackData,gameid);
                return;
            }
        }
    }
    private static void blackjackembd(MessageReceivedEvent event, BlackjackData BlackjackData, String gameid) {
        StringBuilder infogame = new StringBuilder();
        String bjchnlid;
        String bjmsgid;
        if(BlackjackData.getPlayerlist().containsKey("computer")){
            for (var playerEntry : BlackjackData.getPlayerlist().entrySet()) { //get value from gameid -> **player** from bjgameidlist
                String playername= playerEntry.getKey();
                BlackjackData.getplayer(playername).changestatus("playing"); //ngubah status > playing
                List<String> cardlist = BlackjackData.getplayer(playername).getcardlist();
                int totalcard = cardlist.stream()
                        .mapToInt(card -> switch (card) {
                            case "A1" -> 1;
                            case "A11" -> 11;
                            case "J", "K", "Q" -> 10;
                            default -> Integer.parseInt(card);
                        }).sum();
                String firstcardforcomputer="";
                if("computer".equals(playerEntry.getKey())) { //ngubah first card ke angka yg sesuei
                    firstcardforcomputer= switch (cardlist.getFirst()) {
                        case "A1","A11" -> "A";
                        case "J", "K", "Q" -> "10";
                        default -> cardlist.getFirst();
                    };
                }
                infogame.append("computer".equals(playerEntry.getKey()) ? "computer" : getusernicknamefromguild(playerEntry.getKey()))
                        .append(" ").append("[`").append("computer".equals(playerEntry.getKey()) ? firstcardforcomputer+"+?" : totalcard).append("`]\n")
                        .append("computer".equals(playerEntry.getKey()) ? (cardlist.getFirst().equals("A11") ? "A"
                                : cardlist.getFirst().equals("A11") ? "A" : cardlist.getFirst()) +"+?" : String.join("+", cardlist)).append("\n");
            }
            EmbedBuilder blackjackembd = new EmbedBuilder()
                    .setColor(0xff00b6)
                    .setTitle("BlackJack")
                    .setDescription(infogame)
                    .setFooter("Ingame...");
            Message sendmssg = event.getMessage().replyEmbeds(blackjackembd.build()).addActionRow(
                    Button.danger("BJHit", "Hit")
                    ,Button.primary("BJStand", "Stand")).complete();
            bjchnlid = sendmssg.getChannelId();
            bjmsgid = sendmssg.getId();
            BlackjackData.putmessageinfo(bjchnlid,bjmsgid,blackjackembd);
        }else{
            infogame.append("Menunggu jawaban user...\n");
            for (var playerEntry : BlackjackData.getPlayerlist().entrySet()) {
                String playerinfo = BlackjackData.getplayer(playerEntry.getKey()).getstatus();
                infogame.append(getusernicknamefromguild(playerEntry.getKey())).append(" : ")
                        .append(playerinfo.equals("idle") ? "✅\n" : "...\n");
            }
            EmbedBuilder blackjackembd = new EmbedBuilder()
                    .setColor(0xff00b6)
                    .setTitle("BlackJack")
                    .setDescription(infogame.toString())
                    .setFooter("Pending...");
            Message sendmssg = event.getChannel().sendMessageEmbeds(blackjackembd.build()).addActionRow(
                    Button.primary("BJAcc", "Terima")
                    ,Button.danger("BJDeny", "Tolak")).complete();
            bjchnlid = sendmssg.getChannelId();
            bjmsgid = sendmssg.getId();
            BlackjackData.putmessageinfo(bjchnlid,bjmsgid,blackjackembd);
        }
        bjmessagechannelidlists.computeIfAbsent(bjchnlid, k -> new HashMap<>()).put(bjmsgid, gameid);
        blackjackgameidlists.put(gameid,BlackjackData);
    }
    private static void bjsenderrormessg(MessageReceivedEvent event, String message) {
        EmbedBuilder bjerrorembd = new EmbedBuilder().setColor(0xab00ff).setTitle("ERROR! Blackjack").setDescription(message);
        event.getMessage().replyEmbeds(bjerrorembd.build()).queue(msg -> msg.delete().queueAfter(8, TimeUnit.SECONDS));
        event.getMessage().delete().queueAfter(8,TimeUnit.SECONDS);
    }
    private static void bjstarttimerstart() {
        new Thread(() -> {
            bjtimerrunning=true;
            while (bjtimerrunning) {
                try {
                    if (blackjackgameidlists.isEmpty()) {
                        Thread.sleep(1000); // Tunggu 1 detik klo g ad gem
                        continue;
                    }
                    for (var gameEntry : blackjackgameidlists.entrySet()) {
                        String j = gameEntry.getKey();
                        BlackjackData BlackjackData = gameEntry.getValue();
                        if (BlackjackData.getTimer() > 0) {
                            BlackjackData.setTimer(BlackjackData.getTimer() - 1);
                        }
                        if (BlackjackData.getTimer() == 1) {
                            EmbedBuilder bjembd = blackjackgameidlists.get(j).getMessageembd().setFooter("Waktu Habis").setColor(0xab5291);
                            StringBuilder infogame = new StringBuilder();
                            boolean allidling = BlackjackData.getPlayerlist().values().stream()
                                    .allMatch(player -> {
                                        String status = player.getstatus();
                                        return "idle".equals(status) || "playing".equals(status) || "completed".equals(status);
                                    });
                            if(allidling) {
                                for (var playerEntry : BlackjackData.getPlayerlist().entrySet()) { //get value from gameid -> **player** from bjgameidlist
                                    var cards = (ArrayList<String>) playerEntry.getValue().getcardlist();
                                    int totalcard = cards.stream()
                                            .mapToInt(card -> switch (card) {
                                                case "A1" -> 1;
                                                case "A11" -> 11;
                                                case "J", "K", "Q" -> 10;
                                                default -> Integer.parseInt(card);
                                            }).sum();
                                    infogame.append("computer".equals(playerEntry.getKey()) ? "computer" : getusernicknamefromguild(playerEntry.getKey()))
                                            .append(" ").append("[`").append(totalcard).append("`]\n").append(String.join("+", cards)).append("\n");
                                }
                                bjembd.setDescription(infogame);
                            }
                            TextChannel channel = guild.getTextChannelById(BlackjackData.getChannelid());
                            if (channel == null) {
                                System.out.println("Channel tidak ditemukan.");
                                return;
                            }
                            channel.retrieveMessageById(BlackjackData.getMessageid())
                                    .queue(msg -> msg.editMessageEmbeds(bjembd.build()).setComponents().queue()
                                            , e -> System.out.println("BJ ERROR " + e.getMessage())
                                    );//below this until end of method are removing data
                            bjremovegamelists(j);
                        }
                    }
                    Thread.sleep(1000); // Tunggu 1 detik before loop
                } catch (ConcurrentModificationException ignored){}
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "BJTimerThread").start();
    }
    public static void bjremovegamelists(String gameid) {
        var players = (Map<String, BlackjackData.Blackjackplayerinfo>) blackjackgameidlists.get(gameid).getPlayerlist();
        BlackjackData info = blackjackgameidlists.get(gameid);
        if (info != null) bjmessagechannelidlists.get(info.getChannelid()).remove(info.getMessageid()); //ngapus
        players.keySet().forEach(bjplayerinfo::remove);
        blackjackgameidlists.remove(gameid);
    }
    public static void bjpickcard(List<String> cardlist) {
        String[] facecard = {"K", "Q", "J"};
        int ratio = random.nextInt(1, 14);
        cardlist.add((ratio > 4) ? String.valueOf(random.nextInt(2, 11)) : (ratio > 1) ? facecard[random.nextInt(3)] : "A11");
        int totalcard = cardlist.stream()
                .mapToInt(card -> switch (card) {
                    case "A1" -> 1;
                    case "A11" -> 11;
                    case "J", "K", "Q" -> 10;
                    default -> Integer.parseInt(card);
                })
                .sum();
        if (totalcard > 21) {
            int index = cardlist.indexOf("A11");
            if (index != -1) cardlist.set(index, "A1");
        }
    }
    public static void bjcheckstatus(ButtonInteractionEvent event, String gameid, Map<String, BlackjackData.Blackjackplayerinfo> players, EmbedBuilder bjembd){
        StringBuilder infogame = new StringBuilder();
        for (var playerEntry : players.entrySet()) { //get value from gameid -> **player** from bjgameidlist
            var cards = (ArrayList<String>) playerEntry.getValue().getcardlist();
            String firstcard= switch (cards.getFirst()) { //ngubah first card ke angka yg sesuei
                case "A1","A11" -> "A";
                case "J", "K", "Q" -> "10";
                default -> cards.getFirst();
            };
            byte totalcard = (byte) cards.stream()
                    .mapToInt(card -> switch (card) {
                        case "A1" -> 1;
                        case "A11" -> 11;
                        case "J", "K", "Q" -> 10;
                        default -> Integer.parseInt(card);
                    }).sum();
            if(totalcard > 21){
                playerEntry.getValue().changestatus("bust");
            }
            infogame.append("computer".equals(playerEntry.getKey()) ? "computer" : getusernicknamefromguild(playerEntry.getKey()))
                    .append(("completed".equals(playerEntry.getValue().getstatus())||"bust".equals(playerEntry.getValue().getstatus())) ? "✅ " : " ")
                    .append("[`").append("computer".equals(playerEntry.getKey())||!players.containsKey("computer") ? firstcard+"+?" : totalcard).append("`]\n")
                    .append("computer".equals(playerEntry.getKey())||!players.containsKey("computer") ? cards.getFirst()+"+?" : String.join("+", cards)).append("\n");
        }
        boolean allcompleted = players.values().stream().allMatch(player -> {
            String status = player.getstatus();
            return "completed".equals(status)||"bust".equals(status);
        });
        if(!allcompleted) {
            bjembd.setDescription(infogame.toString());
            blackjackgameidlists.get(gameid).putMessageembd(bjembd);
            List<Button> buttons = new ArrayList<>();
            buttons.add(Button.danger("BJHit", "Hit"));
            buttons.add(Button.primary("BJStand", "Stand"));
            if (!players.containsKey("computer")) {
                buttons.add(Button.secondary("BJCheckCard", "My card"));
            }
            event.editMessageEmbeds(bjembd.build())
                    .setComponents(ActionRow.of(buttons))
                    .queue();
        }else{
            String winninginfo;
            boolean isallhascompletestatus = players.values().stream().allMatch(player ->{
                String status = player.getstatus();
                return "completed".equals(status)||"bust".equals(status);
            });
            if(isallhascompletestatus){
                ArrayList<String> winners = new ArrayList<>();
                byte maxTotal = 0;
                byte minCards = Byte.MAX_VALUE;
                for (var entry : players.entrySet()) {
                    if("bust".equals(entry.getValue().getstatus())) continue;
                    String playerid = entry.getKey();
                    List<String> cards =  entry.getValue().getcardlist();
                    byte totalcard = (byte) cards.stream()
                            .mapToInt(card -> switch (card) {
                                case "A1" -> 1;
                                case "A11" -> 11;
                                case "J", "K", "Q" -> 10;
                                default -> Integer.parseInt(card);
                            }).sum();
                    int cardCount = cards.size();
                    if (totalcard > maxTotal) {
                        maxTotal = totalcard;
                        minCards = (byte) cardCount;
                        winners.clear();
                        winners.add(playerid);
                    } else if (totalcard == maxTotal) {
                        if (cardCount < minCards) {
                            minCards = (byte) cardCount;
                            winners.clear();
                            winners.add(playerid);
                        } else if (cardCount == minCards) {
                            winners.add(playerid);
                        }
                    }
                }
                for(byte i =0;i<winners.size();i++){
                    winners.set(i, "computer".equals(winners.get(i))?"computer":getusernicknamefromguild(winners.get(i)));
                }
                if (winners.size() == 1) {
                    winninginfo = winners.getFirst()+" MENANG!";
                } else {
                    bjembd.setColor(0xd60099);
                    if(!players.containsKey("computer")){
                    winninginfo = "SERI antara "+String.join(", ",winners);
                    }else{
                        winninginfo = "SERI";
                    }
                }
            }
            else{
                bjembd.setColor(0xff0089);
                winninginfo = "Semuanya BUST!...";
            }
            infogame.setLength(0);
            for (var playerEntry : players.entrySet()) { //get value from gameid -> **player** from bjgameidlist
                List<String> cards = playerEntry.getValue().getcardlist();
                int totalcard = cards.stream()
                        .mapToInt(card -> switch (card) {
                            case "A1" -> 1;
                            case "A11" -> 11;
                            case "J", "K", "Q" -> 10;
                            default -> Integer.parseInt(card);
                        }).sum();
                infogame.append("computer".equals(playerEntry.getKey()) ? "computer" : getusernicknamefromguild(playerEntry.getKey()))
                        .append(" ").append("[`").append(totalcard).append("`]\n").append(String.join("+", cards)).append("\n");
            }
            bjembd.setDescription(infogame).setFooter(winninginfo);
            blackjackgameidlists.get(gameid).putMessageembd(bjembd);
            event.editMessageEmbeds(bjembd.build()).setComponents().queue();
            bjremovegamelists(gameid);
        }
    }
}