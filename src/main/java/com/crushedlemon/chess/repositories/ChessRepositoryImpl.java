package com.crushedlemon.chess.repositories;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.crushedlemon.chess.commons.model.*;
import com.crushedlemon.chess.exception.CorruptedDataException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;

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
                .blackConnectionId((String) item.get("blackConnectionId"))
                .whiteConnectionId((String) item.get("whiteConnectionId"))
                .gameState(GameState.valueOf((String) item.get("gameState")))
                .startTime(((BigDecimal) item.get("startTime")).longValueExact())
                .flags(((BigDecimal) item.get("flags")).intValueExact())
                .gameType(GameType.valueOf((String) item.get("gameType")))
                .gameResult(GameResult.valueOf((String) item.get("gameResult")))
                .winnerId((String) item.get("winnerId"))
                .build();
    }

    @Override
    public void saveGame(Game game) {
        logger.atInfo().log("Game is being saved now!");
    }

    @Override
    public void saveMove(Move move, String moveName, Long moveTime) {
        logger.atInfo().log("Move is being saved now!");
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
