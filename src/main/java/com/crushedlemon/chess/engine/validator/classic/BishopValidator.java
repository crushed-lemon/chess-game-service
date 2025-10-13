package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.engine.movementRule.classic.BishopClassicMovementRule;

public class BishopValidator extends ClassicValidator {

    private static final BishopValidator INSTANCE = new BishopValidator();

    private BishopValidator() {
        super(new BishopClassicMovementRule());
    }

    static BishopValidator getInstance() {
        return INSTANCE;
    }
}

