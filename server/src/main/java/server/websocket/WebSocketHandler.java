package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.UserGameCommand;

public class WebSocketHandler {
    private final Gson serializer = new Gson();
    private final ConnectionManager connections;

    public WebSocketHandler(ConnectionManager connections) {
        this.connections = connections;
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
                case CONNECT -> System.out.println("CONNECT command received");
                case MAKE_MOVE -> System.out.println("MAKE_MOVE command received");
                case LEAVE -> System.out.println("LEAVE command received");
                case RESIGN -> System.out.println("RESIGN command received");
            }

        }
        catch (Exception exception) {
            System.out.println("Unable to read WebSocket command: " + exception.getMessage());
        }
    }
}
