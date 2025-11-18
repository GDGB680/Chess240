package client;

import datamodel.*;
import java.util.*;
import java.util.Scanner;
import java.util.List;

public class PostloginUI {
    private final Scanner scanner;
    private final ServerFacade serverFacade;
    private List<GameDTO> games;

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
            serverFacade.createGame(gameName);
            System.out.println("✓ Game created successfully!");
        } catch (Exception e) {
            System.out.println("✗ Failed to create game: " + e.getMessage());
        }
    }

    private void listGames() {
        try {
            ListGamesResult result = serverFacade.listGames();
            games = new ArrayList<>(result.games());
            System.out.println("\n--- Available Games ---");
            for (int i = 0; i < games.size(); i++) {
                GameDTO game = games.get(i);
                String whitePlayer = game.whiteUsername() != null ? game.whiteUsername() : "Open";
                String blackPlayer = game.blackUsername() != null ? game.blackUsername() : "Open";
                System.out.printf("%d. %s - White: %s, Black: %s%n",
                        i + 1, game.gameName(), whitePlayer, blackPlayer);
            }
        } catch (Exception e) {
            System.out.println("✗ Failed to list games: " + e.getMessage());
        }
    }

    private void playGame() {
        if (games.isEmpty()) {
            System.out.println("No games available. Try listing games first.");
            return;
        }

        System.out.print("Enter game number: ");
        try {
            int gameNum = Integer.parseInt(scanner.nextLine().trim());
            if (gameNum < 1 || gameNum > games.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameDTO game = games.get(gameNum - 1);
            System.out.println("Choose color: (w)hite or (b)lack");
            String color = scanner.nextLine().trim().toLowerCase();

            if (!color.equals("w") && !color.equals("b")) {
                System.out.println("Invalid color.");
                return;
            }

            String playerColor = color.equals("w") ? "WHITE" : "BLACK";
            serverFacade.joinGame(game.gameID(), playerColor);
            System.out.println("✓ Joined game!");

            // Draw chessboard
            drawChessboard(playerColor.equals("WHITE"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        } catch (Exception e) {
            System.out.println("✗ Failed to join game: " + e.getMessage());
        }
    }

    private void observeGame() {
        if (games.isEmpty()) {
            System.out.println("No games available. Try listing games first.");
            return;
        }

        System.out.print("Enter game number: ");
        try {
            int gameNum = Integer.parseInt(scanner.nextLine().trim());
            if (gameNum < 1 || gameNum > games.size()) {
                System.out.println("Invalid game number.");
                return;
            }

            GameDTO game = games.get(gameNum - 1);
            serverFacade.joinGame(game.gameID(), "OBSERVER");
            System.out.println("✓ Observing game!");

            // Draw from white's perspective
            drawChessboard(true);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        } catch (Exception e) {
            System.out.println("✗ Failed to observe game: " + e.getMessage());
        }
    }

    private void drawChessboard(boolean whitePerspective) {
        // Simple ASCII chessboard
        String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] ranks = {"8", "7", "6", "5", "4", "3", "2", "1"};

        if (!whitePerspective) {
            // Flip for black perspective
            files = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
            ranks = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        }

        System.out.println("\n  " + String.join(" ", files));
        for (String rank : ranks) {
            System.out.print(rank + " ");
            for (int f = 0; f < 8; f++) {
                int sum = (whitePerspective ? 8 - Integer.parseInt(rank) : Integer.parseInt(rank) - 1) + f;
                String square = (sum % 2 == 0) ? "⬜" : "⬛";
                System.out.print(square + " ");
            }
            System.out.println(rank);
        }
        System.out.println("  " + String.join(" ", files));
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
