package com.crushedlemon.chess.engine.movementRule.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;
import lombok.NoArgsConstructor;

import static com.crushedlemon.chess.utils.CommonUtils.getFile;
import static com.crushedlemon.chess.utils.CommonUtils.getRank;

@NoArgsConstructor
public class BishopClassicMovementRule implements ClassicMovementRule {

    @Override
    public boolean isPieceMovementAllowed(Board board, Move move) {
        String startingSquare = move.getStartingSquare();
        int startingRank = getRank(startingSquare);
        int startingFile = getFile(startingSquare);

        String endingSquare = move.getEndingSquare();
        int endingRank = getRank(endingSquare);
        int endingFile = getFile(endingSquare);

        if(Math.abs(endingRank - startingRank) != Math.abs(startingFile - endingFile)) {
            return false;
        }

        int steps = Math.abs(endingRank - startingRank);
        int xDirection = endingFile - startingFile > 0 ? 1 : -1;
        int yDirection = endingRank - startingRank > 0 ? 1 : -1;

        for(int i = 1; i < steps; i++) {
            int file = startingFile + (i * xDirection);
            int rank = startingRank + (i * yDirection);
            Piece piece = board.getPieceAt(file, rank);
            if(!piece.equals(Piece.X)) {
                return false;
            }
        }
        return true;
    }
}
