package com.crushedlemon.chess.engine.validator.classic;

import com.crushedlemon.chess.commons.model.Board;
import com.crushedlemon.chess.commons.model.Color;
import com.crushedlemon.chess.commons.model.Move;
import com.crushedlemon.chess.commons.model.Piece;
import com.crushedlemon.chess.engine.movementRule.classic.ClassicMovementRule;
import com.crushedlemon.chess.engine.validator.MoveValidator;

import static com.crushedlemon.chess.utils.CommonUtils.getColor;

abstract public class ClassicValidator implements MoveValidator {

    private final ClassicMovementRule classicMovementRule;

    protected ClassicValidator(ClassicMovementRule classicMovementRule) {
        this.classicMovementRule = classicMovementRule;
    }

    @Override
    public boolean isMoveValid(Board board, Move move) {
        return !isBlankPiece(board, move.getStartingSquare())
                && classicMovementRule.isPieceMovementAllowed(board, move)
                && !isCapturingOwnPiece(board, move)
                && !isResultingInOwnCheck(board, move);
    }

    private boolean isBlankPiece(Board board, String square) {
        return board.getPieceAt(square).equals(Piece.X);
    }

    private boolean isCapturingOwnPiece(Board board, Move move) {
        Piece piece = board.getPieceAt(move.getStartingSquare());
        Color ownColor = getColor(piece);
        if(isBlankPiece(board, move.getEndingSquare())) {
            return false;
        }
        Piece targetPiece = board.getPieceAt(move.getEndingSquare());
        Color targetColor = getColor(targetPiece);

        return ownColor.equals(targetColor);
    }

    private boolean isResultingInOwnCheck(Board board, Move move) {
        Piece piece = board.getPieceAt(move.getStartingSquare());
        Color ownColor = getColor(piece);
        return isKingChecked(ownColor, board.getNewBoardByPlayingMove(move));
    }

    private boolean isKingChecked(Color ownColor, Board newBoardByPlayingMove) {
        // TODO : implement to check if king is under check
        return false;
    }
}
