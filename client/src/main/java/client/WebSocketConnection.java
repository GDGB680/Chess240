package client;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@WebSocket
public class WebSocketConnection {
    private Session session;
    private final Gson gson = new Gson();
    private final WebSocketMessageListener listener;
    private final CountDownLatch connectLatch;
    private final WebSocketClient client;

    public interface WebSocketMessageListener {
        void onServerMessage(ServerMessage message);
    }

    public WebSocketConnection(String serverUrl, WebSocketMessageListener listener) throws Exception {
        this.listener = listener;
        this.connectLatch = new CountDownLatch(1);
        client = new WebSocketClient();
        client.start();
        URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
        client.connect(this, uri);
        if (!connectLatch.await(5, TimeUnit.SECONDS)) {
            client.stop();
            throw new Exception("WebSocket connection timeout");
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        connectLatch.countDown();
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        try {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            listener.onServerMessage(serverMessage);
        } catch (Exception e) {
            System.err.println("Error deserializing message: " + e.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket closed: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    public void send(UserGameCommand command) throws Exception {
        if (session != null && session.isOpen()) {
            session.getRemote().sendString(gson.toJson(command));
        } else {
            throw new Exception("WebSocket session is not open");
        }
    }

    public void close() throws Exception {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } finally {
            if (client != null) {
                client.stop();
            }
        }
    }
}