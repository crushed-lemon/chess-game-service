package com.crushedlemon.chess.repositories;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.GamePreferences;
import com.crushedlemon.chess.commons.model.Move;

import java.util.Optional;

public class FakeChessRepository implements ChessRepository {

    @Override
    public Game getGame(String gameId) {
        return Game.builder().build();
    }

    @Override
    public void saveGame(Game game) {
    }

    @Override
    public void saveMove(String gameId, Move move, String moveName, Long moveTime) {

    }

    @Override
    public Optional<String> getConnectionId(String playerId) {
        return Optional.of("conn-id");
    }

    @Override
    public Optional<String> getGameId(String userName) {
        return Optional.empty();
    }

    @Override
    public Optional<GamePreferences> getLobby(String userName) {
        return Optional.empty();
    }
}
