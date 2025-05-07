package com.crushedlemon.chess.repositories;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;

public interface ChessRepository {

    Game getGame(String gameId);

    void saveGame(Game game);

    void saveMove(String gameId, Move move, String moveName, Long moveTime);
}
