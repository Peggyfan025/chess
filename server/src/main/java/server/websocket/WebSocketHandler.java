package server.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
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
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = serializer.fromJson(
                                ctx.message(), MakeMoveCommand.class);
                    makeMove(ctx, moveCommand);
                }
                case LEAVE -> leave(ctx, command);
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

    private void makeMove(
            WsMessageContext ctx,
            MakeMoveCommand command) {

        try {
            if (command == null) {
                sendError(ctx, "Invalid move command.");
                return;
            }

            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();
            chess.ChessMove move = command.getMove();

            if (authToken == null || authToken.isBlank()) {
                sendError(ctx, "Invalid authentication token.");
                return;
            }

            if (gameID == null) {
                sendError(ctx, "Invalid game ID.");
                return;
            }

            if (move == null) {
                sendError(ctx, "A move was not provided.");
                return;
            }

            AuthData authData = authDAO.getAuth(authToken);

            if (authData == null) {
                sendError(ctx, "Invalid authentication token.");
                return;
            }

            GameData gameData = gameDAO.getGame(gameID);

            if (gameData == null) {
                sendError(ctx, "Game does not exist.");
                return;
            }

            String username = authData.username();
            ChessGame.TeamColor playerColor;

            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;

            }
            else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }
            else {
                sendError(ctx, "Observers cannot make moves.");
                return;
            }

            ChessGame game = gameData.game();

            if (game.getTeamTurn() != playerColor) {
                sendError(ctx, "It is not your turn.");
                return;
            }

            game.makeMove(move);
            gameDAO.updateGame(gameData);
            ServerMessage loadGameMessage = ServerMessage.loadGame(game);
            connections.broadcast(gameID, loadGameMessage, null);

            String moveDescription = username + " moved " + positionToString(move.getStartPosition())
                    + " to " + positionToString(move.getEndPosition()) + ".";

            ServerMessage moveNotification = ServerMessage.notification(moveDescription);
            connections.broadcast(gameID, moveNotification, ctx.sessionId());
            sendGameStatusNotifications(gameID, gameData);
        }
        catch (InvalidMoveException exception) {
            sendError(ctx, "Invalid move: " + exception.getMessage());
        }
        catch (DataAccessException exception) {
            sendError(ctx, "Unable to update the game.");
        }
    }

    private void leave(WsMessageContext ctx, UserGameCommand command) {
        try {
            String authToken = command.getAuthToken();
            Integer gameID = command.getGameID();

            if (authToken == null || authToken.isBlank()) {
                sendError(ctx, "Invalid authentication token.");
                return;
            }

            if (gameID == null) {
                sendError(ctx, "Invalid game ID.");
                return;
            }

            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                sendError(ctx, "Invalid authentication token.");
                return;
            }

            GameData gameData = gameDAO.getGame(gameID);

            if (gameData == null) {
                sendError(ctx, "Game does not exist.");
                return;
            }

            String username = authData.username();
            String whiteUsername = gameData.whiteUsername();
            String blackUsername = gameData.blackUsername();

            boolean gameChanged = false;

            if (username.equals(whiteUsername)) {
                whiteUsername = null;
                gameChanged = true;

            }
            else if (username.equals(blackUsername)) {
                blackUsername = null;
                gameChanged = true;
            }

            if (gameChanged) {
                GameData updatedGame = new GameData(gameData.gameID(), whiteUsername,
                    blackUsername, gameData.gameName(), gameData.game());
                gameDAO.updateGame(updatedGame);
            }

            connections.remove(ctx);
            ServerMessage notification = ServerMessage.notification(username + " left the game.");
            connections.broadcast(gameID, notification, null);
        }
        catch (DataAccessException exception) {
            sendError(ctx, "Unable to leave the game.");
        }
    }

    private String positionToString(ChessPosition position) {
        char column = (char) ('a' + position.getColumn() - 1);
        return String.valueOf(column) + position.getRow();
    }

    private void sendGameStatusNotifications(int gameID, GameData gameData) {
        ChessGame game = gameData.game();
        ChessGame.TeamColor team = game.getTeamTurn();
        String username = getPlayerName(gameData, team);

        String statusMessage = null;
        if (game.isInCheckmate(team)) {
            statusMessage = username + " is in checkmate.";
        }
        else if (game.isInStalemate(team)) {
            statusMessage = "The game ended in stalemate.";
        }
        else if (game.isInCheck(team)) {
            statusMessage = username + " is in check.";
        }

        if (statusMessage != null) {
            connections.broadcast(gameID, ServerMessage.notification(statusMessage), null);
        }
    }

    private String getPlayerName(GameData gameData, ChessGame.TeamColor color) {
        String username;

        if (color == ChessGame.TeamColor.WHITE) {
            username = gameData.whiteUsername();
        }
        else {
            username = gameData.blackUsername();
        }

        if (username == null) {
            return color == ChessGame.TeamColor.WHITE ? "White" : "Black";
        }
        return username;
    }

    private void sendError(WsMessageContext context, String message) {
        ServerMessage errorMessage = ServerMessage.error(message);
        connections.send(context, errorMessage);
    }
}
