package com.crushedlemon.chess.engine.movementRule.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;

import static com.crushedlemon.chess.utils.CommonUtils.getFile;
import static com.crushedlemon.chess.utils.CommonUtils.getRank;

public class KingClassicMovementRule implements ClassicMovementRule {

    @Override
    public boolean isPieceMovementAllowed(Board board, Move move) {
        String startingSquare = move.getStartingSquare();
        int startingRank = getRank(startingSquare);
        int startingFile = getFile(startingSquare);

        String endingSquare = move.getEndingSquare();
        int endingRank = getRank(endingSquare);
        int endingFile = getFile(endingSquare);

        return Math.abs(endingFile - startingFile) <= 1 && Math.abs(endingRank - startingRank) <= 1;
    }
}
