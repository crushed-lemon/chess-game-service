package com.crushedlemon.chess.controllers;

import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.enums.OperationStatus;
import com.crushedlemon.chess.parsers.PayloadParser;
import com.crushedlemon.chess.services.ChessService;
import com.crushedlemon.chess.utils.CommsUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
public class ChessGameController {

    private static final Logger logger = LoggerFactory.getLogger(ChessGameController.class);

    @Autowired
    private ChessService chessService;

    @PostMapping("/movePiece")
    public ResponseEntity<String> handleWebSocketMessage(
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        logger.atInfo().log(String.format("API payload %s", payload));
        String userName = (String) payload.get("userName");
        Move move = PayloadParser.parseMove(payload);
        String gameId = (String) payload.get("gameId");

        OperationStatus operationStatus = chessService.playMove(gameId, move, userName);

        if (operationStatus.equals(OperationStatus.FAILED_UNAUTHORIZED)) {
            CommsUtil.communicateToClient("clientId", "Disallowed move");
        }
        else if (operationStatus.equals(OperationStatus.FAILED_INVALID_MOVE)) {
            CommsUtil.communicateToClient("clientId", "Invalid move");
        } else {
            CommsUtil.communicateToClient("client2", move.getMoveName());
        }
        return ResponseEntity.ok("");
    }
}
