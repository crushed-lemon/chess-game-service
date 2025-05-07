package com.crushedlemon.chess.engine;

import com.crushedlemon.chess.dto.PlayMoveInput;
import com.crushedlemon.chess.dto.PlayMoveOutput;
import com.crushedlemon.chess.dto.GetMoveResultInput;
import com.crushedlemon.chess.dto.GetMoveResultOutput;

public interface RuleEngine {
    PlayMoveOutput playMove(PlayMoveInput input);
    GetMoveResultOutput getMoveResult(GetMoveResultInput input);
}
