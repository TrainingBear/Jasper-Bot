package jasper.feature;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface FeatureInterface {
    @NotNull
    CommandInfoContainer commandInsert();

    void handleCommand(@NotNull SlashCommandInteractionEvent event);

    void handleCommandMessage(@NotNull MessageReceivedEvent event, @NotNull final String[] args);

    static final class CommandInfoContainer {
        public final String commandPrefix, commandDesc;
        public final OptionData optionData;
        public final List<String> msgCommandAliases;

        CommandInfoContainer(String commandPrefix, String commandDesc, OptionData optionData, @Nullable List<String> msgAliases) {
            this.commandPrefix = commandPrefix;
            this.commandDesc = commandDesc;
            this.optionData = optionData;
            this.msgCommandAliases = msgAliases;
        }
    }
}
