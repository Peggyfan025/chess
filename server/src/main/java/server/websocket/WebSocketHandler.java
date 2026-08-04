package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketHandler {
    private final Gson serializer = new Gson();
    private final ConnectionManager connections;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(ConnectionManager connections, AuthDAO authDAO, GameDAO gameDAO) {
        this.connections = connections;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void configure(WsConfig ws) {
        ws.onConnect(ctx ->
                System.out.println("WebSocket opened: " + ctx.sessionId()));

        ws.onMessage(this::handleMessage);

        ws.onClose(ctx -> {
            connections.remove(ctx);
            System.out.println("WebSocket closed: " + ctx.sessionId());
        });

        ws.onError(ctx ->
                System.out.println("WebSocket error: " + ctx.error().getMessage())
        );
    }

    private void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);

            if (command == null || command.getCommandType() == null) {
                System.out.println("Received invalid WebSocket command.");
                return;
            }
            //actual function to be added
            switch (command.getCommandType()) {
                case CONNECT -> connect(ctx,command);
                case MAKE_MOVE -> System.out.println("MAKE_MOVE command received");
                case LEAVE -> System.out.println("LEAVE command received");
                case RESIGN -> System.out.println("RESIGN command received");
            }

        }
        catch (Exception exception) {
            System.out.println("Unable to read WebSocket command: " + exception.getMessage());
        }
    }

    private void connect(WsMessageContext context, UserGameCommand command) {
        try {
            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();

            if (authToken == null || authToken.isBlank()) {
                sendError(context, "Invalid authentication token.");
                return;
            }

            if (gameID == null) {
                sendError(context, "Invalid game ID.");
                return;
            }

            AuthData authData = authDAO.getAuth(authToken);

            if (authData == null) {
                sendError(context, "Invalid authentication token.");
                return;
            }

            GameData gameData = gameDAO.getGame(gameID);

            if (gameData == null) {
                sendError(context, "Game does not exist.");
                return;
            }

            String username = authData.username();

            String notification;

            if (username.equals(gameData.whiteUsername())) {
                notification = username + " connected as WHITE.";
            }
            else if (username.equals(gameData.blackUsername())) {
                notification = username + " connected as BLACK.";

            }
            else {
                notification = username + " connected as an observer.";
            }

            connections.add(username, gameID, context);
            ServerMessage loadGameMessage = ServerMessage.loadGame(gameData.game());
            connections.send(context, loadGameMessage);

            ServerMessage notificationMessage = ServerMessage.notification(notification);
            connections.broadcast(gameID, notificationMessage, context.sessionId());

        }
        catch (DataAccessException exception) {
            sendError(context, "Unable to access the game.");
        }
    }

    private void sendError(WsMessageContext context, String message) {
        ServerMessage errorMessage = ServerMessage.error(message);
        connections.send(context, errorMessage);
    }
}
