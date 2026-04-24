package jasper.Feature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

import static jasper.VariableList.random;

public class Coinflip {
    public static void coinflipcommand(MessageReceivedEvent event){
        int coinfacing= random.nextInt(1,3);
        String coinfacing2 = (coinfacing==1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2);
    }
    public static void coinflipcommand(SlashCommandInteractionEvent event){
        int coinfacing= random.nextInt(1,3);
        String coinfacing2 = (coinfacing==1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2);
    }
    //
    public static void coinflipguesscommand(String cfselect,MessageReceivedEvent event){
        int coinfacing = random.nextInt(1, 3);
        String coinfacing2 = (coinfacing == 1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2,cfselect);
    }
    public static void coinflipguesscommand(String cfselect,SlashCommandInteractionEvent event){
        int coinfacing = random.nextInt(1, 3);
        String coinfacing2 = (coinfacing == 1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2,cfselect);
    }

    public static void coinflipembd(MessageReceivedEvent event, String coinfacing2){
        int time = random.nextInt(4,7);
        String emoji = (coinfacing2.equals("Head"))?"<:coinhead:1345042710221820004>":"<:cointail:1329435269111484469>";
        EmbedBuilder cfembd = new EmbedBuilder();
        cfembd.setColor(0x9d0070);
        cfembd.setTitle("Coin sedang berputar... <a:coinspin:1329435247523401738>");
        cfembd.setDescription("Coin menghadap ...");
        event.getMessage().replyEmbeds(cfembd.build()).queue(sentmessage ->{
            cfembd.setColor(0xff00b6);
            cfembd.setTitle("Coin sudah berputar!");
            cfembd.setDescription("Coin menghadap **"+coinfacing2+"** "+emoji);
            sentmessage.editMessageEmbeds(cfembd.build()).queueAfter(time, TimeUnit.SECONDS);
        });
    }
    public static void coinflipembd(SlashCommandInteractionEvent event, String coinfacing2){
        int time = random.nextInt(4,7);
        String emoji = (coinfacing2.equals("Head"))?"<:coinhead:1345042710221820004>":"<:cointail:1329435269111484469>";
        EmbedBuilder cfembd = new EmbedBuilder();
        cfembd.setColor(0x9d0070);
        cfembd.setTitle("Coin sedang berputar... <a:coinspin:1329435247523401738>");
        cfembd.setDescription("Coin menghadap ...");
        event.replyEmbeds(cfembd.build()).queue(sentmessage ->{
            cfembd.setColor(0xff00b6);
            cfembd.setTitle("Coin sudah berputar!");
            cfembd.setDescription("Coin menghadap **"+coinfacing2+"** "+emoji);
            sentmessage.editOriginalEmbeds(cfembd.build()).queueAfter(time, TimeUnit.SECONDS);
        });
    }
    //
    public static void coinflipembd(MessageReceivedEvent event, String coinfacing2,String cfselect){
        int time = random.nextInt(4,7);
        String emoji = (coinfacing2.equals("Head"))?"<:coinhead:1345042710221820004>":"<:cointail:1329435269111484469>";
        EmbedBuilder cfgembd = new EmbedBuilder();
        if(cfselect.equals("Head")||cfselect.equals("Tail")){
            if(cfselect.equals(coinfacing2)){
                cfgembd.setColor(0x9d0070);
                cfgembd.setTitle("Coin sedang berputar... <a:coinspin:1329435247523401738>");
                cfgembd.setDescription("Coin menghadap ...");
                event.getMessage().replyEmbeds(cfgembd.build()).queue(sentmessage ->{
                    cfgembd.setColor(0xff00f9);
                    cfgembd.setTitle("Coin berhenti berputar!");
                    cfgembd.setDescription("**BENAR!** Coin menghadap **"+coinfacing2+"** "+emoji);
                    sentmessage.editMessageEmbeds(cfgembd.build()).queueAfter(time, TimeUnit.SECONDS);
                });
            }else {
                cfgembd.setColor(0x9d0070);
                cfgembd.setTitle("Coin sedang berputar... <a:coinspin:1329435247523401738>");
                cfgembd.setDescription("Coin menghadap ...");
                event.getMessage().replyEmbeds(cfgembd.build()).queue(sentmessage ->{
                    EmbedBuilder cfgembd2 = new EmbedBuilder();
                    cfgembd2.setColor(0xff0089);
                    cfgembd2.setTitle("Coin berhenti berputar!");
                    cfgembd2.setDescription("**KALAH!** Coin menghadap **"+coinfacing2+"** "+emoji);
                    sentmessage.editMessageEmbeds(cfgembd2.build()).queueAfter(time, TimeUnit.SECONDS);
                });
            }
        }else {
            cfgembd.setColor(0xab00ff);
            cfgembd.setTitle("Coinflip");
            cfgembd.setDescription("**Error!** Pilih 'Head' atau 'Tail'!");
            event.getMessage().replyEmbeds(cfgembd.build()).queue(message -> message.delete().queueAfter(8,TimeUnit.SECONDS));
        }
    }
    public static void coinflipembd(SlashCommandInteractionEvent event, String coinfacing2,String cfselect){
        int time = random.nextInt(4,7);
        String emoji = (coinfacing2.equals("Head"))?"<:coinhead:1345042710221820004>":"<:cointail:1329435269111484469>";
        EmbedBuilder cfgembd = new EmbedBuilder();
        if(cfselect.equals("Head")||cfselect.equals("Tail")){
            if(cfselect.equals(coinfacing2)){
                cfgembd.setColor(0x9d0070);
                cfgembd.setTitle("Coin sedang berputar... <a:coinspin:1329435247523401738>");
                cfgembd.setDescription("Coin menghadap ...");
                event.replyEmbeds(cfgembd.build()).queue(sentmessage ->{
                    cfgembd.setColor(0xff00f9);
                    cfgembd.setTitle("Coin berhenti berputar!");
                    cfgembd.setDescription("**BENAR!** Coin menghadap **"+coinfacing2+"** "+emoji);
                    sentmessage.editOriginalEmbeds(cfgembd.build()).queueAfter(time, TimeUnit.SECONDS);
                });
            }else {
                cfgembd.setColor(0x9d0070);
                cfgembd.setTitle("Coin sedang berputar... <a:coinspin:1329435247523401738>");
                cfgembd.setDescription("Coin menghadap ...");
                event.replyEmbeds(cfgembd.build()).queue(sentmessage ->{
                    EmbedBuilder cfgembd2 = new EmbedBuilder();
                    cfgembd2.setColor(0xff0089);
                    cfgembd2.setTitle("Coin berhenti berputar!");
                    cfgembd2.setDescription("**KALAH!** Coin menghadap **"+coinfacing2+"** "+emoji);
                    sentmessage.editOriginalEmbeds(cfgembd2.build()).queueAfter(time, TimeUnit.SECONDS);
                });
            }
        }else {
            cfgembd.setColor(0xab00ff);
            cfgembd.setTitle("Coinflip");
            cfgembd.setDescription("**Error!** Pilih 'Head' atau 'Tail'!");
            event.replyEmbeds(cfgembd.build()).queue(message -> message.deleteOriginal().queueAfter(8,TimeUnit.SECONDS));
        }
    }
}
