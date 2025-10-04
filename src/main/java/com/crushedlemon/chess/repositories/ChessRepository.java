package com.crushedlemon.chess.repositories;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;

import java.util.Optional;

public interface ChessRepository {

    Game getGame(String gameId);

    void saveGame(Game game);

    void saveMove(String gameId, Move move, String moveName, Long moveTime);

    Optional<String> getConnectionId(String playerId);
}
