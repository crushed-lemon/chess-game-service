package com.crushedlemon.chess.engine.movementRule.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Color;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;

import static com.crushedlemon.chess.utils.CommonUtils.*;

public class PawnClassicMovementRule implements ClassicMovementRule {

    @Override
    public boolean isPieceMovementAllowed(Board board, Move move) {
        String startingSquare = move.getStartingSquare();
        int startingRank = getRank(startingSquare);
        int startingFile = getFile(startingSquare);

        String endingSquare = move.getEndingSquare();
        int endingRank = getRank(endingSquare);
        int endingFile = getFile(endingSquare);

        Color selfColor = getColor(board.getPieceAt(startingSquare));
        int direction = selfColor.equals(Color.W) ? 1 : -1;
        int baseRank = selfColor.equals(Color.W) ? 2 : 7;

        if(isStraightMovement(endingFile, startingFile)) {
            return isValidStraightMove(endingRank, startingRank, endingFile, direction, baseRank, board);
        }

        if(isDiagonalCapture(endingFile, startingFile)) {
            return isValidDiagonalCapture(endingRank, endingFile, startingRank, direction, board, selfColor);
        }

        return false;
    }

    private boolean isValidDiagonalCapture(int endingRank, int endingFile, int startingRank, int direction, Board board, Color selfColor) {
        int numSquaresMoved = (endingRank - startingRank) * direction;
        if(numSquaresMoved != 1) {
            return false;
        }
        Color capturedColor = getColor(board.getPieceAt(endingFile, endingRank));
        return capturedColor != selfColor;
    }

    private boolean isValidStraightMove(int endingRank, int startingRank, int endingFile, int direction, int baseRank, Board board) {
        int numSquaresMoved = (endingRank - startingRank) * direction;

        if(numSquaresMoved <=0 || numSquaresMoved > 2) {
            return false;
        }

        if(numSquaresMoved == 1) {
            return isEmptySquare(endingFile, endingRank, board);
        }

        // 2-square move
        if(startingRank != baseRank) {
            return false;
        }
        return isEmptySquare(endingFile, endingRank, board)
                && isEmptySquare(endingFile, startingRank + direction, board);
    }

    private boolean isEmptySquare(int file, int rank, Board board) {
        return board.getPieceAt(file, rank).equals(Piece.X);
    }

    private boolean isDiagonalCapture(int endingFile, int startingFile) {
        return Math.abs(endingFile - startingFile) == 1;
    }

    private boolean isStraightMovement(int endingFile, int startingFile) {
        return endingFile == startingFile;
    }

}
