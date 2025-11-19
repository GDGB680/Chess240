package client;

import model.*;
import java.util.*;
import java.util.Scanner;
import java.util.List;

public class PostloginUI {
    private final Scanner scanner;
    private final ServerFacade serverFacade;
    private List<GameData> games;

    public PostloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.games = new ArrayList<>();
    }
    public void run() { // Returns false if user logs out
        boolean running = true;
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Create Game");
            System.out.println("2. List Games");
            System.out.println("3. Play Game");
            System.out.println("4. Observe Game");
            System.out.println("5. Help");
            System.out.println("6. Logout");
            System.out.print("Enter command: ");

            String input = scanner.nextLine().trim();

            switch (input.toLowerCase()) {
                case "1":
                case "create":
                    createGame();
                    break;
                case "2":
                case "list":
                    listGames();
                    break;
                case "3":
                case "play":
                    playGame();
                    break;
                case "4":
                case "observe":
                    observeGame();
                    break;
                case "5":
                case "help":
                    printHelp();
                    break;
                case "6":
                case "logout":
                        logout();
                    return; // Return to prelogin
                default:
                    System.out.println("Invalid command. Try again.");
            }
        }
    }

    private void createGame() {
        System.out.print("Game name: ");
        String gameName = scanner.nextLine().trim();
        try {
            CreateGameResult createResult = serverFacade.createGame(gameName);
            System.out.println("✓ Game created successfully!");

            var result = serverFacade.listGames();
            games = new ArrayList<>(result.games());

            System.out.print("Would you like to join this game? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                System.out.println("Choose color: (w)hite or (b)lack");
                String color = scanner.nextLine().trim().toLowerCase();
                if (color.equals("w") || color.equals("b")) {
                    String playerColor = color.equals("w") ? "WHITE" : "BLACK";

                    serverFacade.joinGame(createResult.gameID(), playerColor);
                    System.out.println("✓ Joined game!");

                    GameData fullGame = serverFacade.getGame(createResult.gameID());
                    ChessboardUI.displayBoard(fullGame.game().getBoard(), playerColor.equals("WHITE"));
                }
            }

        } catch (Exception e) {
            System.out.println("✗ Failed to create game: " + e.getMessage());
        }
    }


    private void listGames() {
        try {
            ListGamesResult result = serverFacade.listGames();
            games = new ArrayList<>(result.games());
            System.out.println("\n--- Available Games ---");
            for (GameData game : games) {
                String whitePlayer = game.whiteUsername() != null ? game.whiteUsername() : "Open";
                String blackPlayer = game.blackUsername() != null ? game.blackUsername() : "Open";
                System.out.printf("%s - White: %s, Black: %s%n",
                        game.gameName(), whitePlayer, blackPlayer);
            }
        } catch (Exception e) {
            System.out.println("✗ Failed to list games: " + e.getMessage());
        }
    }

    private void playGame() {
        if (games.isEmpty()) {
            try {
                var result = serverFacade.listGames();
                games = new ArrayList<>(result.games());
            } catch (Exception e) {
                System.out.println("✗ Failed to fetch games: " + e.getMessage());
                return;
            }
        }

        if (games.isEmpty()) {
            System.out.println("No games available. Create a game first.");
            return;
        }

        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine().trim();

        // Find game by name
        GameData selectedGame = null;
        for (GameData game : games) {
            if (game.gameName().equalsIgnoreCase(gameName)) {
                selectedGame = game;
                break;
            }
        }

        if (selectedGame == null) {
            System.out.println("✗ Game not found: " + gameName);
            return;
        }

        try {
            selectedGame = serverFacade.getGame(selectedGame.gameID());
        } catch (Exception e) {
            System.out.println("✗ Failed to fetch game details: " + e.getMessage());
            return;
        }


        String currentUsername = serverFacade.getUsername();

        // Check if user is already in the game
        if (currentUsername.equals(selectedGame.whiteUsername()) ||
                currentUsername.equals(selectedGame.blackUsername())) {
            System.out.println("You're already in this game!");

            try {
                GameData fullGame = serverFacade.getGame(selectedGame.gameID());
                boolean asWhite = currentUsername.equals(selectedGame.whiteUsername());
                ChessboardUI.displayBoard(fullGame.game().getBoard(), asWhite);
                return;
            } catch (Exception e) {
                System.out.println("✗ Failed to display board: " + e.getMessage());
                return;
            }
        }

        String playerColor;
        boolean whiteIsTaken = selectedGame.whiteUsername() != null;
        boolean blackIsTaken = selectedGame.blackUsername() != null;

        if (whiteIsTaken && blackIsTaken) {
            // Both colors taken - join as observer
            System.out.println("Both colors are taken. Joining as observer...");
            playerColor = null;
        } else if (whiteIsTaken) {
            // White is taken, assign black
            System.out.println("White is taken. Joining as black...");
            playerColor = "BLACK";
        } else if (blackIsTaken) {
            // Black is taken, assign white
            System.out.println("Black is taken. Joining as white...");
            playerColor = "WHITE";
        } else {
            // Both colors available - ask user
            System.out.println("Choose color: (w)hite or (b)lack");
            String color = scanner.nextLine().trim().toLowerCase();
            if (!color.equals("w") && !color.equals("b")) {
                System.out.println("Invalid color.");
                return;
            }
            playerColor = color.equals("w") ? "WHITE" : "BLACK";
        }

        try {
            serverFacade.joinGame(selectedGame.gameID(), playerColor == null ? "" : playerColor);

            if (playerColor == null) {
                System.out.println("✓ Joined game as observer!");
            } else {
                System.out.println("✓ Joined game as " + playerColor + "!");
            }

            GameData fullGame = serverFacade.getGame(selectedGame.gameID());
            boolean asWhite = "WHITE".equals(playerColor);
            ChessboardUI.displayBoard(fullGame.game().getBoard(), asWhite);

        } catch (Exception e) {
            System.out.println("✗ Failed to join game: " + e.getMessage());
        }
    }


    private void observeGame() {
        if (games.isEmpty()) {
            try {
                var result = serverFacade.listGames();
                games = new ArrayList<>(result.games());
            } catch (Exception e) {
                System.out.println("✗ Failed to fetch games: " + e.getMessage());
                return;
            }
        }

        if (games.isEmpty()) {
            System.out.println("No games available. Create a game first.");
            return;
        }

        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine().trim();

        // Find game by name
        GameData selectedGame = null;
        for (GameData game : games) {
            if (game.gameName().equalsIgnoreCase(gameName)) {
                selectedGame = game;
                break;
            }
        }

        if (selectedGame == null) {
            System.out.println("✗ Game not found: " + gameName);
            return;
        }

        try {
            serverFacade.joinGame(selectedGame.gameID(), null);
            System.out.println("✓ Observing game!");

            GameData fullGame = serverFacade.getGame(selectedGame.gameID());
            ChessboardUI.displayBoard(fullGame.game().getBoard(), true); // Default to white's perspective

        } catch (Exception e) {
            System.out.println("✗ Failed to observe game: " + e.getMessage());
        }
    }


    private void logout() {
        try {
            serverFacade.logout();
            System.out.println("✓ Logged out successfully!");
        } catch (Exception e) {
            System.out.println("✗ Logout failed: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("""
            Commands:
            create   - Create a new game
            list     - List all available games
            play     - Join a game to play
            observe  - Observe a game
            help     - Show this help message
            logout   - Logout and return to login screen
        """);
    }
}
