package com.crushedlemon.chess.utils;

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
}
