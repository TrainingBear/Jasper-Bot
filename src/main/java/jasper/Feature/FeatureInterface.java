package jasper.feature;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface FeatureInterface {
    /**
     * 
     * @return {@link CommandInfoContainer} that holds command prefix, short
     *         description, option datas, and command message
     */
    @NotNull
    CommandInfoContainer commandInsert();

    /**
     * function that handle slash command specific feature user demand
     * 
     * @param event {@link SlashCommandInteractionEvent}
     */
    void handleCommand(@NotNull SlashCommandInteractionEvent event);

    /**
     * function that handle message command specific feature user demand
     * 
     * @param event {@link SlashCommandInteractionEvent}
     */
    void handleCommandMessage(@NotNull MessageReceivedEvent event, @NotNull final String[] args);

    static final class CommandInfoContainer {
        public final String commandPrefix, commandDesc;
        public final OptionData[] optionData;
        public final List<String> msgCommandAliases;

        CommandInfoContainer(String commandPrefix, String commandDesc,
                @Nullable List<String> msgAliases, OptionData... optionData) {
            this.commandPrefix = commandPrefix;
            this.commandDesc = commandDesc;
            this.optionData = optionData;
            this.msgCommandAliases = msgAliases;
        }
    }
}
