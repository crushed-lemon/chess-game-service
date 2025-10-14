package com.crushedlemon.chess.engine.movementRule.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;

import java.util.List;

public class CompositeMovementRule implements ClassicMovementRule {

    List<ClassicMovementRule> movementRules;

    public CompositeMovementRule(List<ClassicMovementRule> movementRules) {
        this.movementRules = movementRules;
    }

    @Override
    public boolean isPieceMovementAllowed(Board board, Move move) {
        for(ClassicMovementRule movementRule : movementRules) {
            if(movementRule.isPieceMovementAllowed(board, move)) {
                return true;
            }
        }
        return false;
    }
}
