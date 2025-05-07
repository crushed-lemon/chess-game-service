package com.crushedlemon.chess.dto;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Move;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PlayMoveInput {
    Board board;
    Move move;
    Integer flags;
}
