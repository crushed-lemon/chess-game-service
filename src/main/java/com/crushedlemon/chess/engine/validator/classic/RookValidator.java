package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.engine.movementRule.classic.RookClassicMovementRule;

public class RookValidator extends ClassicValidator {

    private static final RookValidator INSTANCE = new RookValidator();

    private RookValidator() {
        super(new RookClassicMovementRule());
    }

    static RookValidator getInstance() {
        return INSTANCE;
    }
}
