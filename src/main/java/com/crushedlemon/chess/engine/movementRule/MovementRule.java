package com.crushedlemon.chess.engine.movementRule;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;

public interface MovementRule {

    boolean isPieceMovementAllowed(Board board, Move move);

}
