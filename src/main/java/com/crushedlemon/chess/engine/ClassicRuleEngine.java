package com.crushedlemon.chess.engine;

import com.crushedlemon.chess.commons.model.*;
import com.crushedlemon.chess.dto.GetMoveResultInput;
import com.crushedlemon.chess.dto.GetMoveResultOutput;
import com.crushedlemon.chess.dto.PlayMoveInput;
import com.crushedlemon.chess.dto.PlayMoveOutput;
import com.crushedlemon.chess.exception.InvalidMoveException;
import lombok.AllArgsConstructor;

import static com.crushedlemon.chess.utils.CastleUtils.populateCastlingRights;
import static com.crushedlemon.chess.utils.EnPassantUtils.populateEnPassant;

@AllArgsConstructor
public class ClassicRuleEngine implements RuleEngine {

    /**
     * The field `flags` contains castling and en-passant rights.
     * The last 4 bits indicate if casting is allowed in any of the 4 color + side combinations.
     * LSB    : White + Queen Side
     * LSB + 1: White + King Side
     * LSB + 2: Black + Queen Side
     * LSB + 3: Black + King Side
     * The next 5 bits contain a number between [0,16], indicating the square on which en-passant is allowed.
     * 0      : en-passant not allowed
     * 1 - 8  : en-passant allowed on 3rd rank on this position
     * 9 - 16 : en-passant allowed on 6th rank on this position
     */
    @Override
    public PlayMoveOutput playMove(PlayMoveInput input) {
        Move move = input.getMove();
        Piece movedPiece = move.getMovedPiece();
        String startingSquare = move.getStartingSquare();
        String endingSquare = move.getEndingSquare();

        assertPieceExistsAtPosition(input.getBoard(), movedPiece, startingSquare);
        assertValidityOfMove();

        Board newBoard = input.getBoard().getNewBoardByPlayingMove(move);

        int newFlags = populateEnPassant(input.getFlags(), move);
        newFlags = populateCastlingRights(newFlags, move);

        return new PlayMoveOutput(newBoard, newFlags);
    }

    @Override
    public GetMoveResultOutput getMoveResult(GetMoveResultInput input) {
        return null;
    }

    private void assertValidityOfMove() {
        // TODO : Make sure that the move is valid
    }

    private boolean isCastleAllowed(Integer flags, Color color, CastleSide castleSide) {
        int mask = getMask(color, castleSide);
        return (flags & (1 << mask)) > 0;
    }

    private boolean isEnPassantAllowed(Integer flags, String position) {
        return isEnPassantAllowed(flags, position.charAt(0), position.charAt(1) - '0');
    }

    private boolean isEnPassantAllowed(Integer flags, Character file, Integer rank) {
        return isEnPassantAllowed(flags, file - 'a' + 1, rank);
    }

    private boolean isEnPassantAllowed(Integer flags, Integer y, Integer x) {
        if (x != 3 && x != 6) {
            return false;
        }
        int enPassantSquare = (flags >> 4);
        if (x == 3) {
            return (enPassantSquare == y);
        } else {
            return (enPassantSquare == (y + 8));
        }
    }

    private void assertPieceExistsAtPosition(Board board, Piece movedPiece, String startingSquare) {
        Piece existingPiece = board.getPieceAt(startingSquare);
        if (existingPiece != movedPiece) {
            throw new InvalidMoveException(
                    String.format("Move is invalid, tried to move %s from %s but piece there was %s",
                            movedPiece, startingSquare, existingPiece));
        }
    }

    private int getMask(Color color, CastleSide castleSide) {
        int mask = 0;
        mask+= (color == Color.B ? 2 : 0);
        mask+= (castleSide == CastleSide.QUEEN_SIDE ? 1 : 0);
        return mask;
    }
}
