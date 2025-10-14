package com.crushedlemon.chess.dto.error;

import com.crushedlemon.chess.commons.model.Color;

import java.util.Map;

public class GameErrorFactory {
    public static GameError errorUnauthorizedIncorrectColor(String playerId, String gameId, Color currentColor, Color movedPieceColor) {
        return new GameError(
                        ErrorCode.ERROR_UNAUTHORIZED_INCORRECT_PIECE_MOVED,
                        Map.of(
                                "playerId", playerId,
                                "gameId", gameId,
                                "ownColor", currentColor,
                                "movedColor", movedPieceColor
                        )
        );
    }

    public static GameError errorUnauthorizedNotCurrentPlayer(String player, String gameId, String currentPlayer) {
        return new GameError(
                        ErrorCode.ERROR_UNAUTHORIZED_NOT_CURRENT_PLAYER,
                        Map.of(
                                "playerId", player,
                                "gameId", gameId,
                                "currentPlayerId", currentPlayer
                        )
        );
    }

    public static GameError errorUnauthorizedNotInGame(String player, String gameId) {
        return new GameError(
                        ErrorCode.ERROR_UNAUTHORIZED_NOT_IN_GAME,
                        Map.of(
                                "playerId", player,
                                "gameId", gameId
                        )
        );
    }

    public static GameError errorInvalidGame(String gameId) {
        return new GameError(
                ErrorCode.ERROR_INVALID_GAME,
                Map.of(
                        "gameId", gameId
                )
        );
    }
}
