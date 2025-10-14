package com.crushedlemon.chess.utils;

import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.dto.error.GameError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public class MessageUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getMoveMessage(Move move, String playerId, String nextPlayer) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "action", "MOVE_SUCCESS",
                    "source", move.getStartingSquare(),
                    "destination", move.getEndingSquare(),
                    "piece", move.getMovedPiece().toString(),
                    "playerId", playerId,
                    "nextPlayer", nextPlayer
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getResignationMessage(String resigningPlayer) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "action", "PLAYER_RESIGNED",
                    "resigningPlayer", resigningPlayer
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getErrorMessage(List<GameError> errors) {
        JsonArray array = new JsonArray();
        for(GameError error : errors) {
            JsonObject errorJson = new JsonObject();
            errorJson.addProperty("code", error.getCode().toString());

            JsonObject contextJson = new JsonObject();
            for (Map.Entry<String, Object> entry : error.getContext().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Number) {
                    contextJson.addProperty(entry.getKey(), (Number) value);
                } else if (value instanceof Boolean) {
                    contextJson.addProperty(entry.getKey(), (Boolean) value);
                } else {
                    contextJson.addProperty(entry.getKey(), value.toString());
                }
            }

            errorJson.add("context", contextJson);

            array.add(errorJson);
        }
        JsonObject result = new JsonObject();
        result.addProperty("action", "ERROR");
        result.add("errors", array);
        return result.toString();
    }
}
