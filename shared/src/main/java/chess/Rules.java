package chess;

public class Rules {


    public MovementRule getRule(String pieceType) {
        switch (pieceType.toLowerCase()) {
            case "BISHOP":
                return new BishopRule();
            case "KING":
                return new KingRule();
            case "KNIGHT":
                return new KnightRule();
            case "PAWN":
                return new PawnRule();
            case "QUEEN":
                return new QueenRule();
            case "ROOK":
                return new RookRule();
            default:
                throw new IllegalArgumentException("Unknown piece type: " + pieceType);
        }
    }
}
