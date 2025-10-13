package com.crushedlemon.chess.engine.validator;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;

public interface MoveValidator {

    boolean isMoveValid(Board board, Move move);

}
