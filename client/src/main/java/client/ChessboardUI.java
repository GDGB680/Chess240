package client;

import chess.*;
import ui.EscapeSequences;

public class ChessboardUI {
    private static final String[] FILES = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] RANKS = {"8", "7", "6", "5", "4", "3", "2", "1"};

    public static void displayBoard(ChessBoard board, boolean isWhitePerspective) {
        String[] displayFiles = isWhitePerspective ? FILES : new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        String[] displayRanks = isWhitePerspective ? RANKS : new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.println("\n  " + String.join(" ", displayFiles));

        for (String rank : displayRanks) {
            System.out.print(rank + " ");
            for (String file : displayFiles) {
                int row = 9 - Integer.parseInt(rank);
                int col = file.charAt(0) - 'a';
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));

                String square = getSquareColor(row, col);
                String pieceString = getPieceString(piece);
                System.out.print(square + pieceString + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println(" " + rank);
        }
        System.out.println("  " + String.join(" ", displayFiles));
    }



    private static String getSquareColor(int row, int col) {
        boolean isLight = (row + col) % 2 == 1;
        return isLight ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
    }


    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {return EscapeSequences.EMPTY;}


        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;


        return switch (piece.getPieceType()) {
            case KING -> isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default -> EscapeSequences.EMPTY;
        };
    }


}
