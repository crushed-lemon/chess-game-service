package com.crushedlemon.chess.repositories;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import org.springframework.stereotype.Repository;

@Repository
public class FakeChessRepository implements ChessRepository {

    @Override
    public Game getGame(String gameId) {
        return Game.builder().build();
    }

    @Override
    public void saveGame(Game game) {
    }

    @Override
    public void saveMove(Move move, String moveName, Long moveTime) {

    }
}
