package Japser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Japser.Embedmessg.*;

public class Method extends Action {
    //|| ======================== EMOJI ========================||
    static String obfuscated1emoji = "<a:obfuscated1:1331198863331168308>";
    static String obfuscated2emoji = "<a:obfuscated2:1331198914811924530>";
    static String obfuscated3emoji = "<a:obfuscated3:1331198948353900645>";
    //||************************ FOR BJ ***************************||
    static EmbedBuilder bjembd = new EmbedBuilder();
    static ArrayList<String> dealercardpic = new ArrayList<>(), yourcardpic = new ArrayList<>();
    static ArrayList<Integer> yourcard = new ArrayList<>(), dealercard = new ArrayList<>();
    static StringBuilder yourcardpic2 = new StringBuilder(), dealercardpic2 = new StringBuilder();
    static String[]BjFaceCardSym={"K","J","Q"}, BjFaceCardSymAce={"A1","A11"};
    static int[]BjAceCard={1,11}, BjFaceCard={10}, NumCard={2,3,4,5,6,7,8,9,10};
    static int yourtotalcard , dealertotalcard , BjMove=0;
    static String bjusername;
    static String bjalloweduseridstr = "";
    static boolean bjstart =false;
    static Timer BJtimer = new Timer();
    //************************** FOR ROCK PAPER SCISSORS ************************||
    static EmbedBuilder gbkembd = new EmbedBuilder();
    static String gbkplayerid1, gbkplayerid2, gbkplayer2username, gbkplayer1username="";
    static byte gbkplayer1choice,gbkplayer2choice =-1;
    static boolean gbkstart= false;
    static Timer GBKtimer = new Timer();
    static byte gbkcomputerchoice =-1;

    static Random random = new Random();
        //=========================================================== METHOD =====================================================================||
    //||---------------------------------------------------- HELP COMMAND -------------------------------------------------------------||
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
    //||------------------------------------------------------- MATH COMMAND -------------------------------------------------------------||
    public static void mathcommand(String inputuser,MessageReceivedEvent event){
        String mathinput ="";
        if (inputuser.startsWith("!math ")){
            mathinput = inputuser.substring(6);
        }else if(inputuser.startsWith("= ")){
            mathinput=inputuser.substring(2);
        }
        String resultstr = evaluateExpression(mathinput,event,mathinput);
        if(!resultstr.equals("NaN")) {
            String resultstr2 = format3digit(resultstr);
            System.out.println(" |= " + resultstr);
            Mathembd(event,mathinput,resultstr2);
        }
    }
    public static void mathcommand(String mathinput, SlashCommandInteractionEvent event){
        String resultstr = evaluateExpression(mathinput,event,mathinput);
        if(!resultstr.equals("NaN")) {
            String resultstr2 = format3digit(resultstr);
            System.out.println(" |= " + resultstr);
            Mathembd(event,mathinput,resultstr2);
        }
    }
    //||------------------------------------------------------ COINFLIP COMMAND -----------------------------------------------------||
    public static void coinflipcommand(MessageReceivedEvent event){
        int coinfacing= random.nextInt(1,3);
        String coinfacing2 = (coinfacing==1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2);
    }
    public static void coinflipcommand(SlashCommandInteractionEvent event){
        int coinfacing= random.nextInt(1,3);
        String coinfacing2 = (coinfacing==1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2);
    }
    //
    public static void coinflipguesscommand(String cfselect,MessageReceivedEvent event){
        int coinfacing = random.nextInt(1, 3);
        String coinfacing2 = (coinfacing == 1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2,cfselect);
    }
    public static void coinflipguesscommand(String cfselect,SlashCommandInteractionEvent event){
        int coinfacing = random.nextInt(1, 3);
        String coinfacing2 = (coinfacing == 1) ? "Head" : "Tail";
        coinflipembd(event,coinfacing2,cfselect);
    }
    //||----------------------------------------------------- DICEROLL COMMAND --------------------------------------------------------------||
    public static void dicerollcommand(MessageReceivedEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,dicenumber);
    }
    public static void dicerollcommand(SlashCommandInteractionEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,dicenumber);
    }
    //
    public static void dicerollguesscommand(String drguessnum,MessageReceivedEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,drguessnum,dicenumber);
    }
    public static void dicerollguesscommand(String drguessnum,SlashCommandInteractionEvent event){
        int dicenumber = random.nextInt(1,7);
        Drembd(event,drguessnum,dicenumber);
    }
    //||----------------------------------------------------- BlackJack Command -----------------------------------------------||
    public static void blackjackcommand(MessageReceivedEvent event,String username){
        blackjackstartingshuffle(username);
        bjembd(event); //METHOD
    }
    public static void blackjackcommand(SlashCommandInteractionEvent event,String username){
        blackjackstartingshuffle(username);
        bjembd(event);
    }
    //
    private static void blackjackstartingshuffle(String username){
        String matchesnum;
        bjstart = true;
        boolean Acetaken=false;
        yourtotalcard=0;
        dealertotalcard=0;
        bjusername = username;

        //||*************Starting**************||
        for(int i=0;i<2;i++){ //||------------------- Your card -----------------||
            if(!Acetaken) {
                int ratio = random.nextInt(1,14);
                if (ratio == 1) {
                    yourcard.add(BjAceCard[random.nextInt(BjAceCard.length)]);
                    if (yourcard.get(i).equals(1)) {
                        matchesnum = BjFaceCardSymAce[0];
                    } else {
                        matchesnum = BjFaceCardSymAce[1];
                    }
                    yourcardpic.add(matchesnum);
                    Acetaken=true;
                } else if (ratio >= 2 && ratio <= 4) {
                    yourcard.add(BjFaceCard[random.nextInt(BjFaceCard.length)]);
                    matchesnum = BjFaceCardSym[random.nextInt(BjFaceCardSym.length)];
                    yourcardpic.add(matchesnum);
                } else if (ratio >= 5) {
                    yourcard.add(NumCard[random.nextInt(NumCard.length)]);
                    matchesnum = String.valueOf(yourcard.get(i));
                    yourcardpic.add(matchesnum);
                }
            }else{
                int ratio = random.nextInt(1,13);
                Acetaken=false;
                if (ratio >= 1 && ratio <= 3) {
                    yourcard.add(BjFaceCard[random.nextInt(BjFaceCard.length)]);
                    matchesnum = BjFaceCardSym[random.nextInt(BjFaceCardSym.length)];
                    yourcardpic.add(matchesnum);
                } else if (ratio >= 4) {
                    yourcard.add(NumCard[random.nextInt(NumCard.length)]);
                    matchesnum = String.valueOf(yourcard.get(i));
                    yourcardpic.add(matchesnum);
                }
            }
        }
        for(int i=0;i<2;i++){ //||------------------- Dealer card -------------------||
            if(!Acetaken){
                int ratio = random.nextInt(1,14);
                if(ratio==1){
                    dealercard.add(BjAceCard[random.nextInt(BjAceCard.length)]);
                    if(dealercard.get(i).equals(1)){
                        matchesnum = BjFaceCardSymAce[0];
                    }else{
                        matchesnum = BjFaceCardSymAce[1];
                    }
                    dealercardpic.add(matchesnum);
                    Acetaken=true;
                }else if(ratio>=2&&ratio<=4){
                    dealercard.add(BjFaceCard[random.nextInt(BjFaceCard.length)]);
                    matchesnum = BjFaceCardSym[random.nextInt(BjFaceCardSym.length)];
                    dealercardpic.add(matchesnum);
                }else if(ratio>=5) {
                    dealercard.add(NumCard[random.nextInt(NumCard.length)]);
                    matchesnum = String.valueOf(dealercard.get(i));
                    dealercardpic.add(matchesnum);
                }
            }
            else{
                int ratio = random.nextInt(1,13);
                Acetaken=false;
                if(ratio>=1&&ratio<=3){
                    dealercard.add(BjFaceCard[random.nextInt(BjFaceCard.length)]);
                    matchesnum = BjFaceCardSym[random.nextInt(BjFaceCardSym.length)];
                    dealercardpic.add(matchesnum);
                }else if(ratio>=4) {
                    dealercard.add(NumCard[random.nextInt(NumCard.length)]);
                    matchesnum = String.valueOf(dealercard.get(i));
                    dealercardpic.add(matchesnum);
                }
            }
        }
        for (int i = 0; i < yourcard.size(); i++) {
            yourcardpic2.append(yourcardpic.get(i));
            if (i < yourcard.size() - 1) {
                yourcardpic2.append("+"); // Tambahkan "+" setelah elemen, kecuali untuk elemen terakhir
            }
        }
        for (int i = 0; i < dealercard.size(); i++) {
            dealercardpic2.append(dealercardpic.get(i));
            if (i < dealercard.size() - 1) {
                dealercardpic2.append("+"); // Tambahkan "+" setelah elemen, kecuali untuk elemen terakhir
            }
        }
        for(int num:yourcard){
            yourtotalcard+=num;
        }
        for(int num:dealercard){
            dealertotalcard+=num;
        }
    }
    //||----------------------------------------------------- ROCK PAPER SCISSORS ---------------------------------------------||
    public static void directgbkcommand(MessageReceivedEvent event,String yourchoice){
        Random random = new Random();
        byte gbkcomputerchoice = (byte)(random.nextInt(1,4)); //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
        switch(yourchoice){
            case "b"->yourchoice="batu";
            case "g"->yourchoice="gunting";
            case "k"->yourchoice="kertas";
        }
        gbkplayer1username = username;
        directgbkembd(event,gbkcomputerchoice,yourchoice);
    }
    //
    public static void gbkcommand(MessageReceivedEvent event){
        gbkstart = true;
        Random random = new Random();
        if(gbkplayerid2.isEmpty()) {
            gbkcomputerchoice = (byte) (random.nextInt(1, 4)); //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
        }
        gbkembd(event);
    }
    public static void gbkcommand(SlashCommandInteractionEvent event){
        gbkstart = true;
        Random random = new Random();
        if(gbkplayerid2.isEmpty()) {
            gbkcomputerchoice = (byte) (random.nextInt(1, 4)); //GOOD TO KNOW > 1= ROCK 2= PAPER 3= SCISSORS
        }
        gbkembd(event);
    }
    //===================================================================================================================================================================||



    //||**************------------------------ NO TRESPASSING UNTIL CALCULATOR MACHINE :V ---------------******************||
    public static String format3digit(String number) {
        // Cek apakah ada desimal
        if (number.contains(".")) {
            String[] parts = number.split("\\."); // Pisahkan angka dan desimal
            // Format angka sebelum desimal
            DecimalFormat format = new DecimalFormat("#,###");
            String formattedInteger = format.format(Long.parseLong(parts[0]));
            return formattedInteger + "." + parts[1]; // Gabungkan angka dan desimal
        } else {
            // Format angka tanpa desimal
            DecimalFormat format = new DecimalFormat("#,###");
            return format.format(Long.parseLong(number));
        }
    }

    //||======================================================== MATH CALCULATOR MACHINE ================================================||
    public static String evaluateExpression(String expressionInput, SlashCommandInteractionEvent event, String mathinput) {
        try {
            String preprocessedExpression = preprocessExpression(expressionInput);
            // Ganti spasi, pi, e, dan koma ke titik; juga ganti Ã×, Ã÷ jadi * dan /
            StringBuilder expression = new StringBuilder(
                    preprocessedExpression
                            .replaceAll(" ", "")
                            .replaceAll("pi", String.valueOf(Math.PI))
                            .replaceAll("π", String.valueOf(Math.PI))
                            .replaceAll("e", String.valueOf(Math.E))
                            .replaceAll(",", ".")
                            .replaceAll("×","*")
                            .replaceAll("÷","/")
                            .replaceAll("∛","cbrt")
                            .replaceAll("√","sqrt")
            );

            // Lakukan perhitungan
            double result = evalBasicMath(expression);

            // Pembulatan/formatting max 15 digit desimal, buang trailing zero
            BigDecimal bd = BigDecimal.valueOf(result)
                    .setScale(15, RoundingMode.HALF_UP)
                    .stripTrailingZeros();

            return bd.toPlainString();

        } catch (RuntimeException e) {
            String exception = e.getMessage();
            EmbedBuilder errormathembd = new EmbedBuilder();
            errormathembd.setColor(0xab00ff);
            switch (exception){
                case "uns":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Argument __salah__/__tidak support__ atau __tidak ada dalam matematika__**");
                    if(event != null){
                        event.replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
                case "largefac":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Faktorial terlalu besar (max 20!)**");
                    if(event != null){
                        event.replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
                case "dcf":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Terdapat angka __desimal__ dalam faktorial**");
                    if(event != null){
                        event.replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
                case "2.,":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**satu saja ya '`.`' atau '`,`' nya**");
                    if(event != null){
                        event.replyEmbeds(errormathembd.build()).queue();
                    }
                case "dv0":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Terdapat pembagian dengna angka __0__**");
                    if(event != null){
                        event.replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
            }
            // Jika parsing error, kembalikan "NaN" atau sesuai kebutuhan
            return "NaN";
        }
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~||
    public static String evaluateExpression(String expressionInput, MessageReceivedEvent event, String mathinput) {
        try {
            String preprocessedExpression = preprocessExpression(expressionInput);
            // Ganti spasi, pi, e, dan koma ke titik; juga ganti Ã×, Ã÷ jadi * dan /
            StringBuilder expression = new StringBuilder(
                    preprocessedExpression
                            .replaceAll(" ", "")
                            .replaceAll("pi", String.valueOf(Math.PI))
                            .replaceAll("π", String.valueOf(Math.PI))
                            .replaceAll("e", String.valueOf(Math.E))
                            .replaceAll(",", ".")
                            .replaceAll("×","*")
                            .replaceAll("÷","/")
                            .replaceAll("∛","cbrt")
                            .replaceAll("√","sqrt")
            );

            // Lakukan perhitungan
            double result = evalBasicMath(expression);

            // Pembulatan/formatting max 15 digit desimal, buang trailing zero
            BigDecimal bd = BigDecimal.valueOf(result)
                    .setScale(15, RoundingMode.HALF_UP)
                    .stripTrailingZeros();

            return bd.toPlainString();

        } catch (RuntimeException e) {
            String exception = e.getMessage();
            EmbedBuilder errormathembd = new EmbedBuilder();
            errormathembd.setColor(0xab00ff);
            switch (exception){
                case "uns":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Argument __salah__/__tidak support__ atau __tidak ada dalam matematika__**");
                    if(event != null){
                        event.getMessage().replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
                case "largefac":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Faktorial terlalu besar (max 20!)**");
                    if(event != null){
                        event.getMessage().replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
                case "dcf":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Terdapat angka __desimal__ dalam faktorial**");
                    if(event != null){
                        event.getMessage().replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
                case "2.,":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**satu saja ya '`.`' atau '`,`' nya**");
                    if(event != null){
                        event.getMessage().replyEmbeds(errormathembd.build()).queue();
                    }
                case "dv0":
                    errormathembd.setTitle("ERROR! Kalkulator");
                    errormathembd.setDescription(mathinputconvert(mathinput)+"\n**Terdapat pembagian dengna angka __0__**");
                    if(event != null){
                        event.getMessage().replyEmbeds(errormathembd.build()).queue();
                    }
                    break;
            }
            // Jika parsing error, kembalikan "NaN" atau sesuai kebutuhan
            return "NaN";
        }
    }
    private static String expandNumberSuffixes(String input) {
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+)?)(k|jt|m|t)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String numberStr = m.group(1);
            String suffix = m.group(2).toLowerCase();
            double val = Double.parseDouble(numberStr);
            switch (suffix) {
                case "k":  // ribu
                    val *= 1_000;
                    break;
                case "jt": // juta
                    val *= 1_000_000;
                    break;
                case "m":  // milyar
                    val *= 1_000_000_000;
                    break;
                case "t":  // triliun
                    val *= 1_000_000_000_000L;
                    break;
            }
            String replacement = BigDecimal.valueOf(val)
                    .stripTrailingZeros()
                    .toPlainString();
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }
    private static String preprocessExpression(String expressionInput) {
        // 1) Perluas akhiran k/jt/m/t
        String expandedSuffix = expandNumberSuffixes(expressionInput);

        // 2) Tambahkan '*' jika ada pola digit( atau )digit
        StringBuilder result = new StringBuilder();
        char prevChar = '\0';
        for (int i = 0; i < expandedSuffix.length(); i++) {
            char currentChar = expandedSuffix.charAt(i);
            if (Character.isDigit(prevChar) && currentChar == '(') {
                result.append('*');
            }
            if (prevChar == ')' && Character.isDigit(currentChar)) {
                result.append('*');
            }
            result.append(currentChar);
            prevChar = currentChar;
        }
        // 3) Handle persentase
        return handlePercentage(result.toString());
    }
    /**
     * Mengubah pola persentase seperti:
     *   - x + y%, x - y%  => x ± (x*(y/100))
     *   - x * y%, x / y%  => x * (y/100) atau x / (y/100)
     *   - (...%)         => ((...)/100), dsb.
     */
    private static String handlePercentage(String preprocessed) {
        // 1) Tangani pola: x + y% => x + (x*(y/100)), dan x - y% => x - (x*(y/100))
        Pattern addOrSubPattern = Pattern.compile("(\\S+)\\s*([+\\-])\\s*(\\d+(?:\\.\\d+)?)%");
        boolean found = true;
        while (found) {
            Matcher m = addOrSubPattern.matcher(preprocessed);
            if (m.find()) {
                String leftOperand = m.group(1);
                String op = m.group(2);
                String pctNumber = m.group(3);

                // Coba selalu parse leftOperand, supaya "23*0.26" dll bisa dianggap numeric.
                double leftValue;
                try {
                    leftValue = evalBasicMath(new StringBuilder(leftOperand));
                } catch (Exception e) {
                    // Kalau benar-benar gagal parse, hentikan agar tidak loop terus:
                    break;
                }

                BigDecimal percentage = new BigDecimal(pctNumber)
                        .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);

                BigDecimal result;
                BigDecimal bigLeft = BigDecimal.valueOf(leftValue);

                if (op.equals("+")) {
                    result = bigLeft.add(bigLeft.multiply(percentage));
                } else {
                    result = bigLeft.subtract(bigLeft.multiply(percentage));
                }
                String replacement = result.stripTrailingZeros().toPlainString();

                // Lakukan replaceFirst
                preprocessed = m.replaceFirst(Matcher.quoteReplacement(replacement));
            } else {
                found = false;
            }
        }

        // 2) Tangani pola: x * y% => x*(y/100), x / y% => x/(y/100)
        //    Perbaikan: tambahkan [*/] agar * juga tertangani.
        Pattern mulOrDivPattern = Pattern.compile("(\\S+)\\s*([*/])\\s*(\\d+(?:\\.\\d+)?)%");
        found = true;
        while (found) {
            Matcher m = mulOrDivPattern.matcher(preprocessed);
            if (m.find()) {
                String leftOperand = m.group(1);
                String op = m.group(2);
                String pctNumber = m.group(3);

                double leftValue;
                try {
                    leftValue = evalBasicMath(new StringBuilder(leftOperand));
                } catch (Exception e) {
                    // Jika gagal parse leftOperand jadi angka, hentikan
                    break;
                }
                BigDecimal bigLeft = BigDecimal.valueOf(leftValue);

                BigDecimal percentage = new BigDecimal(pctNumber)
                        .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);

                BigDecimal result;
                if (op.equals("*")) {
                    result = bigLeft.multiply(percentage);
                } else {
                    // x / (y/100) = x * (100 / y)
                    result = bigLeft.divide(percentage, MathContext.DECIMAL128);
                }
                String replacement = result.stripTrailingZeros().toPlainString();

                preprocessed = m.replaceFirst(Matcher.quoteReplacement(replacement));
            } else {
                found = false;
            }
        }

        // 3) Tangani pola ( ... %) => (( ... )/100)
        preprocessed = handleParenPct(preprocessed);

        // 4) Ganti semua leftover "angka%" menjadi "(angka/100)"
        //    (Misalnya "50%" => "(50/100)")
        Pattern leftoverPct = Pattern.compile("(\\d+(?:\\.\\d+)?)%");
        Matcher leftoverMatcher = leftoverPct.matcher(preprocessed);
        StringBuilder sb2 = new StringBuilder();
        while (leftoverMatcher.find()) {
            String pctNumber = leftoverMatcher.group(1);
            leftoverMatcher.appendReplacement(sb2, "(" + pctNumber + "/100)");
        }
        leftoverMatcher.appendTail(sb2);

        preprocessed = sb2.toString();
        return preprocessed;
    }
    /**
     * Mencari setiap "%)" lalu mengubah "( ... %)" menjadi "(( ... )/100)".
     * Mendukung nested parentheses sejauh mungkin.
     */
    private static String handleParenPct(String expr) {
        int index = expr.indexOf("%)");
        while (index != -1) {
            int openIndex = findMatchingOpenParen(expr, index - 1);
            if (openIndex == -1) {
                // Tidak ketemu pasangan '(' yang valid, hentikan
                break;
            }
            // Ubah (xxxx%) => ((xxxx)/100)
            expr = expr.substring(0, openIndex)
                    + "("
                    + expr.substring(openIndex + 1, index)
                    + "/100)"
                    + expr.substring(index + 2);

            // Cek lagi kalau masih ada "%)"
            index = expr.indexOf("%)");
        }
        return expr;
    }
    /**
     * Cari '(' yg berpasangan dengan ')' di posisi closePos,
     * mendukung nested parentheses. Mundur dari closePos.
     */
    private static int findMatchingOpenParen(String expr, int closePos) {
        int level = 0;
        for (int i = closePos; i >= 0; i--) {
            if (expr.charAt(i) == ')') {
                level++;
            } else if (expr.charAt(i) == '(') {
                if (level == 0) {
                    return i;
                }
                level--;
            }
        }
        return -1; // tidak ketemu
    }
    public static double evalBasicMath(StringBuilder expression) {

        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) {
                    handleUnsupportedCharacter((char) ch);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) {
                        x += parseTerm();
                    } else if (eat('-')) {
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) {
                        x *= parseFactor();
                    } else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) {
                            throw new RuntimeException("dv0");
                        }
                        x /= divisor;
                    } else if (eat(';')) {
                        // modulo
                        x %= parseFactor();
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;

                // Jika digit sebelum '(' maka sisipkan '*'
                if (ch == '(' && startPos > 0 && Character.isDigit(expression.charAt(startPos - 1))) {
                    expression.insert(startPos + 1, "*");
                    nextChar();
                }

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                    // Jika setelah ')' langsung digit, sisipkan '*'
                    if (pos < expression.length() && pos >= 0) {
                        if (Character.isDigit(expression.charAt(pos))) {
                            expression.insert(pos + 1, "*");
                            nextChar();
                        }
                    }
                }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    // Parse number: cek multiple '.'
                    int dotCount = 0;
                    int numberStart = pos;
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        if (ch == '.') dotCount++;
                        nextChar();
                    }
                    String numStr = expression.substring(numberStart, pos);

                    // Jika lebih dari 1 titik desimal, lempar error
                    if (dotCount > 1) {
                        throw new RuntimeException("2.,");
                    }

                    x = Double.parseDouble(numStr);
                }
                else if (isMatch("sqrt")) {
                    // sqrt(...)
                    nextChar(); nextChar(); nextChar(); nextChar(); // lewati "sqrt"
                    if (!eat('(')) handleUnsupportedCharacter('(');
                    x = Math.sqrt(parseExpression());
                    if (!eat(')')) handleUnsupportedCharacter(')');
                }
                else if (isMatch("cbrt")) {
                    // cbrt(...)
                    nextChar(); nextChar(); nextChar(); nextChar();
                    if (!eat('(')) handleUnsupportedCharacter('(');
                    x = Math.cbrt(parseExpression());
                    if (!eat(')')) handleUnsupportedCharacter(')');
                }
                else {
                    handleUnsupportedCharacter((char) ch);
                    return 0; // supaya kompilasi aman
                }

                // Faktorial
                while (eat('!')) {
                    // Tidak boleh negatif atau desimal
                    if (x < 0 || x != Math.floor(x)) {
                        throw new RuntimeException("dcf");
                    }
                    // Batasi sampai 20!
                    if (x > 20) {
                        throw new RuntimeException("largefac");
                    }

                    x = factorial(x);
                }

                // Exponent (^)
                if (eat('^')) {
                    double exponent = parseFactor();
                    x = Math.pow(x, exponent);
                }

                return x;
            }

            private double factorial(double n) {
                double result = 1;
                for (int i = 2; i <= (int)n; i++) {
                    result *= i;
                }
                return result;
            }

            private boolean isMatch(String func) {
                return expression.substring(pos).startsWith(func);
            }

            private void handleUnsupportedCharacter(char ignoredC) {
                throw new RuntimeException("uns");
            }
        }.parse();
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~||
    private static String mathinputconvert(String mathinput){
        mathinput=mathinput.replaceAll("\\*","×")
                .replaceAll("pi","π")
                .replaceAll("sqrt","√")
                .replaceAll("cbrt","∛")
                .replaceAll("/","÷")
                .replaceAll(" ","");
        return mathinput;
    }
}