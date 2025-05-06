package com.crushedlemon.chess.parsers;

import com.crushedlemon.chess.commons.model.Color;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class PayloadParser {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static Move parseMove(Map<String, Object> payload) {
        //return Move.builder().movedPiece(Piece.N).playerColor(Color.W).startingSquare("b1").endingSquare("c3").build();

        return objectMapper.convertValue((Map<String, Object>) payload.get("move"), Move.class);
    }
}
