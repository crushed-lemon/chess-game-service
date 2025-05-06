package com.crushedlemon.chess.validators;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import org.springframework.stereotype.Component;

@Component
public class MoveValidator {

    public boolean isMoveValid(Game game, Move move) {
        /*
        Validate the move
            a) Piece-wise location validation
            b) Assert no pieces were in between
            c) Assert not capturing own piece
            d) Assert doesn't move king into check
            e) If castles or en-passant, assert that it was allowed
        */
        return true;
    }
}
