package chess;

import java.util.Collection;

public interface MovementRule {
    Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board);
}