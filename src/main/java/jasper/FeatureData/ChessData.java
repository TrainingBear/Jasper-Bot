package jasper.FeatureData;

import jasper.FeatureData.FeatureEnum.ChessVariable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

import static jasper.Feature.Chess.chessremovegamelists;
import static jasper.Feature.Chess.chesssenderrormessg;
import static jasper.VariableList.*;

public class ChessData {
    EmbedBuilder messageembd = new EmbedBuilder();

    String[][] board =
                {{"Drook1","Dknight","Dbishop","Dqueen","Dking","Dbishop","Dknight","Drook2"}
                ,{"Dpawn","Dpawn","Dpawn","Dpawn","Dpawn","Dpawn","Dpawn","Dpawn"}
                ,{"","","","","","","",""}
                ,{"","","","","","","",""}
                ,{"","","","","","","",""}
                ,{"","","","","","","",""}
                ,{"Wpawn","Wpawn","Wpawn","Wpawn","Wpawn","Wpawn","Wpawn","Wpawn"}
                ,{"Wrook1","Wknight","Wbishop","Wqueen","Wking","Wbishop","Wknight","Wrook2"}};

    byte gameacctimer=30;
    String gameid, messageid, channelid;
    static String whoturnid;
    static String whiteside;
    static String blackside;
    static String gameStatus= "waiting";
    String lastMove="*N/A*";
    boolean iswrook1move,iswrook2move,iswkingmove,isdrook1move,isdrook2move,isdkingmove=false;
    static HashMap<String, ChessPlayerInfo> playerlist= new HashMap<>();

    public EmbedBuilder getMessageembd(){return this.messageembd;}
    public String getMessageid(){return this.messageid;}
    public String getChannelid(){return this.channelid;}
    public HashMap<String, ChessPlayerInfo> getPlayerlist(){
        return playerlist;
    }
    public ChessPlayerInfo getplayer(String userid){
        return playerlist.get(userid);
    }
    public String getTimerDisplay(){
        short timer = 0;
        for(var i : playerlist.entrySet()){
            timer= (short) i.getValue().getTimer();
            break;
        }
        byte minute = (byte) (timer/60);
        byte second = (byte) (timer%60);
        return (minute > 0 && second > 0) ? minute+"m "+second+"d" : (minute>0) ? minute+"m" : second+"d";
    }
    public byte getAccTimer(){return this.gameacctimer;}
    public String getGameStatus(){return gameStatus;}
    public String getWhoturnid(){
        return whoturnid;
    }

    public void inputplayer(String userid){
        playerlist.put(userid, new ChessPlayerInfo("idle", getusernicknamefromguild(userid)));
    }
    public void inputplayer(String userid, String status){
        playerlist.put(userid, new ChessPlayerInfo(status , getusernicknamefromguild(userid)));
    }
    public void putmessageinfo(String gameid,String channelid, String messageid, EmbedBuilder messageembd){
        this.gameid = gameid;
        this.channelid=channelid;
        this.messageid=messageid;
        this.messageembd=messageembd;
    }
    public void setTimer(short timer){
        for(var entry : playerlist.entrySet()){
            entry.getValue().setTimer(timer);
        }
    }
    public void setAccTimer(byte timer){
        this.gameacctimer=timer;
    }
    public void changeMessageEmbd(EmbedBuilder messageembd){
        this.messageembd=messageembd;
    }

    public String convertInfoToString(){
        if(whoturnid==null) start();
        StringBuilder boarddisplay = new StringBuilder().append("⠀⠀A⠀B⠀C⠀D⠀E⠀F⠀G⠀H\n");
        String[] numberlist = {"𝟷","𝟸","𝟹","𝟺","𝟻","𝟼","𝟽","𝟾"};
        for (int i = 0; i < board.length; i++) {
            byte l = (byte) (7-i);
            boarddisplay.append(numberlist[l]).append(" ");
            for (int j = 0; j < board[i].length; j++) {
                boolean isWhiteSquare = (i + j) % 2 == 0;
                String piece = board[i][j];
                ChessVariable emoji = switch (piece) {
                    case "" -> isWhiteSquare ? ChessVariable.WSQUARED : ChessVariable.DSQUARED;
                    case "Choosen" -> isWhiteSquare ? ChessVariable.CHOOSEN_WSQUARED : ChessVariable.CHOOSEN_DSQUARED;
                    case "Moved" -> isWhiteSquare ? ChessVariable.MOVED_WSQUARED : ChessVariable.MOVED_DSQUARED;

                    case "Wpawn" -> isWhiteSquare ? ChessVariable.WPAWN_WSQUARED : ChessVariable.WPAWN_DSQUARED;
                    case "ChoosenWpawn" -> isWhiteSquare ? ChessVariable.CHOOSEN_WPAWN_WSQUARED : ChessVariable.CHOOSEN_WPAWN_DSQUARED;
                    case "MovedWpawn" -> isWhiteSquare ? ChessVariable.MOVED_WPAWN_WSQURED : ChessVariable.MOVED_WPAWN_DSQUARED;

                    case "Wrook1", "Wrook2" -> isWhiteSquare ? ChessVariable.WROOK_WSQUARED : ChessVariable.WROOK_DSQUARED;
                    case "ChoosenWrook1","ChoosenWrook2" -> isWhiteSquare ? ChessVariable.CHOOSEN_WROOK_WSQUARED : ChessVariable.CHOOSEN_WROOK_DSQUARED;
                    case "MovedWrook1", "MovedWrook2" -> isWhiteSquare ? ChessVariable.MOVED_WROOK_WSQUARED : ChessVariable.MOVED_WROOK_DSQUARED;

                    case "Wbishop" -> isWhiteSquare ? ChessVariable.WBISHOP_WSQUARED : ChessVariable.WBISHOP_DSQUARED;
                    case "ChoosenWbishop" -> isWhiteSquare ? ChessVariable.CHOOSEN_WBISHOP_WSQUARED : ChessVariable.CHOOSEN_WBISHOP_DSQUARED;
                    case "MovedWbishop" -> isWhiteSquare ? ChessVariable.MOVED_WBISHOP_WSQUARED : ChessVariable.MOVED_WBISHOP_DSQUARED;

                    case "Wknight" -> isWhiteSquare ? ChessVariable.WKNIGHT_WSQUARED : ChessVariable.WKNIGHT_DSQUARED;
                    case "ChoosenWknight" -> isWhiteSquare ? ChessVariable.CHOOSEN_WKNIGHT_WSQUARED : ChessVariable.CHOOSEN_WKNIGHT_DSQUARED;
                    case "MovedWknight" -> isWhiteSquare ? ChessVariable.MOVED_WKNIGHT_WSQUARED : ChessVariable.MOVED_WKNIGHT_DSQUARED;

                    case "Wqueen" -> isWhiteSquare ? ChessVariable.WQUEEN_WSQUARED : ChessVariable.WQUEEN_DSQUARED;
                    case "ChoosenWqueen" -> isWhiteSquare ? ChessVariable.CHOOSEN_WQUEEN_WSQUARED : ChessVariable.CHOOSEN_WQUEEN_DSQUARED;
                    case "MovedWqueen" -> isWhiteSquare ? ChessVariable.MOVED_WQUEEN_WSQUARED : ChessVariable.MOVED_WQUEEN_DSQUARED;

                    case "Wking" -> isWhiteSquare ? ChessVariable.WKING_WSQUARED : ChessVariable.WKING_DSQUARED;
                    case "DeathWking" -> isWhiteSquare ? ChessVariable.DEATH_WKING_WSQUARED : ChessVariable.DEATH_WKING_DSQUARED;
                    case "MovedWking" -> isWhiteSquare ? ChessVariable.MOVED_WKING_WSQUARED : ChessVariable.MOVED_WKING_DSQUARED;


                    case "Dpawn" -> isWhiteSquare ? ChessVariable.DPAWN_WSQUARED : ChessVariable.DPAWN_DSQUARED;
                    case "ChoosenDpawn" -> isWhiteSquare ? ChessVariable.CHOOSEN_DPAWN_WSQUARED : ChessVariable.CHOOSEN_DPAWN_DSQUARED;
                    case "MovedDpawn" -> isWhiteSquare ? ChessVariable.MOVED_DPAWN_WSQURED : ChessVariable.MOVED_DPAWN_DSQUARED;

                    case "Drook1", "Drook2" -> isWhiteSquare ? ChessVariable.DROOK_WSQUARED : ChessVariable.DROOK_DSQUARED;
                    case "ChoosenDrook1","ChoosenDrook2" -> isWhiteSquare ? ChessVariable.CHOOSEN_DROOK_WSQUARED : ChessVariable.CHOOSEN_DROOK_DSQUARED;
                    case "MovedDrook1", "MovedDrook2" -> isWhiteSquare ? ChessVariable.MOVED_DROOK_WSQUARED : ChessVariable.MOVED_DROOK_DSQUARED;

                    case "Dbishop" -> isWhiteSquare ? ChessVariable.DBISHOP_WSQUARED : ChessVariable.DBISHOP_DSQUARED;
                    case "ChoosenDbishop" -> isWhiteSquare ? ChessVariable.CHOOSEN_DBISHOP_WSQUARED : ChessVariable.CHOOSEN_DBISHOP_DSQUARED;
                    case "MovedDbishop" -> isWhiteSquare ? ChessVariable.MOVED_DBISHOP_WSQUARED : ChessVariable.MOVED_DBISHOP_DSQUARED;

                    case "Dknight" -> isWhiteSquare ? ChessVariable.DKNIGHT_WSQUARED : ChessVariable.DKNIGHT_DSQUARED;
                    case "ChoosenDknight" -> isWhiteSquare ? ChessVariable.CHOOSEN_DKNIGHT_WSQUARED : ChessVariable.CHOOSEN_DKNIGHT_DSQUARED;
                    case "MovedDknight" -> isWhiteSquare ? ChessVariable.MOVED_DKNIGHT_WSQUARED : ChessVariable.MOVED_DKNIGHT_DSQUARED;

                    case "Dqueen" -> isWhiteSquare ? ChessVariable.DQUEEN_WSQUARED : ChessVariable.DQUEEN_DSQUARED;
                    case "ChoosenDqueen" -> isWhiteSquare ? ChessVariable.CHOOSEN_DQUEEN_WSQUARED : ChessVariable.CHOOSEN_DQUEEN_DSQUARED;
                    case "MovedDqueen" -> isWhiteSquare ? ChessVariable.MOVED_DQUEEN_WSQUARED : ChessVariable.MOVED_DQUEEN_DSQUARED;

                    case "Dking" -> isWhiteSquare ? ChessVariable.DKING_WSQUARED : ChessVariable.DKING_DSQUARED;
                    case "DeathDking" -> isWhiteSquare ? ChessVariable.DEATH_DKING_WSQUARED : ChessVariable.DEATH_DKING_DSQUARED;
                    case "MovedDking" -> isWhiteSquare ? ChessVariable.MOVED_DKING_WSQUARED : ChessVariable.MOVED_DKING_DSQUARED;

                    default -> ChessVariable.UNKNOWN;
                };
                boarddisplay.append(emoji.getEmoji());
                board[i][j] = board[i][j].replaceAll("Choosen|Moved","");
            }
            boarddisplay.append(" ").append(numberlist[l]);
            boarddisplay.append("\n");
        }
        boarddisplay.append("⠀⠀A⠀B⠀C⠀D⠀E⠀F⠀G⠀H\n")
                .append("Turn/move: ").append(whoturnid.equals(whiteside)
                        ? "**White** " + playerlist.get(whiteside).getUsername().replaceAll(" Offer Draw|💀|🏆| Promoting", "")
                        : "**Black** " + playerlist.get(blackside).getUsername().replaceAll(" Offer Draw|💀|🏆| Promoting", ""))
                .append("\nLast move: ").append(lastMove)
                .append("\n");
        return boarddisplay.toString();
    }
    public static void start() {
        gameStatus = "playing";
        List<String> players = new ArrayList<>(playerlist.keySet());
        if (random.nextBoolean()) {
            whiteside = players.get(0);
            blackside = players.get(1);
        } else {
            whiteside = players.get(1);
            blackside = players.get(0);
        }
        whoturnid = whiteside;
    }
    public void move(String piecename, String from, String destination, MessageReceivedEvent event) {
        char side = whoturnid.equals(whiteside) ? 'W' : 'D';
        int[] destCoords = (!destination.isEmpty()) ? parseCoordinate(destination) : null;
        if (!from.isEmpty() && !destination.isEmpty()) {
            for(byte j = 0; j < board.length; j++){
                if(board[0][j].contains(side+"pawn")){
                    chesssenderrormessg(event, "Pionmu belum di promosi, input \"p [bidak]\"", (byte) 5);
                    return;
                }
            }
            int[] src = parseCoordinate(from);
            if (!board[src[0]][src[1]].startsWith(String.valueOf(side))) {
                chesssenderrormessg(event, "Bidak tidak ditemukan", (byte) 5);
                return;
            }
            List<String> movelist = getMoveList(src[0], src[1], side);
            processMove(src[0], src[1], destCoords[0], destCoords[1], side, movelist, event);
        } else if (from.isEmpty() && !destination.isEmpty()) {
            for(byte j = 0; j < board.length; j++){
                if(board[0][j].contains(side+"pawn")){
                    chesssenderrormessg(event, "Pionmu belum di promosi, \"p [bidak]\"", (byte) 5);
                    return;
                }
            }
            int[] src = findUniquePiece(side + piecename, event);
            if (src == null) return;
            List<String> moves = getMoveList(src[0], src[1], side);
            processMove(src[0], src[1], destCoords[0], destCoords[1], side, moves, event);
        } else {
            switch(piecename){
                case "longcastle","shortcastle"-> handleCastling(piecename.equals("longcastle"), side, event);
                case "bishop", "knight","queen","rook" -> pawnpromote(piecename, side);
            }
            switch (isCheckMatedOrStale(board, side)) {
                case 1 -> {
                    endGame(" Tercheckmate!", side, event);
                    return;
                }
                case 2 -> {
                    endGame(" Stalemate!", side, event);
                    return;
                }
            }
            event.getMessage().delete().queue();
        }
        sendChessEmbed();
    }

    private int[] parseCoordinate(String pos) {
        return new int[]{(8 - Character.getNumericValue(Character.isLetter(pos.charAt(0)) ? pos.charAt(1) : pos.charAt(0)))
                , (Character.toLowerCase(Character.isLetter(pos.charAt(0)) ? pos.charAt(0) : pos.charAt(1)) - 'a')}; //row ,col
    }
    private List<String> getMoveList(int row, int col, char side) {
        List<String> moves = new ArrayList<>();
        String piece = board[row][col];
        switch (piece) {
            case "Wpawn", "Dpawn" -> pawnmove(moves, board, row, col, side);
            case "Wbishop", "Dbishop" -> bishopmove(moves, board, row, col, side);
            case "Wrook1", "Drook1", "Wrook2", "Drook2" -> rookmove(moves, board, row, col, side);
            case "Wqueen", "Dqueen" -> queenmove(moves, board, row, col, side);
            case "Wking", "Dking" -> kingmove(moves, board, row, col, side);
            case "Wknight", "Dknight" -> knightmove(moves, board, row, col, side);
        }
        return moves;
    }
    private void processMove(int srcRow, int srcCol, int destRow, int destCol, char side, List<String> moves, MessageReceivedEvent event) {
        String destNotation = "" + (char) ('a' + destCol) + (8 - destRow);
        if (moves.contains(destNotation)) {
            if(!(iswrook1move||iswrook2move||isdrook1move||isdrook2move)&&board[srcRow][srcCol].contains("rook")){
                if(board[srcRow][srcCol].contains("1")) if (side == 'W') iswrook1move = true; else isdrook1move = true;
                else if (side == 'W') iswrook2move = true; else isdrook2move = true;
            }
            else if(!(iswkingmove||isdkingmove)&&board[srcRow][srcCol].contains("king")) if (side == 'W') iswkingmove = true; else isdkingmove = true;

            playerlist.get(whoturnid).setTimer((short) (playerlist.get(whoturnid).getTimer() + 4));
            if (board[destRow][destCol].contains(String.valueOf(side == 'W' ? 'D' : 'W'))) {
                String enemysideid = whoturnid.equals(whiteside) ? blackside : whiteside;
                playerlist.get(enemysideid).setChesspiece((byte) (playerlist.get(enemysideid).getChesspiece() - 1));
                lastMove = " (×"+board[destRow][destCol].replaceAll("[WD12]|Moved","")+")";
            } //klo makan enemy
            board[destRow][destCol] = "Moved"+board[srcRow][srcCol];
            board[srcRow][srcCol] = "Moved";
            switch (isCheckMatedOrStale(board, side)) {
                case 1 -> {
                    endGame(" Tercheckmate!", side, event);
                    return;
                }
                case 2 -> {
                    endGame(" Stalemate!", side, event);
                    return;
                }
            }
            if (isInsufficientMaterial(board, side)) {
                endGame("Tidak cukup material untuk melanjutkan permainan", side, event);
            }
            char[] lettermapping = {'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'};
            byte[] numbermapping = {1,2,3,4,5,6,7,8};
            if(board[0][destCol].contains(side+"pawn")) {
                getplayer(whoturnid).setUsername(getplayer(whoturnid).getUsername()+" Promoting");//ganti user ke promoting
                lastMove= lastMove.contains("(") ?
                        board[destRow][destCol].replaceAll("[WD12]|Moved","")
                            +" "+lettermapping[srcCol]+numbermapping[srcRow]+" > "+lettermapping[destCol]+numbermapping[destRow]+lastMove
                        :board[destRow][destCol].replaceAll("[WD12]|Moved","") +" "+lettermapping[srcCol]
                            +numbermapping[srcRow]+" > "+lettermapping[destCol]+numbermapping[destRow];
            }
            else rotateAndChangeTurn(lastMove.contains("(") ?
                    board[destRow][destCol].replaceAll("[WD12]|Moved","")
                            +" "+lettermapping[srcCol]+numbermapping[srcRow]+" > "+lettermapping[destCol]+numbermapping[destRow]+lastMove
                    :board[destRow][destCol].replaceAll("[WD12]|Moved","") +" "+lettermapping[srcCol]
                        +numbermapping[srcRow]+" > "+lettermapping[destCol]+numbermapping[destRow]);
            event.getMessage().delete().queue();
        }
        else if (moves.isEmpty()) chesssenderrormessg(event, "**Tidak** ada legal move untuk bidak tersebut", (byte) 5);
        else {
            for (String move : moves) {
                int[] guide = parseCoordinate(move);
                board[guide[0]][guide[1]] = "Choosen" + board[guide[0]][guide[1]];
            }
            chesssenderrormessg(event, "Gerakan tersebut tidak legal untuk bidak tersebut", (byte) 5);
        }
    }
    private int[] findUniquePiece(String piece, MessageReceivedEvent event) {
        int pieceCount = 0, foundRow = 0, foundCol = 0;
        for (byte i = 0; i < board.length; i++) {
            for (byte j = 0; j < board.length; j++) {
                if (board[i][j].startsWith(piece)) {
                    pieceCount++;
                    foundRow = i;
                    foundCol = j;
                    if (pieceCount > 1) {
                        chesssenderrormessg(event, "Bidak yang mana? lebih dari 1", (byte) 6);
                        return null;
                    }
                }
            }
        }
        if (pieceCount == 0) {
            chesssenderrormessg(event, "Bidak tidak ditemukan", (byte) 5);
            return null;
        }
        return new int[]{foundRow, foundCol};
    }
    private void pawnpromote(String piecename, char side){
        piecename = piecename.equals("rook") ? piecename+"1" : piecename;
        for(byte j = 0; j < board.length; j++){
            if(board[0][j].contains(side+"pawn")){
                board[0][j] = side+piecename;
            }
        }
        playerlist.values().forEach(entry -> entry.setUsername(entry.getUsername().replaceAll(" Promoting",""))); //ngilangin suffix Promoting
        rotateAndChangeTurn(lastMove+= " to "+piecename);
    }
    private void handleCastling(boolean isLong, char side, MessageReceivedEvent event) {
        char enemySide = (side == 'W') ? 'D' : 'W';
        if ((side == 'W' ? iswkingmove : isdkingmove)) {
            chesssenderrormessg(event, "Rajamu sudah bergerak, tidak bisa castle", (byte) 5);
            return;
        }
        String[][] boardPreview = deepCopyBoard(board);
        if (ischeckmatewhenmoved(boardPreview, enemySide, side)) {
            chesssenderrormessg(event, "Rajamu sedang kena check, tidak bisa castle", (byte) 5);
            return;
        }
        boolean rookMoved = side == 'W'
                ? (isLong ? iswrook1move : iswrook2move)
                : (isLong ? isdrook1move : isdrook2move);
        if (rookMoved) {
            chesssenderrormessg(event, "Bentengmu sudah bergerak, tidak bisa castle", (byte) 5);
            return;
        }
        boolean isKingAt5thCol = board[7][4].contains(side + "king");
        byte pathStart, pathEnd, previewFrom, previewTo, rookFrom, rookTo, kingFrom, kingTo;
        if (isLong) {
            if (isKingAt5thCol) {
                pathStart = 1; pathEnd = 4; previewFrom = 4; previewTo = 2;
                rookFrom = 0; rookTo = 3; kingFrom = 4; kingTo = 2;
            } else {
                pathStart = 4; pathEnd = 7; previewFrom = 3; previewTo = 5;
                rookFrom = 0; rookTo = 4; kingFrom = 3; kingTo = 5;
            }
        } else {
            if (isKingAt5thCol) {
                pathStart = 5; pathEnd = 7; previewFrom = 4; previewTo = 6;
                rookFrom = 7; rookTo = 5; kingFrom = 4; kingTo = 6;
            } else {
                pathStart = 1; pathEnd = 3; previewFrom = 3; previewTo = 1;
                rookFrom = 0; rookTo = 2; kingFrom = 3; kingTo = 1;
            }
        }
        for (byte i = pathStart; i < pathEnd; i++) {
            if (!board[7][i].isEmpty()) {
                chesssenderrormessg(event, "Terdapat bidak dalam jalur castling, tidak bisa castle", (byte) 5);
                return;
            }
        }
        boardPreview[7][previewTo] = boardPreview[7][previewFrom];
        boardPreview[7][previewFrom] = "";
        if (ischeckmatewhenmoved(boardPreview, enemySide, side)) {
            chesssenderrormessg(event, "Rajamu akan terkena check, tidak bisa castle", (byte) 5);
            return;
        }
        board[7][rookTo] = board[7][rookFrom];
        board[7][rookFrom] = "";
        board[7][kingTo] = "Moved"+board[7][kingFrom];
        board[7][kingFrom] = "Moved";
        if (side == 'W') {
            if (isLong) iswrook1move = true;
            else iswrook2move = true;
            iswkingmove = true;
        } else {
            if (isLong) isdrook1move = true;
            else isdrook2move = true;
            isdkingmove = true;
        }
        rotateAndChangeTurn(isLong? "Long castle": "Short castle");
    }
    private void endGame(String statusMessage, char side, MessageReceivedEvent event) {
        if(statusMessage.equals(" Tercheckmate!")) {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board.length; j++) {
                    if (board[i][j].contains(side == 'W' ? "Dking" : "Wking")) {
                        board[i][j] = "Death" + board[i][j];
                    }
                }
            }
        }
        EmbedBuilder chssembd = this.messageembd.setDescription(convertInfoToString())
                .setFooter((whoturnid.equals(whiteside) ? getplayer(blackside).getUsername() : getplayer(whiteside).getUsername()) +statusMessage)
                .clearFields();
        playerlist.forEach((key, value) -> {
            value.setUsername(value.getUsername().replaceAll(" Promoting","") + (key.equals(whoturnid) ? " 🏆" : " 💀"));
            chssembd.addField(value.getUsername(), value.convertPlayerInfoToString(), false);
        });
        Objects.requireNonNull(guild.getTextChannelById(this.channelid))
                .retrieveMessageById(this.messageid)
                .queue(msg -> msg.editMessageEmbeds(chssembd.build()).setComponents().queue(),
                        e -> System.out.println("BJ ERROR " + e.getMessage()));
        chessremovegamelists(this.gameid);
        event.getMessage().delete().queue();
    }
    private void sendChessEmbed() {
        EmbedBuilder chssembd = this.messageembd.setDescription(convertInfoToString()).clearFields();
        lastMove="";
        playerlist.values().forEach(entry -> chssembd.addField(entry.getUsername(), entry.convertPlayerInfoToString(), false));

        Objects.requireNonNull(guild.getTextChannelById(this.channelid))
                .retrieveMessageById(this.messageid)
                .queue(msg -> msg.editMessageEmbeds(chssembd.build()).queue(),
                        e -> System.out.println("BJ ERROR " + e.getMessage()));
    }
    private void rotateAndChangeTurn(String lastmove){
        this.lastMove= lastmove;
        whoturnid = whiteside.equals(whoturnid) ? blackside : whiteside;
        byte rows = (byte) board.length
            ,cols = (byte) board[0].length;
        for (int i = 0; i < rows / 2; i++) {
            for (int j = 0; j < cols; j++) {
                // Swap baris atas dengan baris bawah
                String temp = board[i][j];
                board[i][j] = board[rows - 1 - i][j];
                board[rows - 1 - i][j] = temp;
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                // Swap kolom kiri dengan kolom kanan
                String temp = board[i][j];
                board[i][j] = board[i][cols - 1 - j];
                board[i][cols - 1 - j] = temp;
            }
        }
    }

    private static void pawnmovewhencheck(List<String> movelist, String[][] board, int row, int col, char side) {
        char enemyside = side == 'W' ? 'D' : 'W';
        char[] mapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        if (row+1 > -1 && col-1 > -1 && board[row + 1][col - 1].startsWith(String.valueOf(enemyside))) {
            String[][] boardpreview = deepCopyBoard(board);
            boardpreview[row - 1][col - 1] = boardpreview[row][col];
            boardpreview[row][col] = "";
            if (!ischeckmatewhenmoved(boardpreview, enemyside, side)) {
                movelist.add(String.valueOf(mapping[col - 1]) + (8 - (row - 1)));
            }
        }
        if (row+1 > -1 && col+1 < board.length && board[row + 1][col + 1].startsWith(String.valueOf(enemyside))) {
            String[][] boardpreview = deepCopyBoard(board);
            boardpreview[row - 1][col + 1] = boardpreview[row][col];
            boardpreview[row][col] = "";
            if (!ischeckmatewhenmoved(boardpreview, enemyside, side)) {
                movelist.add(String.valueOf(mapping[col + 1]) + (8 - (row - 1)));
            }
        }
        if(row ==1) {
            for (int i = 1; i < 3; i++) {
                String[][] boardpreview = deepCopyBoard(board);
                if (!boardpreview[row + i][col].isEmpty()) break;
                boardpreview[row + i][col] = boardpreview[row][col];
                boardpreview[row][col] = "";
                if (ischeckmatewhenmoved(boardpreview, enemyside, side)) break;
                movelist.add(String.valueOf(mapping[col]) + (8 - (row - i)));
            }
        }else{
            String[][] boardpreview = deepCopyBoard(board);
            if (boardpreview[row + 1][col].isEmpty()){
                boardpreview[row + 1][col] = boardpreview[row][col];
                boardpreview[row][col] = "";
                if(!ischeckmatewhenmoved(boardpreview, enemyside,side)) movelist.add(String.valueOf(mapping[col]) + (8 - (row - 1)));
            }
        }
    }
    private static void pawnmove(List<String> movelist, String[][] board, int row, int col, char side) {
        char enemyside = side == 'W' ? 'D' : 'W';
        char[] mapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        if (row-1 > -1 && col-1 > -1 && board[row - 1][col - 1].startsWith(String.valueOf(enemyside))) {
            String[][] boardpreview = deepCopyBoard(board);
            boardpreview[row - 1][col - 1] = boardpreview[row][col];
            boardpreview[row][col] = "";
            if (!ischeckmatewhenmoved(boardpreview, enemyside, side)) {
                movelist.add(String.valueOf(mapping[col - 1]) + (8 - (row - 1)));
            }
        }
        if (row-1 > -1 && col+1 < board.length && board[row - 1][col + 1].startsWith(String.valueOf(enemyside))) {
            String[][] boardpreview = deepCopyBoard(board);
            boardpreview[row - 1][col + 1] = boardpreview[row][col];
            boardpreview[row][col] = "";
            if (!ischeckmatewhenmoved(boardpreview, enemyside, side)) {
                movelist.add(String.valueOf(mapping[col + 1]) + (8 - (row - 1)));
            }
        }
        if(row ==6) {
            for (int i = 1; i < 3; i++) {
                String[][] boardpreview = deepCopyBoard(board);
                if (!boardpreview[row - i][col].isEmpty()) break;
                boardpreview[row - i][col] = boardpreview[row][col];
                boardpreview[row][col] = "";
                if (ischeckmatewhenmoved(boardpreview, enemyside, side)) break;
                movelist.add(String.valueOf(mapping[col]) + (8 - (row - i)));
            }
        }else{
            String[][] boardpreview = deepCopyBoard(board);
            if (boardpreview[row - 1][col].isEmpty()){
                boardpreview[row - 1][col] = boardpreview[row][col];
                boardpreview[row][col] = "";
                if(!ischeckmatewhenmoved(boardpreview, enemyside,side)) movelist.add(String.valueOf(mapping[col]) + (8 - (row - 1)));
            }
        }
    }
    private static void bishopmove(List<String> movelist, String[][] board, int row, int col, char side) {
        char enemyside = (side == 'W') ? 'D' : 'W';
        char[] mapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int boardlength = board.length;
        int[][] directions = {
                {-1, +1}, // Diagonal kanan atas
                {+1, +1}, // Diagonal kanan bawah
                {-1, -1}, // Diagonal kiri atas
                {+1, -1}  // Diagonal kiri bawah
        };
        for (int[] d : directions) {
            int r = row, c = col;
            while (true) {
                r += d[0];
                c += d[1];
                if (r < 0 || r >= boardlength || c < 0 || c >= boardlength) break;
                if (board[r][c].startsWith(String.valueOf(side))) break;
                String[][] boardpreview = deepCopyBoard(board);
                boardpreview[r][c] = boardpreview[row][col];
                boardpreview[row][col] = "";
                if (ischeckmatewhenmoved(boardpreview, enemyside, side)) continue;
                movelist.add(String.valueOf(mapping[c]) + (8 - r));
                if (!board[r][c].isEmpty()) break;
            }
        }
    }
    private static void rookmove(List<String> movelist, String[][] board, int row, int col, char side) {
        char enemyside = side == 'W' ? 'D' : 'W';
        char[] mapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int boardlength = board.length;
        int[][] directions = {
                {-1, 0}, // ke atas
                {1, 0},  // ke bawah
                {0, 1},  // ke kanan
                {0, -1}  // ke kiri
        };

        for (int[] d : directions) {
            int r = row, c = col;
            while (true) {
                r += d[0];
                c += d[1];
                if (r < 0 || r >= boardlength || c < 0 || c >= boardlength) break;
                if (board[r][c].startsWith(String.valueOf(side))) break;
                String[][] boardpreview = deepCopyBoard(board);
                boardpreview[r][c] = boardpreview[row][col];
                boardpreview[row][col] = "";
                if (ischeckmatewhenmoved(boardpreview, enemyside, side)) continue;
                movelist.add(String.valueOf(mapping[c]) + (8 - r));
                if (!board[r][c].isEmpty()) break;
            }
        }
    }
    private static void queenmove(List<String> movelist, String[][] board, int row, int col, char side) {
        char enemyside = side == 'W' ? 'D' : 'W';
        char[] mapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int boardlength = board.length;
        int[][] directions = {
                {-1, 0},  // ke atas
                {1, 0},   // ke bawah
                {0, 1},   // ke kanan
                {0, -1},  // ke kiri
                {-1, 1},  // diagonal kanan atas
                {1, 1},   // diagonal kanan bawah
                {-1, -1}, // diagonal kiri atas
                {1, -1}   // diagonal kiri bawah
        };
        for (int[] d : directions) {
            int r = row, c = col;
            while (true) {
                r += d[0];
                c += d[1];
                if (r < 0 || r >= boardlength || c < 0 || c >= boardlength) break;
                if (board[r][c].startsWith(String.valueOf(side))) break;
                String[][] boardpreview = deepCopyBoard(board);
                boardpreview[r][c] = boardpreview[row][col];
                boardpreview[row][col] = "";
                if (ischeckmatewhenmoved(boardpreview, enemyside, side)) continue;
                movelist.add(String.valueOf(mapping[c]) + (8 - r));
                if (!board[r][c].isEmpty()) break;
            }
        }
    }
    private static void knightmove(List<String> movelist, String[][] board, int row, int col, char side) {
        char enemyside = side == 'W' ? 'D' : 'W';
        char[] mapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int boardlength = board.length;
        int[][] moves = {
                {-2, +1}, {-2, -1}, {+2, +1}, {+2, -1},
                {-1, +2}, {-1, -2}, {+1, +2}, {+1, -2}
        };
        for (int[] d : moves) {
            int r = row + d[0]
                    , c = col + d[1];
            String[][] boardpreview = deepCopyBoard(board);
            if (r < 0 || r >= boardlength || c < 0 || c >= boardlength
                    ||boardpreview[r][c].startsWith(String.valueOf(side))) continue;
            boardpreview[r][c] = boardpreview[row][col];
            boardpreview[row][col] = "";
            if(ischeckmatewhenmoved(boardpreview, enemyside, side)) continue;
            movelist.add(String.valueOf(mapping[c]) + (8 - r));
        }
    }
    private static void kingmove(List<String> movelist, String[][] board, int row, int col, char side) {
        char enemyside = side == 'W' ? 'D' : 'W';
        char[] mapping = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int boardlength = board.length;
        int[][] moves = { // row , colum
                {-1,-1},{-1,0},{-1,+1}
                ,{0,-1}       ,{0,+1}
                ,{+1,-1},{+1,0},{+1,+1}
        };
        for (int[] d : moves) {
            int r = row + d[0]
                    , c = col + d[1];
            String[][] boardpreview = deepCopyBoard(board);
            if (r < 0 || r >= boardlength || c < 0 || c >= boardlength
                ||boardpreview[r][c].startsWith(String.valueOf(side))) continue;
            boardpreview[r][c] = boardpreview[row][col];
            boardpreview[row][col] = "";
            if(ischeckmatewhenmoved(boardpreview, enemyside, side)) continue;
            movelist.add(String.valueOf(mapping[c]) + (8 - r));
        }
    }

    private static boolean ischeckmatewhenmoved(String[][] boardpreview,char enemyside, char side){
        boolean[][] kingthreat=
                {{false,false,false,false,false,false,false,false}
                ,{false,false,false,false,false,false,false,false}
                ,{false,false,false,false,false,false,false,false}
                ,{false,false,false,false,false,false,false,false}
                ,{false,false,false,false,false,false,false,false}
                ,{false,false,false,false,false,false,false,false}
                ,{false,false,false,false,false,false,false,false}
                ,{false,false,false,false,false,false,false,false}};
        for (int i = 0; i < boardpreview.length; i++) {
            for (int j = 0; j < boardpreview.length; j++) {
                if(boardpreview[i][j].startsWith(String.valueOf(enemyside))){
                    if(enemyside=='W') {
                        switch (boardpreview[i][j]) {
                            case "Wpawn" -> pawnthreatpath(kingthreat,boardpreview,i,j);
                            case "Wbishop"-> bishopthreatpath(kingthreat,boardpreview,i,j);
                            case "Wrook1","Wrook2"-> rookthreatpath(kingthreat,boardpreview,i,j);
                            case "Wknight" -> knightthreatpath(kingthreat, boardpreview, i ,j);
                            case "Wqueen"->queenthreatpath(kingthreat, boardpreview,i,j);
                            case "Wking" -> kingthreatpath(kingthreat,boardpreview,i,j);
                        }
                    }else{
                        switch (boardpreview[i][j]) {
                            case "Dpawn" -> pawnthreatpath(kingthreat,boardpreview,i,j);
                            case "Dbishop"-> bishopthreatpath(kingthreat,boardpreview,i,j);
                            case "Drook1","Drook2"-> rookthreatpath(kingthreat,boardpreview,i,j);
                            case "Dknight" -> knightthreatpath(kingthreat, boardpreview, i ,j);
                            case "Dqueen" ->queenthreatpath(kingthreat, boardpreview,i,j);
                            case "Dking" -> kingthreatpath(kingthreat,boardpreview,i,j);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < boardpreview.length; i++) {
            for (int j = 0; j < boardpreview.length; j++) {
                if(boardpreview[i][j].equals(side+"king")&&kingthreat[i][j]) return true;
            }
        }
        return false;
    }
    private static byte isCheckMatedOrStale(String[][] board, char side){ // 0 nah, 1 check, 2 stale
        char enemyside = side == 'W' ? 'D' : 'W';
        List<String> movelist = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if(board[i][j].startsWith(String.valueOf(enemyside))){
                    if(enemyside=='D') {
                        switch (board[i][j]) {
                            case "Dpawn" -> pawnmovewhencheck(movelist,board,i,j,enemyside);
                            case "Dbishop"-> bishopmove(movelist,board,i,j,enemyside);
                            case "Drook1","Drook2"-> rookmove(movelist,board,i,j,enemyside);
                            case "Dknight" -> knightmove(movelist,board,i,j,enemyside);
                            case "Dqueen"-> queenmove(movelist,board,i,j,enemyside);
                            case "Dking" -> kingmove(movelist,board,i,j,enemyside);
                        }
                    }else{
                        switch (board[i][j]) {
                            case "Wpawn" -> pawnmovewhencheck(movelist,board,i,j,enemyside);
                            case "Wbishop"-> bishopmove(movelist,board,i,j,enemyside);
                            case "wrook1","Wrook2"-> rookmove(movelist,board,i,j,enemyside);
                            case "Wknight" -> knightmove(movelist,board,i,j,enemyside);
                            case "Wqueen"-> queenmove(movelist,board,i,j,enemyside);
                            case "Wking" -> kingmove(movelist,board,i,j,enemyside);
                        }
                    }
                    if(!movelist.isEmpty()) return 0;
                }
            }
        }
        String[][] boardpreview = deepCopyBoard(board);
        if(ischeckmatewhenmoved(boardpreview,side,enemyside)) return 1;
        else return 2;
    }
    private static boolean isInsufficientMaterial(String[][] board, char side){
        char enemyside = side == 'W' ? 'D' : 'W';
        List<String> sidepiece= new ArrayList<>()
                    ,enemypiece = new ArrayList<>();
        byte sidetotal=0,enemytotal=0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if(board[i][j].contains(String.valueOf(side))){
                    if(++sidetotal<3) sidepiece.add(board[i][j].contains("bishop")
                            ? ((i + j) % 2 == 0 ? board[i][j]+"WSQ": board[i][j]+"DSQ" ) : board[i][j]);
                    else return false;
                }
                if(board[i][j].contains(String.valueOf(enemyside))) {
                    if(++enemytotal<3) enemypiece.add(board[i][j].contains("bishop")
                            ? ((i + j) % 2 == 0 ? board[i][j]+"WSQ": board[i][j]+"DSQ" ) : board[i][j]);
                    else return false;
                }
            }
        }
        if((sidetotal==2 && enemytotal == 1)) return (sidepiece.contains(side + "bishopWSQ") || (sidepiece.contains(side + "bishopDSQ")))
                                                        || sidepiece.contains(side + "knight");
        else if((sidetotal==1 && enemytotal == 2)) return (enemypiece.contains(enemyside + "bishopWSQ") || (enemypiece.contains(enemyside + "bishopDSQ")))
                                                            || enemypiece.contains(enemyside + "knight");
        else return (sidetotal == 2 && enemytotal == 2) && ((sidepiece.contains(side + "bishop") && enemypiece.contains(enemyside + "bishop"))
                    || (sidepiece.contains(side + "bishopWSQ") && enemypiece.contains(enemyside + "bishopWSQ"))
                    || (sidepiece.contains(side + "bishopDSQ") && enemypiece.contains(enemyside + "bishopDSQ")));
    }

    private static void pawnthreatpath(boolean[][] kingthreat, String[][] boardpreview,int i , int j){
        if(i+1 < boardpreview.length && j+1<boardpreview.length) if (boardpreview[i + 1][j + 1].contains("king")
                || boardpreview[i + 1][j + 1].isEmpty()) kingthreat[i + 1][j + 1] = true;
        if(i+1 < boardpreview.length&&j-1 > -1) if(boardpreview[i + 1][j - 1].contains("king")
                || boardpreview[i+1][j-1].isEmpty()) kingthreat[i+1][j-1] = true;
    }
    private static void rookthreatpath(boolean[][] kingthreat, String[][] boardpreview, int i, int j) {
        int boardlength = boardpreview.length;
        // Arah pergerakan rook: atas, bawah, kanan, kiri
        int[][] directions = {
                {-1, 0}, // ke atas
                {1, 0},  // ke bawah
                {0, 1},  // ke kanan
                {0, -1}  // ke kiri
        };
        for (int[] d : directions) {
            int r = i;
            int c = j;
            while (true) {
                r += d[0];
                c += d[1];
                // Hentikan jika keluar dari papan
                if (r < 0 || r > boardlength-1 || c < 0 || c > boardlength-1) break;
                if (!(boardpreview[r][c].isEmpty() || boardpreview[r][c].contains("king"))) break;
                kingthreat[r][c] = true;
            }
        }
    }
    private static void bishopthreatpath(boolean[][] kingthreat, String[][] boardpreview, int i, int j) {
        byte boardlength = (byte) boardpreview.length;
        // Semua arah gerakan bishop (diagonal)
        int[][] directions = {
                {-1, +1}, // Diagonal kanan atas
                {+1, +1}, // Diagonal kanan bawah
                {-1, -1}, // Diagonal kiri atas
                {+1, -1}  // Diagonal kiri bawah
        };

        // Iterasi untuk tiap arah diagonal
        for (int[] d : directions) {
            int r = i, c = j;
            while (true) {
                r += d[0];
                c += d[1];
                if (r < 0 || r > boardlength-1 || c < 0 || c > boardlength-1) break;
                if (!(boardpreview[r][c].isEmpty() || boardpreview[r][c].contains("king"))) break;
                kingthreat[r][c] = true;
            }
        }
    }
    private static void knightthreatpath(boolean[][] kingthreat, String[][] boardpreview, int i, int j) {
        int boardlength = boardpreview.length;
        // Semua kemungkinan gerakan kuda
        int[][] moves = {
                {-2, +1}, {-2, -1}, {+2, +1}, {+2, -1},
                {-1, +2}, {-1, -2}, {+1, +2}, {+1, -2}
        };
        // Iterasi untuk setiap kemungkinan gerakan
        for (int[] move : moves) {
            int newRow = i + move[0];
            int newCol = j + move[1];

            // Pastikan tidak out of bounds
            if (newRow > -1 && newRow < boardlength && newCol > -1 && newCol < boardlength
                    && (boardpreview[newRow][newCol].contains("king") || boardpreview[newRow][newCol].isEmpty())) kingthreat[newRow][newCol] = true;
        }
    }
    private static void queenthreatpath(boolean[][] kingthreat, String[][] boardpreview, int i, int j) {
        int boardlength = boardpreview.length;
        // Semua arah gerakan queen (diagonal, vertikal, horizontal)
        int[][] directions = {
                {-1, +1}, // Diagonal kanan atas
                {+1, +1}, // Diagonal kanan bawah
                {-1, -1}, // Diagonal kiri atas
                {+1, -1}, // Diagonal kiri bawah
                {-1,  0}, // Ke atas
                {+1,  0}, // Ke bawah
                { 0, +1}, // Ke kanan
                { 0, -1}  // Ke kiri
        };
        // Iterasi untuk tiap arah
        for (int[] d : directions) {
            int r = i;
            int c = j;
            while (true) {
                r += d[0];
                c += d[1];
                if (r < 0 || r > boardlength-1 || c < 0 || c > boardlength-1) break;
                if (!(boardpreview[r][c].isEmpty() || boardpreview[r][c].contains("king"))) break;
                kingthreat[r][c] = true;
            }
        }
    }
    private static void kingthreatpath(boolean[][] kingthreat,String[][] boardpreview,int i, int j){
        int boardlength = boardpreview.length;
        // Semua kemungkinan gerakan kuda
        int[][] moves = { // row , colum
                {-1,-1},{-1,0},{-1,+1}
                ,{0,-1}       ,{0,+1}
                ,{+1,-1},{+1,0},{+1,+1}
        };
        for (int[] move : moves) {
            int newRow = i + move[0];
            int newCol = j + move[1];

            // Pastikan tidak out of bounds
            if (newRow > -1 && newRow < boardlength && newCol > -1 && newCol < boardlength
                    && (boardpreview[newRow][newCol].contains("king") || boardpreview[newRow][newCol].isEmpty())) kingthreat[newRow][newCol] = true;
        }
    }
    private static String[][] deepCopyBoard(String[][] original) {
        if (original == null) {
            return null;
        }
        String[][] copy = new String[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone(); // Copy tiap baris secara independen
        }
        return copy;
    }
    public static class ChessPlayerInfo{
        String status,username;
        short timer=300;
        byte chesspiece=16;
        ChessPlayerInfo(String status, String username){
            this.status=status;
            this.username= username;
        }
        public void setTimer(short timer){
            this.timer= timer;
        }
        private void setChesspiece(byte how_much){
            this.chesspiece = how_much;
        }
        public void setStatus(String status){
            this.status=status;
        }
        public void setUsername(String username){
            this.username=username;
        }
        private byte getChesspiece(){
            return this.chesspiece;
        }
        public String getUsername(){
            return this.username;
        }
        public String getStatus(){
            return this.status;
        }
        public int getTimer(){return this.timer;}
        public String convertPlayerInfoToString(){
            short minute = (short) (this.timer/60);
            short second = (short) (this.timer%60);
            String timerdisplay =
                    (minute>0&&second>0) ? minute+"m "+second+"d" : (minute>0) ? minute+"m" : second+"d";
            return "**Piece**: "+chesspiece+" "+"**Timer**: "+timerdisplay;
        }
    }
}