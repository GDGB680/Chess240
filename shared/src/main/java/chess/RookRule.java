package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for (int[] direction : directions) {
            addMovesInDirection(moves, board, position, direction[0], direction[1]);
        }

        return moves;
    }
}
