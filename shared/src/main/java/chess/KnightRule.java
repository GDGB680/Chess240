package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] jumps = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };

        for (int[] jump : jumps) {
            int row = position.getRow() + jump[0];
            int col = position.getColumn() + jump[1];

            if (board.isValidSquare(row, col)) break;

            if (!board.isOccupiedByOwnPiece(row, col, position)) {
                ChessPosition newPosition = new ChessPosition(row, col);
                moves.add(new ChessMove(position, newPosition, null));
            }
        }

        return moves;
    }
}
