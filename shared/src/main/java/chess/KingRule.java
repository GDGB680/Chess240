package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1},
                              {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int row = position.getRow() + direction[0];
            int col = position.getColumn() + direction[1];

            if (!board.isValidSquare(row, col)) continue;

            if (!board.isOccupiedByOwnPiece(row, col, position)) {
                ChessPosition newPosition = new ChessPosition(row, col);
                moves.add(new ChessMove(position, newPosition, null));
            }
        }

        return moves;
    }
}