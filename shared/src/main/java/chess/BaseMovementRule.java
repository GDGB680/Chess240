package chess;

import java.util.Collection;
import java.util.ArrayList;

public abstract class BaseMovementRule implements MovementRule {


    private Collection<ChessMove> calculateMoves() {
        // Logic to calculate moves (to be used by pieceMoves or subclasses)
        return new ArrayList<>();
    }

    // Implement the interface method
    @Override
    public Collection<ChessMove> pieceMoves() {
        // Use helper method or add base logic here
        return calculateMoves();
    }


}
