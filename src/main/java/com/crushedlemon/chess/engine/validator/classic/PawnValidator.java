package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.engine.movementRule.classic.PawnClassicMovementRule;

public class PawnValidator extends ClassicValidator {

    private final static PawnValidator INSTANCE = new PawnValidator();

    private PawnValidator() {
        super(new PawnClassicMovementRule());
    }

    static PawnValidator getInstance() {
        return INSTANCE;
    }
}
