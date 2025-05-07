package com.crushedlemon.chess.utils;

import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

import static com.crushedlemon.chess.utils.CommonUtils.isKing;
import static com.crushedlemon.chess.utils.CommonUtils.isRook;

public class CastleUtils {

    @AllArgsConstructor
    @Value
    public static class CastleContext {
        int mask;
        Piece expectedPiece;
        String expectedSquare;
    }

    public final static List<CastleContext> castleContexts = List.of(
            // rook movements
            new CastleContext(0x0001, Piece.R, "a1"),
            new CastleContext(0x0010, Piece.R, "h1"),
            new CastleContext(0x0100, Piece.r, "a8"),
            new CastleContext(0x1000, Piece.r, "h8"),
            // king movements
            new CastleContext(0x0001, Piece.K, "e1"),
            new CastleContext(0x0010, Piece.K, "e1"),
            new CastleContext(0x0100, Piece.k, "e8"),
            new CastleContext(0x1000, Piece.k, "e8")
    );

    public static int populateCastlingRights(int flags, Move move) {
        Piece movedPiece = move.getMovedPiece();
        if (!isRook(movedPiece) && !isKing(movedPiece)) {
            return flags;
        }
        int newFlags = flags;
        for (CastleContext ctx : CastleUtils.castleContexts) {
            if ((flags & ctx.getMask()) > 0) {
                String startingSquare = move.getStartingSquare();
                if (movedPiece == ctx.getExpectedPiece() && startingSquare.equals(ctx.getExpectedSquare())) {
                    newFlags = newFlags & ~ctx.getMask();
                }
            }
        }
        return newFlags;
    }
}
