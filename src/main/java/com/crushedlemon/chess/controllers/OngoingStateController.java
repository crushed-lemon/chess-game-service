package com.crushedlemon.chess.controllers;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.GamePreferences;
import com.crushedlemon.chess.services.ChessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.JsonObject;

import java.util.Optional;

import static com.crushedlemon.chess.utils.CommonUtils.getColorForUser;
import static com.crushedlemon.chess.utils.CommonUtils.getCurrentColor;

@RestController
@Slf4j
public class OngoingStateController {

    @Autowired
    private ChessService chessService;

    @GetMapping("/ongoing-lobby")
    ResponseEntity<String> onGoingLobbyRequests(
            @RequestParam(name = "username") String username) {
        Optional<GamePreferences> gamePreferences = chessService.getOngoingLobbyRequest(username);

        JsonObject response = new JsonObject();
        response.addProperty("lobby", gamePreferences.isPresent());

        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/ongoing-game-id")
    ResponseEntity<String> onGoingGameId(
            @RequestParam(name = "username") String username) {
        Optional<String> gameId = chessService.getOngoingGameId(username);
        if(gameId.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        JsonObject response = new JsonObject();
        response.addProperty("gameId", gameId.get());

        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/ongoing-game")
    public ResponseEntity<String> getGame(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "gameId") String gameId) {
        Optional<Game> game = chessService.getOngoingGame(gameId);

        if(game.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Game id %s not found", gameId));
        }

        if(!game.get().getWhitePlayerId().equals(username) && !game.get().getBlackPlayerId().equals(username)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    String.format("%s is not allowed to access game id : %s", username, gameId));
        }

        JsonObject response = new JsonObject();
        response.addProperty("board", game.get().getBoard().getPieces());
        response.addProperty("color", getColorForUser(username, game.get()));
        response.addProperty("currentPlayer", getCurrentColor(game.get()).toString());

        return ResponseEntity.ok(response.toString());
    }
}
