package com.crushedlemon.chess.dto.error;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Map;

@AllArgsConstructor
@Value
public class GameError {
    ErrorCode code;
    Map<String, Object> context;
}
