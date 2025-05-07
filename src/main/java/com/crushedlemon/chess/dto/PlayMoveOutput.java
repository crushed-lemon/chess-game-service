package com.crushedlemon.chess.dto;

import com.crushedlemon.chess.commons.model.Board;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class PlayMoveOutput {
    Board board;
    Integer flags;
}
