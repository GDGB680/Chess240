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
    public Collection<ChessMove> pieceMoves(ChessPosition position, ChessBoard board) {
        // Optional: add default/sliding logic here, or keep abstract
        // Use helper method or add base logic here
        return new ArrayList<>();
    }



    protected boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    protected boolean isEmptyOrCapturablePosition(ChessBoard board, ChessPosition newPosition) {
        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
        return pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != teamColor;
    }


    protected Collection<ChessMove> calculateStraightMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // right, down, left, up
        for (int[] direction : directions) {
            addMovesInDirection(moves, board, position, direction[0], direction[1]);
        }
        return moves;
    }

    protected Collection<ChessMove> calculateDiagonalMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int[] direction : directions) {
            addMovesInDirection(moves, board, position, direction[0], direction[1]);
        }
        return moves;
    }

    private void addMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, int rowDelta, int colDelta) {
        int row = start.getRow();
        int col = start.getColumn();
        while (true) {
            row += rowDelta;
            col += colDelta;
            ChessPosition newPosition = new ChessPosition(row, col);
            if (!isValidPosition(row, col)) { break; }
            if (isEmptyOrCapturablePosition(board, newPosition)) {
                moves.add(new ChessMove(start, newPosition, null));
                if (board.getPiece(newPosition) != null) { break; }// Stop if we captured a piece
            } else {
                break;
            }
        }
    }
}
