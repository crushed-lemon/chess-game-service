package com.crushedlemon.chess.controllers;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.GamePreferences;
import com.crushedlemon.chess.services.ChessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
public class OngoingStateController {

    @Autowired
    private ChessService chessService;

    @GetMapping("/ongoing-lobby")
    ResponseEntity<Boolean> onGoingLobbyRequests(
            @RequestBody Map<String, Object> payload) {
        String userName = (String) payload.get("userName");

        Optional<GamePreferences> gamePreferences = chessService.getOngoingLobbyRequest(userName);

        return ResponseEntity.ok(gamePreferences.isPresent());
    }

    @GetMapping("/ongoing-game-id")
    ResponseEntity<String> onGoingGameId(
            @RequestBody Map<String, Object> payload) {
        String userName = (String) payload.get("userName");

        Optional<String> gameId = chessService.getOngoingGameId(userName);

        return gameId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
    }

    @GetMapping("/ongoing-game")
    public ResponseEntity<String> getGame(
            @RequestBody Map<String, Object> payload) {
        String userName = (String) payload.get("userName");
        String gameId = (String) payload.get("gameId");

        Optional<Game> game = chessService.getOngoingGame(gameId);

        if(game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Game id %s not found", gameId));
        }

        if(!game.get().getWhitePlayerId().equals(userName) && !game.get().getBlackPlayerId().equals(userName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    String.format("%s is not allowed to access game id : %s", userName, gameId));
        }

        return ResponseEntity.ok(game.get().getBoard().getPieces());
    }
}
