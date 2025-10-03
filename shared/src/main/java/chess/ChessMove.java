package chess;

import java.util.Objects;

public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    public ChessPosition getStartPosition() {return startPosition;}
    public ChessPosition getEndPosition() {return endPosition;}
    public ChessPiece.PieceType getPromotionPiece() {return promotionPiece;}


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null || getClass() != obj.getClass()) {return false;}
        ChessMove chessMove = (ChessMove) obj;
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) &&
                Objects.equals(promotionPiece, chessMove.promotionPiece);
    }

    @Override
    public int hashCode() {return Objects.hash(startPosition, endPosition, promotionPiece);}
}
