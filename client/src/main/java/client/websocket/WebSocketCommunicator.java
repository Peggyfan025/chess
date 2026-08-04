package client.websocket;

import client.ResponseException;
import com.google.gson.Gson;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import org.glassfish.tyrus.client.ClientManager;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebSocketCommunicator {
    private final Gson serializer = new Gson();
    private final ServerMessageObserver observer;
    private final URI serverUri;

    private jakarta.websocket.Session session;

    public WebSocketCommunicator(int port, ServerMessageObserver observer) {
        this.observer = observer;
        this.serverUri =
                URI.create("ws://localhost:" + port + "/ws");
    }

    public void connect() throws ResponseException {
        if (isOpen()) {
            return;
        }

        try {
            ClientManager client = ClientManager.createClient();
            session = client.connectToServer(this, serverUri);

        } catch (Exception exception) {
            throw new ResponseException("Unable to connect to the game server.");
        }
    }

    @OnOpen
    public void onOpen(jakarta.websocket.Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String json) {

        try {
            ServerMessage message = serializer.fromJson(json, ServerMessage.class);
            observer.notify(message);

        }
        catch (Exception exception) {
            System.out.println("Error: Unable to read server message.");
        }
    }

    @OnClose
    public void onClose(jakarta.websocket.Session session) {
        this.session = null;
    }

    public void send(UserGameCommand command)
            throws ResponseException {

        if (!isOpen()) {
            throw new ResponseException("Game connection is not open.");
        }

        try {
            String json = serializer.toJson(command);
            session.getBasicRemote().sendText(json);

        }
        catch (IOException exception) {
            throw new ResponseException("Unable to send the game command.");
        }
    }

    public void close() throws ResponseException {
        if (!isOpen()) {
            return;
        }

        try {
            session.close();
            session = null;

        }
        catch (IOException exception) {
            throw new ResponseException("Unable to close the game connection.");
        }
    }

    public boolean isOpen() {
        return session != null && session.isOpen();
    }
}
