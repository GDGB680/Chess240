package client;

import ui.EscapeSequences;

import java.util.Scanner;

public class PreloginUI {
    private final Scanner scanner;
    private final ServerFacade serverFacade;

    public PreloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
    }

    public boolean run() {
        while (true) {
            System.out.println("\n" + EscapeSequences.SET_TEXT_BOLD + "--- Chess Client ---" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Help");
            System.out.println("4. Quit");
            System.out.print("Enter command: ");

            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "1", "register" -> {
                    if (register()) {return true;}
                }
                case "2", "login" -> {
                    if (login()) {return true;}
                }
                case "3", "help" -> printHelp();
                case "4", "quit" -> {
                    System.out.println("Goodbye!");
                    return false;
                }
                default -> System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid command." + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private boolean register() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        try {
            serverFacade.register(username, password, email);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "✓ Registration successful!" + EscapeSequences.RESET_TEXT_COLOR);
            return true;
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "✗ Registration failed: " + e.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            return false;
        }
    }

    private boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            serverFacade.login(username, password);
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "✓ Login successful!" + EscapeSequences.RESET_TEXT_COLOR);
            return true;
        } catch (Exception e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "✗ Login failed: " + e.getMessage() + EscapeSequences.RESET_TEXT_COLOR);
            return false;
        }
    }

    private void printHelp() {
        System.out.println(EscapeSequences.SET_TEXT_BOLD + "\nAvailable Commands:" + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.println("register - Create a new account");
        System.out.println("login    - Login to your account");
        System.out.println("help     - Show this help message");
        System.out.println("quit     - Exit the application");
    }
}
