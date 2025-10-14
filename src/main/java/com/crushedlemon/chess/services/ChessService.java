package com.crushedlemon.chess.services;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.GamePreferences;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.dto.error.GameError;

import java.util.List;
import java.util.Optional;

public interface ChessService {

    List<GameError> playMove(Game game, Move move, String player);

    Optional<Game> getOngoingGame(String gameId);

    List<GameError> resign(Game game, String userName);

    Optional<GamePreferences> getOngoingLobbyRequest(String userName);

    Optional<String> getOngoingGameId(String userName);

    Optional<String> getConnectionId(String playerId);
}
