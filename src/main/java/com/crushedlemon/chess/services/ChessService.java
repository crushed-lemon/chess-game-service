package com.crushedlemon.chess.services;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.GamePreferences;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.enums.OperationStatus;

import java.util.Optional;

public interface ChessService {

    OperationStatus playMove(String gameId, Move move, String player);

    Optional<Game> getOngoingGame(String gameId);

    void resign(String gameId, String userName);

    Optional<GamePreferences> getOngoingLobbyRequest(String userName);

    Optional<String> getOngoingGameId(String userName);
}
