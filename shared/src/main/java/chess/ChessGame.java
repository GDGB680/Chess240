package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentColor;
    private ChessBoard board;

    public ChessGame() {
        this.currentColor = TeamColor.WHITE;
        this.board = new ChessBoard();
        board.resetBoard();
    }

    public TeamColor getTeamTurn() {return currentColor;}
    public void setTeamTurn(TeamColor team) {this.currentColor = team;}

    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            if (isValidMove(move)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }


    private boolean isValidMove(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) { return false; }
        // Check if the move is in the list of possible moves for the piece
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, move.getStartPosition());
        if (!possibleMoves.contains(move)) { return false; }
        // Temporarily make the move
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
        // Check if the move leaves the piece's team in check
        boolean valid = !isInCheck(piece.getTeamColor());
        // Undo the move
        board.addPiece(move.getStartPosition(), piece);
        board.addPiece(move.getEndPosition(), capturedPiece);
        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }


    public void setBoard(ChessBoard board) {this.board = board;}
    public ChessBoard getBoard() {return this.board;}
}
