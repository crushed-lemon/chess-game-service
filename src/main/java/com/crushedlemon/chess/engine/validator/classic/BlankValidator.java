package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.engine.movementRule.classic.CompositeMovementRule;

import java.util.List;

public class BlankValidator extends ClassicValidator {

    private static final BlankValidator INSTANCE = new BlankValidator();

    private BlankValidator() {
        super(new CompositeMovementRule(List.of()));
    }

    static BlankValidator getInstance() {
        return INSTANCE;
    }
}
