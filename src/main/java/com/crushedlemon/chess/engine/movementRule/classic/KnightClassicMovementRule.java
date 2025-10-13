package com.crushedlemon.chess.engine.movementRule.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class KnightClassicMovementRule implements ClassicMovementRule {

    // TODO : Implement
    @Override
    public boolean isPieceMovementAllowed(Board board, Move move) {
        return true;
    }
}
