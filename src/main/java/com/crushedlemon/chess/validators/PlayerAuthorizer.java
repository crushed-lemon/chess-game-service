package com.crushedlemon.chess.validators;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import org.springframework.stereotype.Component;

@Component
public class PlayerAuthorizer {

    public boolean isPlayerAuthorized(Game game, Move move, String player) {
        /*
        Authorize the move
            a) The player is playing the game, and it is their turn
            b) The player has moved their own piece
        */
        return true;
    }

}
