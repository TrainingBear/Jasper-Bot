package jasper;

import jasper.FeatureData.BlackjackData;
import jasper.FeatureData.ChessData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;

public class VariableList {
/*
    For whose developing this, these note may helpful
    color for error .setColor(0xab00ff)
    color for right .setColor(0xff00f9)
    color for false .setColor(0xff0089)
    color for default .setColor(0xff00b6)
    color for timeout .setColor(0xab5291)

 */



    //|| ======================== EMOJI ========================||
    final public static String obfuscated1emoji = "<a:obfuscated1:1345042468558602281>";
    final public static String obfuscated2emoji = "<a:obfuscated2:1345042572266962984>";
    final public static String obfuscated3emoji = "<a:obfuscated3:1345042449294295090>";
    //||************************ FOR BJ ***************************||
    public static HashMap<String, BlackjackData> blackjackgameidlists = new HashMap<>();
    public static HashMap<String, String> bjplayerinfo = new HashMap<>(); //INFO WHERE THE PLAYER PLAYS TO
    public static HashMap<String, Map<String, String>> bjmessagechannelidlists = new HashMap<>(); //INFO MESSAGE DATA
    public static volatile boolean bjtimerrunning = true;
    /*  STATUSES = idle,WAITING ALL PLAYERS FOR ACC
    *              accpending, WAITING INVITED PLAYER FOR ACC
    *              decline, THE PLAYER DECLINE TO PLAYING
    *              playing, PLAYING THE GAME
    *              completed, DONE WAITING FOR ALL PLAYER COMPLETED ALSO
    *              bust, LOSE THE GAME
    */
    //||************************************** FOR CHESS ***********************************||
    public static HashMap<String, ChessData> chessgameidlists = new HashMap<>();
    public static HashMap<String, String> chessplayerinfo = new HashMap<>();
    public static HashMap<String, Map<String, String>> chessmessagechannelidlists = new HashMap<>();
    public static volatile boolean chesstimerrunning = true;
    /*  STATUSES = idle,WAITING ALL PLAYERS FOR ACC
     *             accpending, WAITING INVITED PLAYER FOR ACC
     *             playing, PLAYING THE GAME
     */
    //||************************** FOR ROCK PAPER SCISSORS ************************||
    public static EmbedBuilder gbkembd = new EmbedBuilder();
    public static String gbkplayerid1, gbkplayerid2, gbkplayer2username, gbkplayer1username="";
    public static byte gbkplayer1choice,gbkplayer2choice =-1;
    public static boolean gbkstart= false;
    public static Timer GBKtimer = new Timer();
    public static byte gbkcomputerchoice =-1;
    //||****************************** FOR WEATHER CD ********************************||
    public static boolean weathercdboolean = false; //TRUE IF CD FALSE IF NOT




    public static LocalDateTime TIME = LocalDateTime.now();
    public static int YYEAR,MMONTH,DDAY,HHOUR,MMINUTE,SSECOND;
    public static String username;
    public static String displayname;
    public static String channelname;
    public final static String adminid= "781098203746795531";
    public static Guild guild;

    public static Random random = new Random();

    public static String getusernicknamefromguild(String userid){
        Member member = guild.retrieveMemberById(userid).complete();
        return (member != null) ? member.getUser().getName(): "*???*";
    }
}
