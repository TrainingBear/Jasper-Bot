package jasper.feature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jasper.featureData.ColorUtil;
import kotlin.Pair;
import kotlin.Triple;

public final class Help implements FeatureInterface {

    private static List<FeatureContainer> otherFeatures = new ArrayList<>();

    public static void inputFeature(@NotNull FeatureContainer featureInfos) {
        otherFeatures.add(featureInfos);
    }

    public static final class FeatureContainer {
        private String commandArgs, shortDesc;
        private List<String> prefixes, longDesc;

        public FeatureContainer(
                final List<String> prefixes,
                final String commandArgs,
                final String shortDesc,
                final List<String> longDesc) {
            this.prefixes = prefixes;
            this.commandArgs = commandArgs;
            this.shortDesc = shortDesc;
            this.longDesc = longDesc;
        }
    }

    @Override
    public @NotNull Triple<Pair<String, String>, OptionData, List<String>> commandInsert() {
        OptionData options = new OptionData(OptionType.STRING,
                "help_type", "Penjelasan command lebih lanjut", false);
        // TODO add options to other features
        return new Triple<Pair<String, String>, OptionData, List<String>>(
                new Pair<String, String>("jhelp", "Daftar `!` command"), options, List.of("jhelp"));
    }

    @Override
    public void handleCommand(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder embedMessage = ColorUtil.NORMAL.getEmbedMessage("Jasper⛏Project Command list")
                .setDescription("Testing");
        event.replyEmbeds(embedMessage.build()).queue();
    }

    @Override
    public void handleCommandMessage(@NotNull MessageReceivedEvent event, @NotNull final String[] args) {
        if (args.length > 2) {
            argumentNotValid(event.getMessage(), null);
            return;
        }
        final boolean isNoArgs = args.length == 0;
        int pageIndex, maxPage = 1;
        try {
            pageIndex = args.length == 2 ? Integer.parseInt(args[1]) - 1 : 0;
        } catch (NumberFormatException e) {
            argumentNotValid(event.getMessage(), null);
            return;
        }
        final String argsPrefix = isNoArgs ? "" : args[0];
        StringBuilder toShow = new StringBuilder();

        for (final FeatureContainer i : otherFeatures) {
            if (isNoArgs)
                toShow.append(i.commandArgs + '\n').append(i.shortDesc + '\n');
            else if (i.prefixes.contains(argsPrefix)) {
                toShow.append(
                        i.longDesc.get(pageIndex < (maxPage = i.longDesc.size())
                                ? (pageIndex < 1 ? (pageIndex = 0) : pageIndex)
                                : (pageIndex = i.longDesc.size() - 1)));
                break;
            }
        }
        if (!isNoArgs && toShow.isEmpty()) {
            argumentNotValid(event.getMessage(),
                    ColorUtil.ERROR.getEmbedMessage("Jasper⛏Project Command list ~~" + argsPrefix + "~~")
                            .setDescription("Tidak ada command \"" + argsPrefix +
                                    "\", lihat !jhelp untuk list command"));
            return;
        }
        event.getMessage().replyEmbeds(
                ColorUtil.NORMAL.getEmbedMessage("Jasper⛏Project Command list" + (isNoArgs ? "" : argsPrefix))
                        .setDescription(toShow).setFooter("Halaman " + (++pageIndex) + '/' + maxPage).build())
                .queue();
    }

    private static void argumentNotValid(@NotNull Message msg, @Nullable EmbedBuilder embed) {
        msg.replyEmbeds(embed.build()).queue(message -> message.delete()
                .queueAfter(8, TimeUnit.SECONDS));
    }

}
