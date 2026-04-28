package jasper.feature;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import kotlin.Pair;
import kotlin.Triple;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface FeatureInterface {
    /**
     * Triple <{Command prefix, Command description}, Command option data, Message
     * commands>
     * 
     * @return
     */
    @NotNull
    Triple<Pair<String, String>, OptionData, List<String>> commandInsert();

    void handleCommand(@NotNull SlashCommandInteractionEvent event);

    void handleCommandMessage(@NotNull MessageReceivedEvent event, @NotNull final String[] args);
}
