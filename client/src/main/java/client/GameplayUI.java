package client;

import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayUI implements WebSocketConnection.WebSocketMessageListener {
    private final ServerFacade serverFacade;
    private final Scanner scanner;
    private final int gameID;
    private final String playerColor; // "WHITE", "BLACK", or null for observer
    private WebSocketConnection wsConnection;
    private GameData currentGame;
    private boolean gameActive = true;

    public GameplayUI(ServerFacade serverFacade, int gameID, String playerColor) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public void run() {
        try {
            // Connect via WebSocket
            wsConnection = new WebSocketConnection(serverFacade.getUrl(), this);

            // Send CONNECT command
            UserGameCommand connectCmd = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    serverFacade.getAuthToken(),
                    gameID,
                    null
            );
            wsConnection.send(connectCmd);

            // Game loop
            while (gameActive) {
                displayMenu();
                String command = scanner.nextLine().trim().toLowerCase();

                switch (command) {
                    case "help" -> displayHelp();
                    case "redraw" -> displayBoard();
                    case "leave" -> handleLeave();
                    case "move" -> handleMakeMove();
                    case "resign" -> handleResign();
                    case "highlight" -> handleHighlightMoves();
                    default -> System.out.println("Unknown command");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (wsConnection != null) {
                    wsConnection.close();
                }
            } catch (Exception e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    @Override
    public void onServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                currentGame = message.getGame();
                displayBoard();
                break;
            case ERROR:
                System.out.println("x" + message.getMessage());
                break;
            case NOTIFICATION:
                System.out.println("I" + message.getMessage());
                break;
        }
    }

    private void displayMenu() {
        System.out.println("\n--- Gameplay Commands ---");
        System.out.println("help - Show this help message");
        System.out.println("redraw - Redraw the chess board");
        System.out.println("leave - Leave the game");
        System.out.println("move - Make a move");
        System.out.println("resign - Resign from the game");
        System.out.println("highlight - Highlight legal moves");
        System.out.print("Command: ");
    }

    private void displayHelp() {
        System.out.println("""
            Available commands:
              help - Show this help message
              redraw - Redraw the chess board
              leave - Leave the game
              move - Make a move
              resign - Resign from the game
              highlight - Highlight legal moves for a piece
            """);
    }

    private void displayBoard() {
        if (currentGame != null) {
            boolean isWhitePerspective = playerColor == null || "WHITE".equals(playerColor);
            ChessboardUI.displayBoard(currentGame.game().getBoard(), isWhitePerspective);
        }
    }

    private void handleMakeMove() {
        if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            System.out.println("Observers cannot make moves");
            return;
        }

        try {
            System.out.print("Enter start position (e.g., e2): ");
            String startStr = scanner.nextLine().trim();
            ChessPosition start = parsePosition(startStr);

            System.out.print("Enter end position (e.g., e4): ");
            String endStr = scanner.nextLine().trim();
            ChessPosition end = parsePosition(endStr);

            ChessMove move = new ChessMove(start, end, null);

            UserGameCommand moveCmd = new UserGameCommand(
                    UserGameCommand.CommandType.MAKE_MOVE,
                    serverFacade.getAuthToken(),
                    gameID,
                    move
            );
            wsConnection.send(moveCmd);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleLeave() {
        try {
            UserGameCommand leaveCmd = new UserGameCommand(
                    UserGameCommand.CommandType.LEAVE,
                    serverFacade.getAuthToken(),
                    gameID,
                    null
            );
            wsConnection.send(leaveCmd);
            gameActive = false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleResign() {
        if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            System.out.println("Observers cannot resign");
            return;
        }

        System.out.print("Are you sure you want to resign? (y/n): ");
        if ("y".equalsIgnoreCase(scanner.nextLine().trim())) {
            try {
                UserGameCommand resignCmd = new UserGameCommand(
                        UserGameCommand.CommandType.RESIGN,
                        serverFacade.getAuthToken(),
                        gameID,
                        null
                );
                wsConnection.send(resignCmd);
                gameActive = false;
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleHighlightMoves() {
        try {
            System.out.print("Enter piece position (e.g., e2): ");
            String posStr = scanner.nextLine().trim();
            ChessPosition pos = parsePosition(posStr);

            if (currentGame == null || currentGame.game() == null) {
                System.out.println("Game not loaded");
                return;
            }

            var legalMoves = currentGame.game().validMoves(pos);
            System.out.println("Legal moves for piece at " + posStr + ":");
            for (ChessMove move : legalMoves) {
                System.out.println("  -> " + move.getEndPosition());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private ChessPosition parsePosition(String str) throws IllegalArgumentException {
        if (str.length() != 2) throw new IllegalArgumentException("Invalid format (use e.g., 'e2')");
        char file = str.charAt(0);
        int rank = Character.getNumericValue(str.charAt(1));

        if (file < 'a' || file > 'h' || rank < 1 || rank > 8) {
            throw new IllegalArgumentException("Position out of bounds");
        }

        int col = file - 'a' + 1;
        return new ChessPosition(rank, col);
    }
}
