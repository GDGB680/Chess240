package chess;

import java.util.Collection;
import java.util.HashSet;

public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {return pieceColor;}
    public PieceType getPieceType() {return type;}

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new HashSet<ChessMove>();
    }
}


//@Override
//public boolean equals(Object o) {
//    if (this == o) {return true;}
//    if (o == null || getClass() != o.getClass()) {return false;}
//    ChessPiece that = (ChessPiece) o;
//    return pieceColor == that.pieceColor && pieceType == that.pieceType;
//}
//
//@Override
//public int hashCode() { return Objects.hash(pieceColor, pieceType); }
//}
