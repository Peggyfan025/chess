package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson serializer = new Gson();

    public void add(String username, int gameID, WsContext context) {
        Connection connection = new Connection(username, gameID, context);
        connections.put(context.sessionId(), connection);
    }

    public void remove(WsContext context) {
        connections.remove(context.sessionId());
    }

    public void send(WsContext context, Object message) {
        if (context.session.isOpen()) {
            context.send(serializer.toJson(message));
        }
    }

    public void broadcast(int gameID, Object message, String excludedSessionID) {
        String json = serializer.toJson(message);

        for (Connection connection : connections.values()) {
            boolean sameGame = connection.gameID() == gameID;
            boolean excluded = excludedSessionID != null
                    && excludedSessionID.equals(connection.context().sessionId());

            if (sameGame && !excluded && connection.context().session.isOpen()) {
                connection.context().send(json);
            }
        }
    }

    public record Connection(String username, int gameID, WsContext context) {}
}
