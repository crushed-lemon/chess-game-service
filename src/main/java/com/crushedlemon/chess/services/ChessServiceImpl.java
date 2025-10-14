package com.crushedlemon.chess.services;

import com.crushedlemon.chess.commons.model.*;
import com.crushedlemon.chess.dto.*;
import com.crushedlemon.chess.dto.error.ErrorCode;
import com.crushedlemon.chess.dto.error.GameError;
import com.crushedlemon.chess.engine.RuleEngine;
import com.crushedlemon.chess.engine.RuleEngineFactory;
import com.crushedlemon.chess.exception.InvalidMoveException;
import com.crushedlemon.chess.repositories.ChessRepository;
import com.crushedlemon.chess.validators.PlayerAuthorizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.crushedlemon.chess.utils.CommonUtils.isPawn;
import static com.crushedlemon.chess.utils.CommonUtils.opponentOf;

@Service
@Slf4j
public class ChessServiceImpl implements ChessService {

    @Autowired
    private ChessRepository chessRepository;

    @Autowired
    private PlayerAuthorizer playerAuthorizer;

    @Autowired
    private RuleEngineFactory ruleEngineFactory;

    // TODO : Get metrics about this API and restructure code/infra to minimize the latency of this API
    @Override
    public List<GameError> playMove(Game game, Move move, String player) {

        // Go through README in this package to know the steps to take per move

        log.info("Move played in game {} by {} : {}", game.getGameId(), player, move);

        Optional<GameError> authError = playerAuthorizer.isPlayerAuthorized(game, move, player);
        if (authError.isPresent()) {
            log.info("Player {} is not authorized to move in game {}", player, game.getGameId());
            return List.of(authError.get());
        }

        RuleEngine ruleEngine = ruleEngineFactory.getRuleEngine(game.getGameType());
        try {
            // Refactor this so that it returns proper errors
            PlayMoveOutput playMoveOutput = ruleEngine.playMove(new PlayMoveInput(game.getBoard(), move, game.getFlags()));
            GetMoveResultOutput getMoveResultOutput = ruleEngine.getMoveResult(new GetMoveResultInput(game.getBoard(), move));

            log.info("PlayMoveOutput and GetMoveResultOutput computed successfully");

            String moveName = buildMoveName(move, getMoveResultOutput.getMoveResults());
            Long moveTime = Instant.now().toEpochMilli();
            chessRepository.saveMove(game.getGameId(), move, moveName, moveTime);

            log.info("Move saved in repo");

            Game modifiedGame = modifyGame(game, playMoveOutput, getMoveResultOutput);
            chessRepository.saveGame(modifiedGame);
            log.info("Updated game state saved in repo");
        } catch (InvalidMoveException e) {
            log.info("Move was invalid");
            return List.of(new GameError(ErrorCode.ERROR_INVALID_MOVE, Map.of(
                    "playerId", player,
                    "gameId", game.getGameId(),
                    "movedPiece", move.getMovedPiece(),
                    "startingSquare", move.getStartingSquare(),
                    "endingSquare", move.getEndingSquare()
            )));
        } catch (Exception e) {
            log.error("Error in resigning", e);
            return List.of(new GameError(ErrorCode.ERROR_RUNTIME_EXCEPTION, Map.of("trace", e.getMessage())));
        }

        return List.of();
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
    public List<GameError> resign(Game game, String userName) {
        try {
            Game modifiedGame = game.toBuilder()
                    .gameState(GameState.ENDED)
                    .gameResult(GameResult.GAME_RESULT_RESIGNED)
                    .winnerId(opponentOf(userName, game)).build();
            chessRepository.saveGame(modifiedGame);
        } catch (Exception e) {
            log.error("Error in resigning", e);
            return List.of(new GameError(ErrorCode.ERROR_RUNTIME_EXCEPTION, Map.of("trace", e.getMessage())));
        }
        return List.of();
    }

    @Override
    public Optional<GamePreferences> getOngoingLobbyRequest(String userName) {
        return chessRepository.getLobby(userName);
    }

    @Override
    public Optional<String> getOngoingGameId(String userName) {
        return chessRepository.getGameId(userName);
    }

    @Override
    public Optional<String> getConnectionId(String playerId) {
        return chessRepository.getConnectionId(playerId);
    }

    private Game modifyGame(Game game, PlayMoveOutput playMoveOutput, GetMoveResultOutput getMoveResultOutput) {
        // TODO : Incorporate result of getMoveResultOutput to fill in game state and winner fields
        return game.toBuilder()
                .board(playMoveOutput.getBoard())
                .flags(playMoveOutput.getFlags())
                .currentPlayer(1 - game.getCurrentPlayer())
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
