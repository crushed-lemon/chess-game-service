package com.crushedlemon.chess.validators;

import com.crushedlemon.chess.commons.model.Color;
import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.dto.error.GameError;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.crushedlemon.chess.dto.error.GameErrorFactory.*;

@Component
public class PlayerAuthorizer {

    public Optional<GameError> isPlayerAuthorized(Game game, Move move, String player) {

        if(!game.getWhitePlayerId().equals(player) && !game.getBlackPlayerId().equals(player)) {
            return Optional.of(errorUnauthorizedNotInGame(player, game.getGameId()));
        }

        Color currentColor = getCurrentColor(game);
        String currentPlayer = getCurrentPlayer(game, currentColor);
        if(!player.equals(currentPlayer)) {
            return Optional.of(errorUnauthorizedNotCurrentPlayer(player, game.getGameId(), currentPlayer));
        }
        Color movedPieceColor = getMovedPieceColor(move);
        if(!movedPieceColor.equals(currentColor)) {
            return Optional.of(errorUnauthorizedIncorrectColor(player, game.getGameId(), currentColor, movedPieceColor));
        }
        return Optional.empty();
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
