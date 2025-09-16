package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        // Implement the specific logic for moves here
        // For example: add all possible one-step moves in all directions
        // moves.add(...);

        return moves;
    }
}