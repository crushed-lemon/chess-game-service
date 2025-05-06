package com.crushedlemon.chess.services;

import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.enums.OperationStatus;

public interface ChessService {

    OperationStatus playMove(String gameId, Move move, String player);
}
