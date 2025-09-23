package chess;

import java.util.Collection;

public interface MovementRule {
    default Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        return null;
    }
}