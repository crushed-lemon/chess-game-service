package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.engine.movementRule.classic.KnightClassicMovementRule;

public class KnightValidator extends ClassicValidator {

    private static final KnightValidator INSTANCE = new KnightValidator();

    private KnightValidator() {
        super(new KnightClassicMovementRule());
    }

    static KnightValidator getInstance() {
        return INSTANCE;
    }
}
