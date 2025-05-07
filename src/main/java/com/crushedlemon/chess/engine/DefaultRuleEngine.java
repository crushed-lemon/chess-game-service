package com.crushedlemon.chess.engine;

import com.crushedlemon.chess.dto.GetMoveResultInput;
import com.crushedlemon.chess.dto.GetMoveResultOutput;
import com.crushedlemon.chess.dto.PlayMoveInput;
import com.crushedlemon.chess.dto.PlayMoveOutput;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DefaultRuleEngine implements RuleEngine {

    @Override
    public PlayMoveOutput playMove(PlayMoveInput input) {
        return new PlayMoveOutput(input.getBoard(), input.getFlags());
    }

    @Override
    public GetMoveResultOutput getMoveResult(GetMoveResultInput input) {
        return new GetMoveResultOutput(List.of());
    }
}
