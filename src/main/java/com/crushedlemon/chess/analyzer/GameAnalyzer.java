package com.crushedlemon.chess.analyzer;

import com.crushedlemon.chess.commons.model.Game;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.enums.MoveResult;

import java.util.List;

public interface GameAnalyzer {

    List<MoveResult> getMoveResults(Game game, Move move);

}
