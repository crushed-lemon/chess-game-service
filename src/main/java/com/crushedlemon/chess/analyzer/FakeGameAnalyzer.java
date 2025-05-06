package com.crushedlemon.chess.analyzer;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.enums.MoveResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FakeGameAnalyzer implements GameAnalyzer {

    @Override
    public List<MoveResult> getMoveResults(Game game, Move move) {
        return List.of(MoveResult.NOTHING);
    }
}
