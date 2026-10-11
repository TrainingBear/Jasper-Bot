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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.security.auth.login.LoginException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jasper.feature.FeatureInterface;
import jasper.feature.Help;
import jasper.Util.JsonConfig;
import kotlin.Pair;

public final class Main extends ListenerAdapter {
    final public static String OBFUSCATED_EMOJI_1 = "<a:obfuscated1:1345042468558602281>",
            OBFUSCATED_EMOJI_2 = "<a:obfuscated2:1345042572266962984>",
            OBFUSCATED_EMOJI_3 = "<a:obfuscated3:1345042449294295090>",
            GUILD_ID = "1238981507192717402",
            LOG_CHANNEL_ID = "1497904514663972935";

    public static Guild guild;
    public static TextChannel logChannel;
    public static Random random = new Random();

    public static JsonConfig jsCfg = new JsonConfig(null, null, true, true);

    public static byte ERROR_DELETE_TIME = 35;

    @NotNull
    public final static List<Pair<List<String>, FeatureInterface>> messageCommandMapping = new ArrayList<>();
    @NotNull
    public final static HashMap<String, FeatureInterface> commandMapping = new HashMap<>();

    public static void main(String[] args) throws LoginException {
        System.out.println("[Log] Starting Bot...");
        JDA jda = JDABuilder.createDefault(System.getenv("DISCORD_TOKEN"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setActivity(null)
                .setActivity(Activity.customStatus("/jhelp ♦️ ⛏"))
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
            System.err.println("[Log](channel is null): " + log);
    }

    /**
     * Format/strip trailing on double
     * <p>
     * Example:
     * 234.211 strip with 2 digits after . (followZero false): 234.21; <br>
     * or 5 digits followZero true: 234.21100
     * </p>
     * 
     * @return
     */
    public static String subDigit(final double d, int howMany, final boolean followZero) {
        String decimal = String.valueOf(d);
        int i = 0, dotIndex = decimal.length();
        for (; i < decimal.length(); i++) {
            final char c = decimal.charAt(i);
            if ((dotIndex = c == '.' ? i : dotIndex) == i || dotIndex == decimal.length())
                continue; // if(c=='.') dotIndex = i
            if (--howMany <= 0)
                break;
        }
        final boolean decimalBlank = dotIndex == decimal.length() - 2 && decimal.charAt(decimal.length() - 1) == '0';
        if (decimalBlank)
            howMany++;
        decimal = howMany <= -1 ? decimal.substring(0, decimal.length() - 1)
                : decimal.substring(0, decimalBlank ? decimal.length() - 2
                        : ++i >= decimal.length() ? decimal.length() : i);
        if (followZero && dotIndex != decimal.length() && howMany >= 1)
            for (; howMany > 0; howMany--)
                decimal += '0';
        return decimal;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("[Log] Start of ready...");
        final long startTime = System.currentTimeMillis();

        guild = event.getJDA().getGuildById(GUILD_ID);
        if (guild == null)
            throw new NullPointerException("Guild is null, id: " + GUILD_ID);
        logChannel = guild.getTextChannelById(LOG_CHANNEL_ID);
        if (logChannel == null)
            sendLog("Log channel is null, logs will not be displayed!!!");

        ArrayList<FeatureInterface> toInserts = new ArrayList<>();
        FeatureInterface helpFeature = null;

        try (JarFile jar = new JarFile(new File(Main.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()))) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries/* JarEntry */.nextElement().getName();
                if (!name.startsWith("jasper/feature/") || !name.endsWith(".class") || name.contains("$"))
                    continue;

                final String className = name.replace('/', '.').replace(".class", "");
                final Class<?> clazz = Class.forName(className);
                if (clazz.isInterface())
                    continue;

                final FeatureInterface fI = (FeatureInterface) clazz.getDeclaredConstructor().newInstance();
                if (fI instanceof Help help)
                    helpFeature = help;
                else
                    toInserts.add(fI);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (helpFeature != null)
            toInserts.add(helpFeature);

        byte featureCount = 0;
        for (FeatureInterface i : toInserts) {
            final FeatureInterface.CommandInfoContainer infos = i.commandInsert();
            if (infos.commandDesc.charAt(0) == '^') {
                System.out.println("[Log] Skipping: " + infos.commandPrefix + ". Cause:\n    " + infos.commandDesc.substring(1));
                continue;
            }
            guild.upsertCommand(infos.commandPrefix, infos.commandDesc)
                    .addOptions(infos.optionData).queue();
            commandMapping.put(infos.commandPrefix, i);

            if (infos.msgCommandAliases != null)
                messageCommandMapping.add(new Pair<List<String>, FeatureInterface>(infos.msgCommandAliases, i));
            featureCount++;
            System.out.println(
                    "[Log] Inserting " + (infos.msgCommandAliases != null ? "message command and " : "")
                            + "command handler of " + infos.commandPrefix);
        }
        final long tookTimeMS = (System.currentTimeMillis() - startTime);
        sendLog("Finished Start of ready. Registered `" + featureCount + "` feature(s), took: "
                + tookTimeMS + "ms...");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        final FeatureInterface fI = commandMapping.get(event.getName());
        if (fI != null) {
            fI.handleSlashCommand(event);

            StringBuilder sb = new StringBuilder().append(event.getName() + ' ');
            for (OptionMapping option : event.getOptions()) // for logs purpose
                sb.append(option.getName()).append(": ").append(option.getAsString()).append(" ");
            sendLog(event.getUser().getName() + " in " + event.getChannel().getName() + " executing: " + sb.toString());

        } else
            sendLog("Command not found! " + event.getName());
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final String rawMessage = event.getMessage().getContentRaw().toLowerCase();
        final boolean startWithEqual = rawMessage.startsWith("="); // exclusive command suggested by kujatic :moyai:
        final String cmdTrigger = FeatureInterface.commandTriggerStr;
        if (event.getAuthor().isBot() || !event.isFromGuild()
                || (!rawMessage.startsWith(cmdTrigger) && !startWithEqual))
            return;

        final List<String> argsRaw = new ArrayList<>(Arrays.asList(rawMessage.split("\\s+")));
        if (startWithEqual && argsRaw.getFirst().length() != 1) {
            argsRaw.set(0, argsRaw.getFirst().substring(1));
            argsRaw.add(0, "=");
        }
        final String prefix = startWithEqual ? argsRaw.getFirst() : argsRaw.getFirst().substring(1);
        if (prefix.isEmpty())
            return;

        for (Pair<List<String>, FeatureInterface> i : messageCommandMapping) {
            if (!i.getFirst().contains(prefix))
                continue;
            String[] args = new String[argsRaw.size() - 1]; // removing prefix, to fresh just only arg's
            System.arraycopy(argsRaw.toArray(String[]::new), 1, args, 0, argsRaw.size() - 1);

            sendLog(event.getAuthor().getName() + " in " + event.getChannel().getName() + " sending: " + rawMessage);
            i.getSecond().handleMessageCommand(event, args);
            return;
        }
    }
}