package com.crushedlemon.chess.repositories;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.crushedlemon.chess.commons.model.*;
import com.crushedlemon.chess.exception.CorruptedDataException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class ChessRepositoryImpl implements ChessRepository {

    private static final Logger logger = LoggerFactory.getLogger(ChessRepositoryImpl.class);

    @Autowired
    private DynamoDB dynamoDB;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Game getGame(String gameId) {
        Table chessGamesTable = dynamoDB.getTable("chess-games");
        Item item = chessGamesTable.getItem("gameId", gameId);

        if(Objects.isNull(item)) {
            return null;
        }

        String boardPieces = (String) item.get("board");
        Integer gameDurationInt = ((BigDecimal) item.get("gameDuration")).intValueExact();
        Integer incrementPerMoveInt = ((BigDecimal) item.get("incrementPerMove")).intValueExact();

        GameDuration gameDuration = fromGameDurationValue(gameDurationInt);
        IncrementPerMove incrementPerMove = fromIncrementValue(incrementPerMoveInt);

        return Game.builder()
                .gameId(gameId)
                .board(new Board(boardPieces))
                .gameSettings(GameSettings.builder().gameDuration(gameDuration).incrementPerMove(incrementPerMove).build())
                .blackPlayerId((String) item.get("blackUser"))
                .whitePlayerId((String) item.get("whiteUser"))
                .currentPlayer(item.getInt("currentPlayer"))
                .startTime(((BigDecimal) item.get("startTime")).longValueExact())
                .flags(((BigDecimal) item.get("flags")).intValueExact())
                .gameType(GameType.valueOf((String) item.get("gameType")))
                .gameState(GameState.valueOf((String) item.get("gameState")))
                .gameResult(GameResult.valueOf((String) item.get("gameResult")))
                .winnerId((String) item.get("winnerId"))
                .build();
    }

    @Override
    public void saveGame(Game game) {
        Table gamesTable = dynamoDB.getTable("chess-games");
        gamesTable.updateItem("gameId", game.getGameId(),
                "SET #board = :board, #flags = :flags, #currentPlayer = :currentPlayer, #gameState = :gameState, #gameResult = :gameResult, #winnerId = :winnerId",
                Map.of(
                "#board", "board",
                "#flags", "flags",
                "#currentPlayer", "currentPlayer",
                "#gameState", "gameState",
                "#gameResult", "gameResult",
                "#winnerId", "winnerId"),
                Map.of(
                ":board", game.getBoard().getPieces(),
                ":flags", game.getFlags(),
                ":currentPlayer", game.getCurrentPlayer(),
                ":gameState", game.getGameState().toString(),
                ":gameResult", game.getGameResult().toString(),
                ":winnerId", game.getWinnerId()));
    }

    @Override
    public void saveMove(String gameId, Move move, String moveName, Long moveTime) {
        Table movesTable = dynamoDB.getTable("chess-game-moves");
        Map<String, Object> mp = Map.of(
                "gameId", gameId,
                "moveTime", moveTime,
                "movedPiece", move.getMovedPiece().toString(),
                "startingSquare", move.getStartingSquare(),
                "endingSquare", move.getEndingSquare(),
                "moveName", moveName);
        movesTable.putItem(Item.fromMap(mp));
    }

    @Override
    public Optional<String> getConnectionId(String playerId) {
        Table connectionsTable = dynamoDB.getTable("chess-connections");
        Item item = connectionsTable.getItem("userId", playerId);
        if(Objects.isNull(item)) {
            return Optional.empty();
        }
        return Optional.of((String) item.get("connectionId"));
    }

    @Override
    public Optional<String> getGameId(String userName) {
        Table gamesTable = dynamoDB.getTable("chess-games");
        QuerySpec q = new QuerySpec()
                .withKeyConditionExpression("whiteUser = :w AND gameState = :s")
                .withValueMap(new ValueMap()
                        .withString(":w", userName)
                        .withString(":s", "ONGOING")
                );
        Iterator<Item> items = gamesTable.getIndex("whiteUser-gameState-index").query(q).iterator();

        if(items.hasNext()) {
            return Optional.of(items.next().getString("gameId"));
        }

        // Try with black
        QuerySpec bq = new QuerySpec()
                .withKeyConditionExpression("blackUser = :b AND gameState = :s")
                .withValueMap(new ValueMap()
                        .withString(":b", userName)
                        .withString(":s", "ONGOING")
                );
        Iterator<Item> bItems = gamesTable.getIndex("blackUser-gameState-index").query(bq).iterator();

        if(bItems.hasNext()) {
            return Optional.of(bItems.next().getString("gameId"));
        }

        return Optional.empty();
    }

    @Override
    public Optional<GamePreferences> getLobby(String userName) {
        Table lobbyTable = dynamoDB.getTable("chess-lobby");
        QuerySpec q = new QuerySpec()
                .withKeyConditionExpression("userId = :u")
                .withValueMap(new ValueMap()
                        .withString(":u", userName)
                );
        Iterator<Item> items = lobbyTable.getIndex("userId-index").query(q).iterator();

        if(items.hasNext()) {
            Item item = items.next();
            Integer gameDurationInt = ((BigDecimal) item.get("gameDuration")).intValueExact();
            Integer incrementPerMoveInt = ((BigDecimal) item.get("incrementPerMove")).intValueExact();
            String playAs = item.getString("playAs");

            GameDuration gameDuration = fromGameDurationValue(gameDurationInt);
            IncrementPerMove incrementPerMove = fromIncrementValue(incrementPerMoveInt);

            return Optional.of(
                    GamePreferences.builder()
                            .gameDuration(gameDuration)
                            .incrementPerMove(incrementPerMove)
                            .playAs(PlayAs.valueOf(playAs))
                            .build()
            );
        }

        return Optional.empty();
    }

    private GameDuration fromGameDurationValue(Integer gameDurationInt) {
        return Arrays.stream(GameDuration.values())
                .filter(jd -> jd.getDurationInSeconds() == gameDurationInt)
                .findAny()
                .orElseThrow(() -> new CorruptedDataException("DB's game duration data cannot be mapped"));
    }

    private IncrementPerMove fromIncrementValue(Integer incrementInt) {
        return Arrays.stream(IncrementPerMove.values())
                .filter(ipm -> ipm.getIncrementInSeconds() == incrementInt)
                .findAny()
                .orElseThrow(() -> new CorruptedDataException("DB's increment per move data cannot be mapped"));
    }
}
