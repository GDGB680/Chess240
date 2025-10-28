package chess;

import java.util.Collection;
import java.util.ArrayList;

public abstract class BaseMovementRule implements MovementRule {

    protected void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board,
                                       ChessPosition start, int rowDelta, int colDelta) {
        int row = start.getRow();
        int col = start.getColumn();

        while (true) {
            row += rowDelta;
            col += colDelta;
            if (!board.isValidSquare(row, col)) {break;}
            if (board.isOccupiedByOwnPiece(row, col, start)) {break;}
            ChessPosition newPosition = new ChessPosition(row, col);
            moves.add(new ChessMove(start, newPosition, null));
            if (board.isOccupiedByOpponent(row, col, start)) {break;}
        }
    }

    protected void addSingleMove(Collection<ChessMove> moves, ChessBoard board,
                                 ChessPosition start, int rowOffset, int colOffset) {
        int row = start.getRow() + rowOffset;
        int col = start.getColumn() + colOffset;

        if (!board.isValidSquare(row, col)) {return;}
        if (!board.isOccupiedByOwnPiece(row, col, start)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            moves.add(new ChessMove(start, newPosition, null));
        }
    }
}