package handler;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import model.GameData;
import chess.ChessGame;

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

    
}
