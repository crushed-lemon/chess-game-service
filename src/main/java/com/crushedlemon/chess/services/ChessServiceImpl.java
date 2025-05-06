package com.crushedlemon.chess.services;

import com.crushedlemon.chess.analyzer.GameAnalyzer;
import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.enums.MoveResult;
import com.crushedlemon.chess.enums.OperationStatus;
import com.crushedlemon.chess.repositories.ChessRepository;
import com.crushedlemon.chess.validators.MoveValidator;
import com.crushedlemon.chess.validators.PlayerAuthorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChessServiceImpl implements ChessService {

    private static final List<MoveResult> TERMINAL_MOVE_RESULTS = List.of(MoveResult.CHECKMATE, MoveResult.STALEMATE);

    @Autowired
    private ChessRepository chessRepository;

    @Autowired
    private PlayerAuthorizer playerAuthorizer;

    @Autowired
    private MoveValidator moveValidator;

    @Autowired
    private GameAnalyzer gameAnalyzer;

    @Override
    public OperationStatus playMove(String gameId, Move move, String player) {
        /*
            Steps to take per move
            1. Get the game from gameId
            2. Authenticate the move
                a) The player is playing the game, and it is their turn
                b) The player has moved their own piece
            3. Validate the move
                a) Piece-wise location validation
                b) Assert no pieces were in between
                c) Assert not capturing own piece
                d) Assert doesn't move king into check
                e) If castles or en-passant, assert that it was allowed
            4. Determine impact of the move
                a) Nothing
                b) Capture
                c) Check
                d) Checkmate
                e) Stalemate
            5. Give a name and timestamp to the move
            6. Register the move in moves db
            7. Calculate and save the state of the game
            8. Notify the other player
         */

        Game game = chessRepository.getGame(gameId);

        boolean isAuthorized = playerAuthorizer.isPlayerAuthorized(game, move, player);
        if (!isAuthorized) {
            return OperationStatus.FAILED_UNAUTHORIZED;
        }

        boolean isValidMove = moveValidator.isMoveValid(game, move);
        if (!isValidMove) {
            return OperationStatus.FAILED_INVALID_MOVE;
        }

        List<MoveResult> moveResults = gameAnalyzer.getMoveResults(game, move);

        String moveName = buildMoveName(move, moveResults);
        Long moveTime = Instant.now().toEpochMilli();

        chessRepository.saveMove(move, moveName, moveTime);

        boolean isGameEnded = TERMINAL_MOVE_RESULTS.stream().anyMatch(moveResults::contains);
        Game modifiedGame = applyMove(game, move, isGameEnded);
        chessRepository.saveGame(modifiedGame);
        return OperationStatus.SUCCESS;
    }

    private Game applyMove(Game game, Move move, boolean isGameEnded) {
        return game;
    }

    private String buildMoveName(Move move, List<MoveResult> moveResults) {
        return "exc5";
    }
}
