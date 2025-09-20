package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            addMovesInDirection(moves, board, position, direction[0], direction[1]);
        }

        return moves;
    }
}
