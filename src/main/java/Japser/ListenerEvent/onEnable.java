package Japser.ListenerEvent;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import static Japser.VariableList.guild;

public class onEnable extends ListenerAdapter {
    public void onReady(@NotNull ReadyEvent event){
        guild = event.getJDA().getGuildById("1238981507192717402");
        if (guild != null) {
            guild.upsertCommand("jhelp", "Daftar `!` command").addOptions(
                    new OptionData(OptionType.STRING,"help_type","Penjelasan command lebih lanjut",false)
                            .addChoice("math","math")
                            .addChoice("coinflip","coinflip")
                            .addChoice("diceroll","diceroll")
                            .addChoice("blackjack","blackjack")
                            .addChoice("batuguntingkertas","batuguntingkertas")
                            .addChoice("cuaca","cuaca")
            ).queue();
            guild.upsertCommand("math","Kalkulator matematika, jika apps tidak merespon berarti ada bug, report").addOptions(
                    new OptionData(OptionType.STRING,"mathinput","Masukkan argument matematika",true)
            ).queue();
            guild.upsertCommand("coinflip","Putar koin").addOptions(
                    new OptionData(OptionType.STRING,"guess","Tebakan",false)
                            .addChoice("Head","Head")
                            .addChoice("Tail","Tail")
            ).queue();
            guild.upsertCommand("diceroll","Putar dadu").addOptions(
                    new OptionData(OptionType.INTEGER,"guess","Tebakan, pilih 1-6").setMinValue(1).setMaxValue(6)
            ).queue();
            guild.upsertCommand("batuguntingkertas","Minigame batu gunting kertas").addOptions(
                    new OptionData(OptionType.USER,"user","Bermain dengan user lain",false),
                    new OptionData(OptionType.STRING,"pilih","Langsung pilih",false).
                            addChoice("Batu","batu")
                            .addChoice("Gunting","gunting")
                            .addChoice("Kertas","kertas")
            ).queue();
            guild.upsertCommand("cuaca","Cuaca dari kota").addOptions(
                    new OptionData(OptionType.STRING,"kota","input kota",true)
            ).queue();
            guild.upsertCommand("say","Hanya untuk dev/admin").addOptions(
                    new OptionData(OptionType.STRING,"say","say",true)
            ).queue();

        }
    }
    /*
    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("cuaca") && event.getFocusedOption().getName().equals("kota")) {
            List<Command.Choice> choices = citiesLIST.stream()
                    .filter(city -> city.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase()))
                    .limit(25)
                    .map(city -> new Command.Choice(city, city))
                    .toList();

            event.replyChoices(choices).queue();
        }
    }*/
}
