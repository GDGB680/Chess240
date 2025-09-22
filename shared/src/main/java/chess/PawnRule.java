package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

public class PawnRule extends BaseMovementRule {

    @Override
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece pawn = board.getPiece(position);
        ChessGame.TeamColor color = pawn.getTeamColor();

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = position.getRow();
        int col = position.getColumn();

        // Helper to add either normal or promotion moves
        BiConsumer<ChessPosition, ChessPiece.PieceType> addMove =
                (dest, promo) -> {
            moves.add(new ChessMove(position, dest, promo));
        };

        // 1) One‐square forward
        int forwardRow = row + direction;
        ChessPosition forwardPos = new ChessPosition(forwardRow, col);
        boolean blockedForward = !board.isValidSquare(forwardRow, col)
                || board.isOccupiedByOwnPiece(forwardRow, col, position)
                || board.isOccupiedByOpponent(forwardRow, col, position);
        if (!blockedForward) {
            // Promotion on reaching back rank
            if (forwardRow == promotionRow) {
                for (ChessPiece.PieceType promoType : new ChessPiece.PieceType[]{
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.ROOK,
                        ChessPiece.PieceType.BISHOP,
                        ChessPiece.PieceType.KNIGHT }) {
                    addMove.accept(forwardPos, promoType);
                }
            } else {
                addMove.accept(forwardPos, null);
                // 2) Two‐square forward on first move (both squares must be empty)
                if (row == startRow) {
                    int twoAhead = row + 2 * direction;
                    ChessPosition twoPos = new ChessPosition(twoAhead, col);
                    boolean blockedTwoAhead = !board.isValidSquare(twoAhead, col)
                            || board.isOccupiedByOwnPiece(twoAhead, col, position)
                            || board.isOccupiedByOpponent(twoAhead, col, position)
                            || board.isOccupiedByOwnPiece(forwardRow, col, position)
                            || board.isOccupiedByOpponent(forwardRow, col, position);

                    if (!blockedTwoAhead) {addMove.accept(twoPos, null);}
                }
            }
        }

        // 3) Diagonal captures (with promotion if on last rank)
        for (int dc : new int[]{-1, 1}) {
            int captureCol = col + dc;
            int captureRow = forwardRow;
            if (board.isValidSquare(captureRow, captureCol)
                    && board.isOccupiedByOpponent(captureRow, captureCol, position)) {

                ChessPosition capturePos = new ChessPosition(captureRow, captureCol);
                if (captureRow == promotionRow) {
                    for (ChessPiece.PieceType promoType : new ChessPiece.PieceType[]{
                            ChessPiece.PieceType.QUEEN,
                            ChessPiece.PieceType.ROOK,
                            ChessPiece.PieceType.BISHOP,
                            ChessPiece.PieceType.KNIGHT }) {
                        addMove.accept(capturePos, promoType);
                    }
                } else {addMove.accept(capturePos, null);}
            }
        }
        return moves;
    }
}