package com.crushedlemon.chess.validators;

import com.crushedlemon.chess.commons.model.Color;
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
        Color currentColor = getCurrentColor(game);
        String currentPlayer = getCurrentPlayer(game, currentColor);
        if(!player.equals(currentPlayer)) {
            return false;
        }
        Color movedPieceColor = getMovedPieceColor(move);
        if(!movedPieceColor.equals(currentColor)) {
            return false;
        }
        return true;
    }

    private static Color getMovedPieceColor(Move move) {
        String movedPieceString = move.getMovedPiece().toString();
        return movedPieceString.toLowerCase().equals(movedPieceString) ? Color.B : Color.W;
    }

    private static String getCurrentPlayer(Game game, Color currentColor) {
        return currentColor.equals(Color.B) ? game.getBlackPlayerId() : game.getWhitePlayerId();
    }

    private static Color getCurrentColor(Game game) {
        return game.getCurrentPlayer() == 0 ? Color.B : Color.W;
    }
}
