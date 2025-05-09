package com.crushedlemon.chess.services;

import com.crushedlemon.chess.engine.RuleEngine;
import com.crushedlemon.chess.engine.RuleEngineFactory;
import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static com.crushedlemon.chess.utils.CommonUtils.isPawn;

@Service
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

        Game game = chessRepository.getGame(gameId);

        boolean isAuthorized = playerAuthorizer.isPlayerAuthorized(game, move, player);
        if (!isAuthorized) {
            return OperationStatus.FAILED_UNAUTHORIZED;
        }

        RuleEngine ruleEngine = ruleEngineFactory.getRuleEngine(game.getGameType());
        OperationStatus operationStatus = OperationStatus.SUCCESS;
        try {
            PlayMoveOutput playMoveOutput = ruleEngine.playMove(new PlayMoveInput(game.getBoard(), move, game.getFlags()));
            GetMoveResultOutput getMoveResultOutput = ruleEngine.getMoveResult(new GetMoveResultInput(game.getBoard(), move));

            String moveName = buildMoveName(move, getMoveResultOutput.getMoveResults());
            Long moveTime = Instant.now().toEpochMilli();
            chessRepository.saveMove(gameId, move, moveName, moveTime);

            Game modifiedGame = modifyGame(game, playMoveOutput, getMoveResultOutput);
            chessRepository.saveGame(modifiedGame);
            sendCommunications(game, move, player, "success");
        } catch (InvalidMoveException e) {
            operationStatus = OperationStatus.FAILED_INVALID_MOVE;
            sendCommunications(game, move, player, "failure");
        }

        return operationStatus;
    }

    // TODO : Move these methods out of this class. Also, make these comms asynchronous.
    private void sendCommunications(Game game, Move move, String player, String status) {
        String selfConnectionId = player.equals(game.getWhitePlayerId()) ? game.getWhiteConnectionId() : game.getBlackConnectionId();
        String opponentConnectionId = player.equals(game.getWhitePlayerId()) ? game.getBlackConnectionId() : game.getWhiteConnectionId();
        String message = getMoveMessage(move, status);
        CommsUtil.communicateToClient(selfConnectionId, message);
        if (status.equals("success")) {
            CommsUtil.communicateToClient(opponentConnectionId, message);
        }
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
}
