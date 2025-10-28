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
            addSingleMove(moves, board, position, direction[0], direction[1]);
        }

        return moves;
    }
}
