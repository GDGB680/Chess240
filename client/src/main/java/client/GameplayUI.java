package client;

import model.GameData;
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
}