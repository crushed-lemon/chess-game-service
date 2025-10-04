package com.crushedlemon.chess.controllers;

import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.enums.OperationStatus;
import com.crushedlemon.chess.parsers.PayloadParser;
import com.crushedlemon.chess.services.ChessService;
import com.crushedlemon.chess.utils.CommsUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

// This is an intensely used Controller. Move this into its own separate infra.
@RestController
@Slf4j
public class ChessGameController {

    @Autowired
    private ChessService chessService;

    @PostMapping("/movePiece")
    public ResponseEntity<String> handleWebSocketMessage(
            @RequestBody Map<String, Object> payload,
            @RequestHeader Map<String, String> headers,
            HttpServletRequest request) {

        log.atInfo().log("API payload {}", payload);
        String userName = (String) payload.get("userName");
        Move move = PayloadParser.parseMove(payload);
        String gameId = (String) payload.get("gameId");

        OperationStatus operationStatus = chessService.playMove(gameId, move, userName);

        // TODO : Implement this
        if (operationStatus.equals(OperationStatus.FAILED_UNAUTHORIZED)) {
            //CommsUtil.communicateToClient("clientId", "Disallowed move");
        }
        else if (operationStatus.equals(OperationStatus.FAILED_INVALID_MOVE)) {
            //CommsUtil.communicateToClient("clientId", "Invalid move");
        } else {
            //CommsUtil.communicateToClient("client2", move.getMoveName());
        }
        return ResponseEntity.ok("");
    }
}
