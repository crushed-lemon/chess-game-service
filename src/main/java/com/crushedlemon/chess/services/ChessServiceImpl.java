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
import com.crushedlemon.chess.validators.PlayerAuthorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChessServiceImpl implements ChessService {

    private static final Logger logger = LoggerFactory.getLogger(ChessServiceImpl.class);

    @Autowired
    private ChessRepository chessRepository;

    @Autowired
    private PlayerAuthorizer playerAuthorizer;

    @Autowired
    private RuleEngineFactory ruleEngineFactory;

    @Override
    public OperationStatus playMove(String gameId, Move move, String player) {

        // Go through README in this package to know the steps to take per move

        Game game = chessRepository.getGame(gameId);

        logger.atInfo().log("Game Id : " + game.getGameId());
        logger.atInfo().log("Game : " + game);

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

            Game modifiedGame = modifyGame(game, move, playMoveOutput, getMoveResultOutput);
            chessRepository.saveGame(modifiedGame);
        } catch (InvalidMoveException e) {
            operationStatus = OperationStatus.FAILED_INVALID_MOVE;
        }

        return operationStatus;
    }

    private Game modifyGame(Game game, Move move, PlayMoveOutput playMoveOutput, GetMoveResultOutput getMoveResultOutput) {
        return game;
    }

    private String buildMoveName(Move move, List<MoveResult> moveResults) {
        return "exc5";
    }
}
