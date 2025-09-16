package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece bishop = board.getPiece(position);

        if (bishop == null) {
            return moves;
        }
        this.teamColor = bishop.getTeamColor();


        moves.addAll(calculateDiagonalMoves(board, position));
        return moves;
    }
}
