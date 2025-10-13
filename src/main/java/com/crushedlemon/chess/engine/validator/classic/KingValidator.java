package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.engine.movementRule.classic.KingClassicMovementRule;

public class KingValidator extends ClassicValidator {

    private static final KingValidator INSTANCE = new KingValidator();

    private KingValidator() {
        super(new KingClassicMovementRule());
    }

    static KingValidator getInstance() {
        return INSTANCE;
    }
}
