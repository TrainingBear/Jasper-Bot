package jasper.feature;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface FeatureInterface {
    public static String commandTriggerStr = "!";

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
    void handleSlashCommand(@NotNull SlashCommandInteractionEvent event);

    /**
     * function that handle message command specific feature user demand
     * 
     * @param event {@link SlashCommandInteractionEvent}
     */
    void handleMessageCommand(@NotNull MessageReceivedEvent event, @NotNull final String[] args);

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

        /**
         * Another option as placeholder for soon implementation feature
         * 
         * @param commandPrefix      Command prefix indicates name of it
         * @param unimplementMessage Reason why this feature not implemented
         */
        CommandInfoContainer(@NotNull String commandPrefix, @NotNull String unimplementMessage) {
            this.commandPrefix = commandPrefix;
            this.commandDesc = '^' + unimplementMessage;
            this.optionData = null;
            this.msgCommandAliases = null;
        }
    }
}
