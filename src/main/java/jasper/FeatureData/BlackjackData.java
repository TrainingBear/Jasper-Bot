package jasper.FeatureData;

import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static jasper.VariableList.random;

public class BlackjackData {
    EmbedBuilder messageembd = new EmbedBuilder();
    String messageid,channelid;
    int timer=60;
    HashMap<String, Blackjackplayerinfo> playerlist = new HashMap<>();

    public Blackjackplayerinfo getplayer(String userid){
        return this.playerlist.get(userid);
    }
    public HashMap<String, Blackjackplayerinfo> getPlayerlist(){return this.playerlist;}
    public int getTimer(){return this.timer;}
    public int getplayersize(){
        return this.playerlist.size();
    }
    public EmbedBuilder getMessageembd(){return this.messageembd;}
    public String getMessageid(){return this.messageid;}
    public String getChannelid(){return  this.channelid;}

    public void inputplayer(String userid){
        playerlist.put(userid, new Blackjackplayerinfo("idle"));
        getplayer(userid).startingshuffle();
    }
    public void inputplayer(String userid, String status){
        playerlist.put(userid, new Blackjackplayerinfo(status));
        getplayer(userid).startingshuffle();
    }
    public void putmessageinfo(String channelid, String messageid, EmbedBuilder messageembd){
        this.channelid=channelid;
        this.messageid=messageid;
        this.messageembd=messageembd;
    }
    public void putMessageembd(EmbedBuilder messageembd){
        this.messageembd= messageembd;
    }

    public void setTimer(int i){
        this.timer=i;
    }

    public static class Blackjackplayerinfo{
        String status;
        List<String> card = new ArrayList<>();

        Blackjackplayerinfo(String status){
            this.status=status;
        }

        public void startingshuffle(){
            startingshufflecard(this.card);
        }
        public void changestatus(String status){
            this.status = status;
        }

        public List<String> getcardlist(){return this.card;}
        public String getstatus(){return this.status;}
        private static void startingshufflecard(List<String> cardlist){
            byte[] numcard = {2, 3, 4, 5, 6, 7, 8, 9, 10};
            String[] facecard = {"K", "Q", "J"};
            for (int i = 0; i < 2; i++) {
                int ratio = random.nextInt(1, 14);
                if (ratio > 4) {
                    cardlist.add(String.valueOf(numcard[random.nextInt(numcard.length)]));
                } else if (ratio > 1) {
                    cardlist.add(facecard[random.nextInt(facecard.length)]);
                } else {
                    cardlist.add("A11");
                }
            }
        }
    }
}
