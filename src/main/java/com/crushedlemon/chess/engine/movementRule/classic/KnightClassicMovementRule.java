package com.crushedlemon.chess.engine.movementRule.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Color;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;
import lombok.NoArgsConstructor;

import static com.crushedlemon.chess.utils.CommonUtils.*;

@NoArgsConstructor
public class KnightClassicMovementRule implements ClassicMovementRule {

    @Override
    public boolean isPieceMovementAllowed(Board board, Move move) {
        String startingSquare = move.getStartingSquare();

        String endingSquare = move.getEndingSquare();

        if(!isValidSquareForKnight(startingSquare, endingSquare)) {
            return false;
        }

        return isSquareEmpty(board, endingSquare) || isOpponentAttacked(board, startingSquare, endingSquare);
    }

    private boolean isValidSquareForKnight(String startingSquare, String endingSquare) {

        int endingRank = getRank(endingSquare);
        int endingFile = getFile(endingSquare);

        int startingRank = getRank(startingSquare);
        int startingFile = getFile(startingSquare);

        return (Math.abs(endingFile - startingFile) == 2 && Math.abs(endingRank - startingRank) == 1)
            || (Math.abs(endingFile - startingFile) == 1 && Math.abs(endingRank - startingRank) == 2);
    }

    private boolean isOpponentAttacked(Board board, String startingSquare, String endingSquare) {
        Color opponentColor = getColor(board.getPieceAt(endingSquare));
        Color selfColor = getColor(board.getPieceAt(startingSquare));
        return !selfColor.equals(opponentColor);
    }

    private boolean isSquareEmpty(Board board, String endingSquare) {
        return board.getPieceAt(endingSquare).equals(Piece.X);
    }
}
