package jasper.feature;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jasper.feature.Help.FeatureContainer;
import jasper.featureData.ColorUtil;
import kotlin.Pair;
import kotlin.Triple;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public final class Math implements FeatureInterface {

    @Override
    public Triple<Pair<String, String>, OptionData, List<String>> commandInsert() {
        OptionData options = new OptionData(OptionType.STRING,
                "input", "Masukkan input untuk dikalkulasi,", false);
        Help.inputFeature(new FeatureContainer(List.of("math", "="), "🔢 **!math** _atau_ **=**",
                "Kalkulator matematika",
                """
                        🔢 Kalkulator matematika
                        *Tidak support aljabar*
                        **List/daftar operator:**
                        [ `+` ] Penjumlahan [ `-` ] Pengurangan
                        [ `*` ] Perkalian [ `/` ] Pembagian
                        [ `;` ] Modulus/sisa
                        [ `(\uD835\uDC5B)` ] Tanda kurung
                        [ `(a^(b))` ] Perpangkatan [ `\uD835\uDC5B!` ] Pemfaktorial
                        [ `sqrt(\uD835\uDC5B)` ] Akar kuadrat
                        [ `cbrt(\uD835\uDC5B)` ] Akar kubik
                        [ `pi`/`π` ] Pi (3.14≈) [ `e` ] e *konstanta* (2.71≈)
                        [ `\uD835\uDC5B%` ] Persentase
                        **Suffix:** *satuan*
                        [ \uD835\uDC5B`k` ] seribu [ \uD835\uDC5B`jt` ] sejuta
                        [ \uD835\uDC5B`m` ] semilyar [ \uD835\uDC5B`t` ] setriliun
                        """));
        return new Triple<Pair<String, String>, OptionData, List<String>>(
                new Pair<String, String>("math", "Kalkulator matematika, tidak support aljabar"), options,
                List.of("math", "="));
    }

    @Override
    public void handleCommand(SlashCommandInteractionEvent event) {

    }

    @Override
    public void handleCommandMessage(MessageReceivedEvent event, String[] args) {
        event.getMessage().replyEmbeds(ColorUtil.ERROR.getEmbedMessage("Math Calculator")
                .setDescription("Code sedang diupdate, masih dalam tahap pengembangan").build())
                .queue(message -> message.delete().queueAfter(8, TimeUnit.SECONDS));
        ;
    }

}
