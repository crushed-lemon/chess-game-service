package com.crushedlemon.chess.controllers;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.dto.error.GameError;
import com.crushedlemon.chess.dto.error.GameErrorFactory;
import com.crushedlemon.chess.parsers.PayloadParser;
import com.crushedlemon.chess.services.ChessService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.crushedlemon.chess.utils.CommonUtils.opponentOf;
import static com.crushedlemon.chess.utils.CommsUtil.communicateToClient;
import static com.crushedlemon.chess.utils.MessageUtils.*;

// This is an intensely used Controller. Move this into its own separate infra.
@RestController
@Slf4j
public class ChessGameController {

    @Autowired
    private ChessService chessService;

    @PostMapping("/movePiece")
    public ResponseEntity<String> movePiece(
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        String playerId = (String) payload.get("userName");
        Move move = PayloadParser.parseMove(payload);
        String gameId = (String) payload.get("gameId");

        log.info("{} wants to play {} in game {}", playerId, move, gameId);

        String message = getMoveMessage(move, playerId);

        return handleAction(
                playerId,
                gameId,
                message,
                (game) -> chessService.playMove(game, move, playerId),
                (selfConn, opponentConn) -> sendSuccessMessage(selfConn, opponentConn, message));
    }

    @PostMapping("/resign")
    public ResponseEntity<String> resign(
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {
        String playerId = (String) payload.get("userName");
        String gameId = (String) payload.get("gameId");

        log.info("{} wants to resign from {}", playerId, gameId);

        String message = getResignationMessage(playerId);

        return handleAction(
                playerId,
                gameId,
                message,
                (game) -> chessService.resign(game, playerId),
                (selfConn, opponentConn) -> sendSuccessMessage(selfConn, opponentConn, message));
    }

    private ResponseEntity<String> handleAction(
            String playerId,
            String gameId,
            String message,
            Function<Game, List<GameError>> action,
            BiConsumer<Optional<String>, Optional<String>> successCommunicator) {
        Optional<Game> game = chessService.getOngoingGame(gameId);
        Optional<String> connectionId = chessService.getConnectionId(playerId);

        if (game.isEmpty()) {
            sendFailureMessage(connectionId, List.of(GameErrorFactory.errorInvalidGame(gameId)));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorMessage(List.of(GameErrorFactory.errorInvalidGame(gameId))));
        }

        List<GameError> errors = action.apply(game.get());

        if (!errors.isEmpty()) {
            sendFailureMessage(connectionId, errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorMessage(errors));
        } else {
            String opponent = opponentOf(playerId, game.get());
            Optional<String> opponentConnectionId = chessService.getConnectionId(opponent);
            successCommunicator.accept(connectionId, opponentConnectionId);
            return ResponseEntity.ok(message);
        }
    }

    private void sendFailureMessage(Optional<String> connectionId, List<GameError> errors) {
        connectionId.ifPresent(s -> communicateToClient(s, getErrorMessage(errors)));
    }

    private void sendSuccessMessage(Optional<String> connectionId, Optional<String> opponentConnectionId, String message) {
        connectionId.ifPresent(s -> communicateToClient(s, message));
        opponentConnectionId.ifPresent(s -> communicateToClient(s, message));
    }
}
