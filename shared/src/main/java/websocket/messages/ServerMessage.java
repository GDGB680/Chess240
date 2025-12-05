package websocket.messages;

import model.GameData;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    private GameData game;
    private String message;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {this.serverMessageType = type;}

    public ServerMessage(GameData game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.message = message;
    }

    public ServerMessageType getServerMessageType() {return this.serverMessageType;}
    public GameData getGame() {return game;}
    public String getMessage() {return message;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerMessage that)) return false;
        return getServerMessageType() == that.getServerMessageType() &&
                Objects.equals(getGame(), that.getGame()) &&
                Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getGame(), getMessage());
    }
}