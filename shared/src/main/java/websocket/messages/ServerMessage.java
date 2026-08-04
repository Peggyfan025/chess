package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;

    // Used by LOAD_GAME
    private ChessGame game;

    // Used by ERROR
    private String errorMessage;

    // Used by NOTIFICATION
    private String message;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    private ServerMessage(ServerMessageType type, ChessGame game, String errorMessage,String message){
        this.serverMessageType = type;
        this.game = game;
        this.errorMessage = errorMessage;
        this.message = message;
    }

    public static ServerMessage loadGame(ChessGame game) {
        return new ServerMessage(ServerMessageType.LOAD_GAME, game, null, null);
    }

    public static ServerMessage error(String errorMessage){
        if (!errorMessage.toLowerCase().contains("error")){
            errorMessage = "Error: " + errorMessage;
        }
        return new ServerMessage(ServerMessageType.ERROR,null,errorMessage,null);
    }

    public static ServerMessage notification(String message) {
        return new ServerMessage(ServerMessageType.NOTIFICATION, null, null, message);
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public ChessGame getGame() {
        return game;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}
