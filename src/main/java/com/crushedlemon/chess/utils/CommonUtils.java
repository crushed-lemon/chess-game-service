package com.crushedlemon.chess.utils;

import com.crushedlemon.chess.commons.model.Color;
import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Piece;

public class CommonUtils {

    public static boolean isPawn(Piece piece) {
        return piece == Piece.P || piece == Piece.p;
    }

    public static boolean isRook(Piece piece) {
        return piece == Piece.R || piece == Piece.r;
    }

    public static boolean isKing(Piece piece) {
        return piece == Piece.K || piece == Piece.k;
    }

    public static int getFile(String position) {
        return position.charAt(0) - 'a' + 1;
    }

    public static int getRank(String position) {
        return position.charAt(1) - '0';
    }

    public static Color getColor(Piece piece) {
        String pc = piece.toString();
        return pc.toLowerCase().equals(pc) ? Color.B : Color.W;
    }

    public static String getColorForUser(String username, Game game) {
        if(game.getWhitePlayerId().equals(username)) {
            return "WHITE";
        } else {
            return "BLACK";
        }
    }

    public static Color getCurrentColor(Game game) {
        return game.getCurrentPlayer() == 0 ? Color.B : Color.W;
    }

    public static String opponentOf(String userName, Game game) {
        return game.getBlackPlayerId().equals(userName) ? game.getWhitePlayerId() : game.getBlackPlayerId();
    }
}
