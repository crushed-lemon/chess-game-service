package com.crushedlemon.chess.services;

import com.crushedlemon.chess.commons.model.*;
import com.crushedlemon.chess.engine.RuleEngine;
import com.crushedlemon.chess.engine.RuleEngineFactory;
import com.crushedlemon.chess.dto.GetMoveResultInput;
import com.crushedlemon.chess.dto.GetMoveResultOutput;
import com.crushedlemon.chess.dto.PlayMoveInput;
import com.crushedlemon.chess.dto.PlayMoveOutput;
import com.crushedlemon.chess.enums.MoveResult;
import com.crushedlemon.chess.enums.OperationStatus;
import com.crushedlemon.chess.exception.InvalidMoveException;
import com.crushedlemon.chess.repositories.ChessRepository;
import com.crushedlemon.chess.utils.CommsUtil;
import com.crushedlemon.chess.validators.PlayerAuthorizer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.crushedlemon.chess.utils.CommonUtils.isPawn;

@Service
@Slf4j
public class ChessServiceImpl implements ChessService {

    private static final Logger logger = LoggerFactory.getLogger(ChessServiceImpl.class);

    @Autowired
    private ChessRepository chessRepository;

    @Autowired
    private PlayerAuthorizer playerAuthorizer;

    @Autowired
    private RuleEngineFactory ruleEngineFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public OperationStatus playMove(String gameId, Move move, String player) {

        // Go through README in this package to know the steps to take per move

        log.info("Move played in game {} by {} : {}", gameId, player, move);

        Game game = chessRepository.getGame(gameId);
        if(game == null) {
            return OperationStatus.FAILED_INVALID_MOVE;
        }

        boolean isAuthorized = playerAuthorizer.isPlayerAuthorized(game, move, player);
        if (!isAuthorized) {
            log.info("Player {} is not authorized to move in game {}", player, gameId);
            return OperationStatus.FAILED_UNAUTHORIZED;
        }

        RuleEngine ruleEngine = ruleEngineFactory.getRuleEngine(game.getGameType());
        OperationStatus operationStatus = OperationStatus.SUCCESS;
        try {
            PlayMoveOutput playMoveOutput = ruleEngine.playMove(new PlayMoveInput(game.getBoard(), move, game.getFlags()));
            GetMoveResultOutput getMoveResultOutput = ruleEngine.getMoveResult(new GetMoveResultInput(game.getBoard(), move));

            log.info("PlayMoveOutput and GetMoveResultOutput computed successfully");

            String moveName = buildMoveName(move, getMoveResultOutput.getMoveResults());
            Long moveTime = Instant.now().toEpochMilli();
            chessRepository.saveMove(gameId, move, moveName, moveTime);

            log.info("Move saved in repo");

            Game modifiedGame = modifyGame(game, playMoveOutput, getMoveResultOutput);
            chessRepository.saveGame(modifiedGame);
            log.info("Updated game state saved in repo");

            sendCommunications(game, move, player, "success");
            log.info("Communications sent");
        } catch (InvalidMoveException e) {
            operationStatus = OperationStatus.FAILED_INVALID_MOVE;
            sendCommunications(game, move, player, "failure");
        }

        return operationStatus;
    }

    @Override
    public Optional<Game> getOngoingGame(String gameId) {
        Game game = chessRepository.getGame(gameId);
        if(game == null) {
            return Optional.empty();
        } else {
            return Optional.of(game);
        }
    }

    @Override
    public void resign(String gameId, String userName) {
        Game game = chessRepository.getGame(gameId);
        Game modifiedGame = game.toBuilder()
                .gameState(GameState.ENDED)
                .gameResult(GameResult.GAME_RESULT_RESIGNED)
                .winnerId(opponentOf(userName, game)).build();
        chessRepository.saveGame(modifiedGame);
        sendResignationCommunications(game, userName);
    }

    @Override
    public Optional<GamePreferences> getOngoingLobbyRequest(String userName) {
        return chessRepository.getLobby(userName);
    }

    @Override
    public Optional<String> getOngoingGameId(String userName) {
        return chessRepository.getGameId(userName);
    }

    // TODO : Move these methods out of this class. Also, make these comms asynchronous.
    private void sendCommunications(Game game, Move move, String player, String status) {
        Optional<String> selfConnectionId = chessRepository.getConnectionId(player);
        String opponent = opponentOf(player, game);
        Optional<String> opponentConnectionId = chessRepository.getConnectionId(opponent);

        String message = getMoveMessage(move, status);

        selfConnectionId.ifPresent(s -> CommsUtil.communicateToClient(s, message));
        if (status.equals("success")) {
            opponentConnectionId.ifPresent(s -> CommsUtil.communicateToClient(s, message));
        }
    }

    private void sendResignationCommunications(Game game, String resigningPlayer) {
        Optional<String> selfConnectionId = chessRepository.getConnectionId(resigningPlayer);
        String opponent = opponentOf(resigningPlayer, game);
        Optional<String> opponentConnectionId = chessRepository.getConnectionId(opponent);

        String message = getResignationMessage(resigningPlayer);

        selfConnectionId.ifPresent(s -> CommsUtil.communicateToClient(s, message));
        opponentConnectionId.ifPresent(s -> CommsUtil.communicateToClient(s, message));
    }

    private String getMoveMessage(Move move, String status) {
        try {
            String msg = status.equals("success") ? "" : status;
            return objectMapper.writeValueAsString(Map.of(
                    "action", "selfPlayedMove",
                    "error", msg,
                    "source", move.getStartingSquare(),
                    "destination", move.getEndingSquare(),
                    "piece", move.getMovedPiece().toString()
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getResignationMessage(String resigningPlayer) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "action", "resigned",
                    "resigningPlayer", resigningPlayer
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Game modifyGame(Game game, PlayMoveOutput playMoveOutput, GetMoveResultOutput getMoveResultOutput) {
        // TODO : Incorporate result of getMoveResultOutput to fill in game state and winner fields
        return game.toBuilder()
                .board(playMoveOutput.getBoard())
                .flags(playMoveOutput.getFlags())
                .build();
    }

    private String buildMoveName(Move move, List<MoveResult> moveResults) {
        // TODO : Implement this method to handle checks, checkmates, captures, and disambiguation
        StringBuilder moveNameBuilder = new StringBuilder();
        String firstChar = move.getMovedPiece().toString().toUpperCase();
        if (isPawn(move.getMovedPiece())) {
            firstChar = "";
        }
        moveNameBuilder.append(firstChar);
        moveNameBuilder.append(move.getEndingSquare());
        return moveNameBuilder.toString();
    }

    private String opponentOf(String userName, Game game) {
        return game.getBlackPlayerId().equals(userName) ? game.getWhitePlayerId() : game.getBlackPlayerId();
    }
}
