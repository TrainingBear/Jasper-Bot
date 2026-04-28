package jasper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jasper.feature.FeatureInterface;
import jasper.feature.Help;
import jasper.feature.Math;
import kotlin.Pair;
import kotlin.Triple;

public final class Main extends ListenerAdapter {
    final public static String OBFUSCATED_EMOJI_1 = "<a:obfuscated1:1345042468558602281>",
            OBFUSCATED_EMOJI_2 = "<a:obfuscated2:1345042572266962984>",
            OBFUSCATED_EMOJI_3 = "<a:obfuscated3:1345042449294295090>",
            GUILD_ID = "1238981507192717402",
            LOG_CHANNEL_ID = "1497904514663972935";

    public static Guild guild;
    public static TextChannel logChannel;
    public static Random random = new Random();
    @NotNull
    public static List<Pair<List<String>, FeatureInterface>> messageCommandMapping = new ArrayList<>();
    @NotNull
    public static HashMap<String, FeatureInterface> commandMapping = new HashMap<>();

    public static void main(String[] args) throws LoginException {
        System.out.println("[Log] Starting Bot...");
        JDA jda = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(null)
                // .setActivity(Activity.customStatus("/jhelp ♦️ ⛏"))
                .build();
        jda.addEventListener(new Main());
        System.out.println("[Log] Finished Starting Bot...");
    }

    public static String getUserNickname(@NotNull String userid) {
        Member member = guild.retrieveMemberById(userid).complete();
        return (member != null) ? member.getUser().getName() : "*???*";
    }

    public static void sendLog(@Nullable String log) {
        if (logChannel != null) {
            logChannel.sendMessage("[Log] " + log).queue();
            System.out.println("[Log] " + log);
        } else
            System.err.println("[Log](replacement, channel is null): " + log);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("[Log] Start of ready...");
        guild = event.getJDA().getGuildById(GUILD_ID);
        if (guild == null)
            throw new NullPointerException("Guild is null, id: " + GUILD_ID);
        logChannel = guild.getTextChannelById(LOG_CHANNEL_ID);
        if (logChannel == null)
            sendLog("Log channel is null, logs will not be displayed!!!");

        FeatureInterface[] toInserts = {
                new Math(), new Help()
        };
        for (FeatureInterface i : toInserts) {
            Triple<Pair<String, String>, OptionData, List<String>> infos = i.commandInsert();
            guild.upsertCommand(infos.getFirst().getFirst(), infos.getFirst().getSecond())
                    .addOptions(infos.getSecond()).queue();
            commandMapping.put(infos.getFirst().getFirst(), i);

            if (infos.getThird() != null)
                messageCommandMapping.add(new Pair<List<String>, FeatureInterface>(infos.getThird(), i));
            System.out.println(
                    "[Log] Inserting " + (infos.getThird() != null ? "message command and " : "")
                            + "command handler of " + infos.getFirst().getFirst());
        }
        System.out.println("[Log] Finished Start of ready...");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        FeatureInterface fI = commandMapping.get(event.getName());
        if (fI != null) {
            fI.handleCommand(event);
            sendLog(event.getUser().getName() + " in " + event.getChannel().getName() + " executing: "
                    + event.getFullCommandName());
        } else
            sendLog("Command not found! " + event.getName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final String rawMessage = event.getMessage().getContentRaw().toLowerCase();
        final boolean startWithEqual = rawMessage.startsWith("="); // exclusive command suggested by kujatic :moyai:
        if (event.getAuthor().isBot() || !event.isFromGuild() || (!rawMessage.startsWith("!") && !startWithEqual))
            return;

        final String[] argsRaw = rawMessage.split("\\s+");
        if (startWithEqual && !argsRaw[0].equals("="))
            return;
        final String prefix = startWithEqual ? argsRaw[0] : argsRaw[0].substring(1);
        if (prefix.isEmpty())
            return;

        for (Pair<List<String>, FeatureInterface> i : messageCommandMapping) {
            if (!i.getFirst().contains(prefix))
                continue;
            String[] args = new String[argsRaw.length - 1];
            System.arraycopy(argsRaw, 1, args, 0, argsRaw.length - 1);
            i.getSecond().handleCommandMessage(event, args);
            sendLog(event.getAuthor().getName() + " in " + event.getChannel().getName() + " sending: " + rawMessage);
            return;
        }
    }
}