package com.crushedlemon.chess.engine.movementRule.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;
import lombok.NoArgsConstructor;

import static com.crushedlemon.chess.utils.CommonUtils.getFile;
import static com.crushedlemon.chess.utils.CommonUtils.getRank;

@NoArgsConstructor
public class RookClassicMovementRule implements ClassicMovementRule {

    @Override
    public boolean isPieceMovementAllowed(Board board, Move move) {
        String startingSquare = move.getStartingSquare();
        int startingRank = getRank(startingSquare);
        int startingFile = getFile(startingSquare);

        String endingSquare = move.getEndingSquare();
        int endingRank = getRank(endingSquare);
        int endingFile = getFile(endingSquare);

        if(startingRank != endingRank && startingFile != endingFile) {
            return false;
        }

        int xSteps = Math.abs(endingFile - startingFile);
        int xDirection = endingFile - startingFile > 0 ? 1 : -1;
        for(int i = 1; i < xSteps; i++) {
            int file = startingFile + (i * xDirection);
            if(!board.getPieceAt(file, startingRank).equals(Piece.X)) {
                return false;
            }
        }
        int ySteps = Math.abs(endingRank - startingRank);
        int yDirection = endingRank - startingRank > 0 ? 1 : -1;
        for(int i = 1; i < ySteps; i++) {
            int rank = startingRank + (i * yDirection);
            if(!board.getPieceAt(startingFile, rank).equals(Piece.X)) {
                return false;
            }
        }
        return true;
    }
}
