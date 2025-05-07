package com.crushedlemon.chess.utils;

import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;

import static com.crushedlemon.chess.utils.CommonUtils.*;

public class EnPassantUtils {

    public static int populateEnPassant(int flags, Move move) {
        Piece movedPiece = move.getMovedPiece();
        int enPassantSquareIndex = 0;
        if (isPawn(movedPiece)) {
            int startingRank = getRank(move.getStartingSquare());
            int endingRank = getRank(move.getEndingSquare());
            if (startingRank == movedPiece.getEpStartingRank() && endingRank == movedPiece.getEpEndingRank()) {
                enPassantSquareIndex = (movedPiece.getEpResultingRank() * 8) + getFile(move.getEndingSquare());
            }
        }
        return (enPassantSquareIndex << 4) + (flags & 0x1111);
    }
}
