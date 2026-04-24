package jasper.Feature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class Help {
    public static void helpcommand(MessageReceivedEvent event){
        helpembd(event);
    }
    public static void helpcommand(SlashCommandInteractionEvent event){
        helpembd(event);
    }
    //
    public static void helpcommand(MessageReceivedEvent event, String helptype){
        helpembd(event,helptype);
    }
    public static void helpcommand(SlashCommandInteractionEvent event,String helptype){
        helpembd(event,helptype);
    }

    public static void helpembd(MessageReceivedEvent event){
        EmbedBuilder helpembd= new EmbedBuilder();
        helpmessagesembd(helpembd);
        event.getMessage().replyEmbeds(helpembd.build()).queue();
    }
    public static void helpembd(SlashCommandInteractionEvent event){
        EmbedBuilder helpembd= new EmbedBuilder();
        helpmessagesembd(helpembd);
        event.replyEmbeds(helpembd.build()).queue();
    }
    //                                                                          \\
    public static void helpembd(MessageReceivedEvent event, String helptype){
        EmbedBuilder helpembd= new EmbedBuilder();
        helpmessagesembd(helpembd,helptype);
        event.getMessage().replyEmbeds(helpembd.build()).queue();
    }
    public static void helpembd(SlashCommandInteractionEvent event,String helptype) {
        EmbedBuilder helpembd = new EmbedBuilder();
        helpmessagesembd(helpembd, helptype);
        event.replyEmbeds(helpembd.build()).queue();
    }
    //
    private static void helpmessagesembd(EmbedBuilder helpembd){
        helpembd.setColor(0xff00b6);
        helpembd.setTitle("Jasper⛏Project Command list");
        helpembd.setDescription("""
                command yang lainnya ntar di tambahin sama developer
                ⁉️ **!jhelp [command]**
                Mendapatkan list/keterangan command
                🔢 **!math _atau_ =**
                Kalkulator matematika
                🪙 **!coinflip [head/tail]**
                Melempar koin & mendarat diantara head/tail
                🎲 **!diceroll [1-6]**
                Melempar dadu & menunjukkan angka diantara 1-6
                🃏 **!blackjack**
                Minigame blackjack
                ☁️ **!cuaca**
                Info cuaca berdasarkan kota
                ♟️ **!chess**
                Coming soon, in development
                """);
        helpembd.setFooter("Jangan spam command!");
    }
    private static void helpmessagesembd(EmbedBuilder helpembd,String helptype){
        helpembd.setTitle("Jasper⛏Project Help Command "+helptype);
        helpembd.setColor(0xff00b6);
        switch (helptype) {
            case "!jhelp","!jh","jhelp","jh" -> {
                helpembd.setDescription("⁉️ Untuk mengetahui list command");
                helpembd.addField("!jhelp [command]","Untuk mengetahui tentang command tersebut lebih lanjut",false);
            }
            case "!math", "math", "=" -> {
                helpembd.setDescription("""
                    🔢 Kalkulator matematika
                    **List/daftar operator:**
                    [ `+` ] Penjumlahan **\\|||** [ `-` ] Pengurangan
                    [ `*` ] Perkalian **\\|||** [ `/` ] Pembagian
                    [ `;` ] Modulus/sisa **\\|||** [ `(\uD835\uDC5B)` ] Tanda kurung
                    [ `(a^(b))` ] Perpangkatan
                    [ `\uD835\uDC5B!` ] Pemfaktorial
                    [ `sqrt(\uD835\uDC5B)` ] Akar kuadrat
                    [ `cbrt(\uD835\uDC5B)` ] Akar kubik
                    [ `\uD835\uDC5B%` ] Persentase  [ `pi`/`π` ] Pi (3.14≈)
                    [ `e` ] e *konstanta* (2.71≈)
                    **Suffix:** *satuan*
                    [ \uD835\uDC5B`k` ] seribu **\\|||** [ \uD835\uDC5B`jt` ] sejuta
                    [ \uD835\uDC5B`m` ] semilyar **\\|||** [ \uD835\uDC5B`t` ] setriliun
                    """);
                helpembd.setFooter("""
                    Beberapa operator tidak ada karena terlalu kompleks
                    untuk fungsi persentase [ \uD835\uDC5B% ] kemungkinan akan mengalami error
                    """);}
            case "!coinflip", "!cf", "coinflip", "cf" ->
                helpembd.setDescription("""
                    🪙 Melempar **coin**(virtual) lalu mendarat antara __Tail__(angka) dan __Head__(Kepala/gambar)
                    - Jika ingin menebak, tambahkan spasi lalu 'head'/'h' atau 'tail'/'t'
                    """);
            case "!diceroll", "!dr", "diceroll", "dr" -> helpembd.setDescription("""
                    🎲 Melempar **dadu**(virtual) lalu menunjukkan angka antara __1__ *sampai* __6__
                    - Jika ingin menebak, tambahkan spasi lalu pilih 1-6
                    """);
            case "!blackjack","blackjack","!bj","bj" ->
                helpembd.setDescription("""
                    🃏 **Permainan kartu/blackjack**
                     bertujuan jumlah nominal kartu **tidak** melebihi dari __21__ dan **melebihi** dari dealer
                    berikut cara bermainnya:
                    **Awal bermain**
                    **Dealer** akan membagikan masing-masing 2 kartu, **dealer** akan __membuka__ satu kartunya
                    **[Hit]**
                    **Dealer** akan __memberikan__ **kamu** satu kartu kartu Ace akan menjadi 1 jika melebihi 21
                    **[Stand]**
                    **Kamu** __diam__/__tidak mengambil kartu__, **dealer** akan mengambil kartu untuk dirinya bila perlu
                    """);
            case "!batuguntingkertas","!gbk","!guntingbatukertas","!suit","batuguntingkertas","gbk","guntingbatukertas","suit"-> {
                helpembd.setDescription("""
                    **Minigame batu-gunting-kertas**
                    Maksimal hanya __2__ **user**, jika **user** _menerima_ kurang dari __18__ detik
                    **Akhir permainan:**
                    """);
                helpembd.addField("Menang","Jika **kamu** 🗿**/**✂️**/**📃 **lawan** ✂️**/**📃**/**🗿",false);
                helpembd.addField("Kalah","Jika **kamu** 🗿**/**✂️**/**📃 **lawan**  ️📃**/**🗿**/**✂️",false);
                helpembd.addField("Seri","Jika **kamu** dan **lawan** memilih yang _sama_",false);
            }
            case "!cuaca","!weather","!c","!w","cuaca","weather","c","w" -> {
                helpembd.setDescription("""
                    ☁️ **Info Cuaca**
                    Mengetahui cuaca berdasarkan kota yang diinput
                    input kota menggunakan bahasa inggris
                    """);
                helpembd.setFooter("data-data berdasarkan dari openweatherapi");
            }
            case "!chess","chess","!ch","ch"-> helpembd.setDescription("""
                    ♟️ **Catur**
                    Siapa yang gatau catur? game papan yang berukuran 8x8
                    
                    **cara menggerakkan bidak:**
                    **"**m __[bidak]__ __[koordinat bidak]__ __[koordinat tujuan]__**"**
                    **"**m __[bidak]__ __[koordinat tujuan]__**"**
                    **"**m __[longcastle , shortcastle]__**"**
                    **"**p __[bidak untuk pion promosi]__**"**
                    """);
            default -> {
                helpembd.setColor(0xab00ff);
                helpembd.setDescription("Jenis command tersebut tidak valid");
                helpembd.setFooter("atau jika kamu merasa bug silahkan lapor ke server ini!");
            }
        }
    }
}
