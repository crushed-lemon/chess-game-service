package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.engine.movementRule.classic.BishopClassicMovementRule;
import com.crushedlemon.chess.engine.movementRule.classic.CompositeMovementRule;
import com.crushedlemon.chess.engine.movementRule.classic.RookClassicMovementRule;

import java.util.List;

public class QueenValidator extends ClassicValidator {

    private static final QueenValidator INSTANCE = new QueenValidator();

    private QueenValidator() {
        super(new CompositeMovementRule(
                List.of(new BishopClassicMovementRule(), new RookClassicMovementRule())
        ));
    }

    static QueenValidator getInstance() {
        return INSTANCE;
    }
}
