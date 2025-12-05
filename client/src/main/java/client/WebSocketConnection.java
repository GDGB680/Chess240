package client;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;
import java.util.concurrent.CountDownLatch;


@WebSocket
public class WebSocketConnection {
    private Session session;
    private final Gson gson = new Gson();
    private final WebSocketMessageListener listener;
    private static final CountDownLatch connectLatch = new CountDownLatch(1);

    public interface WebSocketMessageListener {
        void onServerMessage(ServerMessage message);
    }

    public WebSocketConnection(String serverUrl, WebSocketMessageListener listener) throws Exception {
        this.listener = listener;

        WebSocketClient client = new WebSocketClient();
        client.start();

        URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
        client.connect(this, uri);
        connectLatch.await();
    }
}