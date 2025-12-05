package client;

public WebSocketConnection(String serverUrl, WebSocketMessageListener listener) throws Exception {
    this.listener = listener;

    WebSocketClient client = new WebSocketClient();
    client.start();

    URI uri = new URI(serverUrl.replace("http", "ws") + "/ws");
    client.connect(this, uri);
    connectLatch.await();
}