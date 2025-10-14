package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.commons.model.Piece;
import com.crushedlemon.chess.engine.validator.MoveValidator;
import com.crushedlemon.chess.exception.ValidatorInstantiationException;

import java.util.Map;

public class ValidatorFactory {

    private final static Map<String, MoveValidator> validatorMap = Map.of(
            "b", BishopValidator.getInstance(),
            "n", KnightValidator.getInstance(),
            "r", RookValidator.getInstance(),
            "q", QueenValidator.getInstance(),
            "k", KingValidator.getInstance(),
            "p", PawnValidator.getInstance()
    );

    public static MoveValidator getMoveValidatorForPiece(Piece piece) {
        String key = piece.toString().toLowerCase();
        if(validatorMap.containsKey(key)) {
            return validatorMap.get(key);
        }
        throw new ValidatorInstantiationException(String.format("Cannot instantiate validator for piece %s", piece));
    }
}
