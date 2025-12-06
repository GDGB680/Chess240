package handler;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import model.GameData;
import chess.ChessGame;
import chess.ChessMove;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebSocketHandler {
    private static final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private static final Map<Session, Integer> sessionGameMap = new ConcurrentHashMap<>();
    private static final Map<Session, String> sessionUserMap = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    private final GameService gameService;

    public WebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        System.out.println("WebSocket connection established");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                case MAKE_MOVE -> handleMakeMove(session, command);
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command);
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Error: " + e.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Integer gameID = sessionGameMap.get(session);
        if (gameID != null) {
            gameSessions.getOrDefault(gameID, new HashSet<>()).remove(session);
            sessionGameMap.remove(session);
            sessionUserMap.remove(session);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();

            // Get the game (validates auth and game exists)
            GameData gameData = gameService.getGame(gameID, authToken);

            // Determine username and color
            String username = gameService.getUsernameFromToken(authToken);
            sessionUserMap.put(session, username);

            // Add session to game
            gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
            sessionGameMap.put(session, gameID);

            // Send LOAD_GAME to connecting user
            sendMessage(session, new ServerMessage(gameData));

            // Determine player type
            String playerType = "Observer";
            if (username.equals(gameData.whiteUsername())) {
                playerType = "WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                playerType = "BLACK";
            }

            // Send NOTIFICATION to others
            String notification = username + " joined as " + playerType;
            broadcastToOthers(gameID, session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification));

        } catch (Exception e) {
            sendErrorMessage(session, "Failed to connect to game: " + e.getMessage());
        }
    }

    private void handleMakeMove(Session session, UserGameCommand command) throws IOException {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();

            if (command.getMove() == null) {
                sendErrorMessage(session, "Move is required");
                return;
            }

            // Make the move
            gameService.makeMove(gameID, authToken, command.getMove());

            // Get updated game
            GameData gameData = gameService.getGame(gameID, authToken);

            // Send LOAD_GAME to all
            broadcastToGame(gameID, new ServerMessage(gameData));

            // Send notification about move
            String username = sessionUserMap.get(session);
            String moveNotification = username + " made a move from " +
                    command.getMove().getStartPosition() + " to " + command.getMove().getEndPosition();
            broadcastToOthers(gameID, session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveNotification));

            // Check for checkmate/check/stalemate
            ChessGame.TeamColor teamTurn = gameData.game().getTeamTurn();

            if (gameData.game().isInCheckmate(teamTurn)) {
                String checkmateMsg = teamTurn + " is in checkmate";
                broadcastToGame(gameID, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkmateMsg));
            } else if (gameData.game().isInCheck(teamTurn)) {
                String checkMsg = teamTurn + " is in check";
                broadcastToGame(gameID, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, checkMsg));
            }

        } catch (Exception e) {
            sendErrorMessage(session, "Invalid move: " + e.getMessage());
        }
    }

    private void handleLeave(Session session, UserGameCommand command) throws IOException {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();

            gameService.leaveGame(gameID, authToken);

            gameSessions.getOrDefault(gameID, new HashSet<>()).remove(session);
            String username = sessionUserMap.remove(session);
            sessionGameMap.remove(session);

            String leaveMsg = username + " left the game";
            broadcastToGame(gameID, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, leaveMsg));

        } catch (Exception e) {
            sendErrorMessage(session, "Failed to leave game");
        }
    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {
        try {
            int gameID = command.getGameID();
            String authToken = command.getAuthToken();

            gameService.resignGame(gameID, authToken);

            String username = sessionUserMap.get(session);
            String resignMsg = username + " resigned";
            broadcastToGame(gameID, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, resignMsg));

        } catch (Exception e) {
            sendErrorMessage(session, "Failed to resign");
        }
    }

    private void sendMessage(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(gson.toJson(message));
        }
    }

    private void broadcastToGame(int gameID, ServerMessage message) throws IOException {
        for (Session session : gameSessions.getOrDefault(gameID, new HashSet<>())) {
            sendMessage(session, message);
        }
    }

    private void broadcastToOthers(int gameID, Session excludeSession, ServerMessage message) throws IOException {
        for (Session session : gameSessions.getOrDefault(gameID, new HashSet<>())) {
            if (!session.equals(excludeSession)) {
                sendMessage(session, message);
            }
        }
    }

    private void sendErrorMessage(Session session, String error) throws IOException {
        sendMessage(session, new ServerMessage(ServerMessage.ServerMessageType.ERROR, error));
    }
}
