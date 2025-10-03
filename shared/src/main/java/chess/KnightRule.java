package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                              {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        for (int[] direction : directions) {
            int row = position.getRow() + direction[0];
            int col = position.getColumn() + direction[1];

            if (!board.isValidSquare(row, col)) {continue;}

            if (!board.isOccupiedByOwnPiece(row, col, position)) {
                ChessPosition newPosition = new ChessPosition(row, col);
                moves.add(new ChessMove(position, newPosition, null));
            }
        }
        return moves;
    }
}
