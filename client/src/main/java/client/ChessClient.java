package client;


import chess.ChessGame;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketCommunicator;
import model.AuthData;
import model.GameData;
import ui.BoardDrawer;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private final WebSocketCommunicator websocket;
    private State state = State.SIGNED_OUT;
    private String authToken;
    private List<GameData> listedGames = new ArrayList<>();
    private int currentGameID;
    private ChessGame currentGame;
    private ChessGame.TeamColor perspective;
    private boolean observing;

    private enum State {
        SIGNED_OUT,
        SIGNED_IN,
        GAMEPLAY
    }

    public ChessClient(ServerFacade server, int port) {
        this.server = server;
        this.websocket = new WebSocketCommunicator(port, this);
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println("\nReceived: " + message.getServerMessageType());
    }

    public String eval(String input) {
        try {
            String trimmedInput = input.trim();
            if (trimmedInput.isEmpty()) {
                return help();
            }

            String[] tokens = trimmedInput.split("\\s+");
            String command = tokens[0].toLowerCase();

            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (command) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "create" -> createGame(params);
                case "list" -> listGames(params);
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);

                case "redraw" -> redraw(params);
                case "leave" -> leaveGame(params);
                case "move" -> makeMove(params);
                case "resign" -> resign(params);
                case "highlight" -> highlightMoves(params);

                case "help" -> help();
                case "quit" -> "quit";
                default -> "Unknown command. Type help to see available commands.";
            };

        }
        catch (ResponseException | IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
        catch (Exception e) {
            return "Error: unable to process that command.";
        }
    }

    public String help() {
        if (state == State.SIGNED_OUT) {
            return """
                register <USERNAME> <PASSWORD> <EMAIL> - create an account
                login <USERNAME> <PASSWORD> - sign in
                help - display available commands
                quit - exit the program
                """;
        }
        if (state == State.SIGNED_IN) {
            return """
                    create <GAME NAME> - create a chess game
                    list - list available games
                    play <GAME NUMBER> <WHITE|BLACK> - join a game
                    observe <GAME NUMBER> - observe a game
                    logout - sign out
                    help - display available commands
                    quit - exit the program
                    """;
        }
        return """
            redraw - redraw the chess board
            move <START> <END> - make a chess move
            highlight <POSITION> - highlight legal moves
            resign - resign from the game
            leave - leave the game
            help - show available commands
            quit - exit the program
            """;
    }

    private String redraw(String... params) {
        assertInGame();
        if (params.length != 0) {
            throw new IllegalArgumentException("Expected: redraw");
        }
        if (currentGame == null) {
            return "The game board has not loaded yet.";
        }
        return "Board redraw will be completed later.";
    }

    private String makeMove(String... params) {
        assertInGame();

        if (params.length != 2 && params.length != 3) {
            throw new IllegalArgumentException(
                    "Expected: move <START> <END> [PROMOTION]");
        }

        return "Making moves will be completed later.";
    }

    private String leaveGame(String... params) {
        assertInGame();

        if (params.length != 0) {
            throw new IllegalArgumentException(
                    "Expected: leave"
            );
        }

        return "Leaving the game will be completed later.";
    }

    private String resign(String... params) {
        assertInGame();

        if (params.length != 0) {
            throw new IllegalArgumentException(
                    "Expected: resign");
        }

        return "Resigning will be completed later.";
    }

    private String highlightMoves(String... params) {
        assertInGame();

        if (params.length != 1) {
            throw new IllegalArgumentException(
                    "Expected: highlight <POSITION>"
            );
        }

        if (currentGame == null) {
            return "The game board has not loaded yet.";
        }

        return "Legal-move highlighting will be completed in Step 11.";
    }


    private String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length != 3) {
            throw new IllegalArgumentException("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
        }

        AuthData authData = server.register(params[0], params[1], params[2]);

        authToken = authData.authToken();
        state = State.SIGNED_IN;

        return "Registered and signed in as " + authData.username() + ".\n"+help();
    }

    private String login(String...params) throws ResponseException {
        assertSignedOut();
        if (params.length != 2) {
            throw new IllegalArgumentException("Expected: login <USERNAME> <PASSWORD>");
        }

        AuthData authData = server.login(params[0], params[1]);

        authToken = authData.authToken();
        state = State.SIGNED_IN;
        return "Signed in as " + authData.username() + ".\n"+ help();
    }

    private String logout(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length != 0) {
            throw new IllegalArgumentException("Expected: logout");
        }

        server.logout(authToken);
        authToken = null;
        listedGames.clear();
        state = State.SIGNED_OUT;
        return "Signed out successfully.\n"+help();
    }
    private String createGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length == 0) {
            throw new IllegalArgumentException("Expected: create <GAME NAME>");
        }

        String gameName = String.join(" ", params);
        server.createGame(authToken, gameName);

        return "Created game: " + gameName + ".";
    }

    private String listGames(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length != 0) {
            throw new IllegalArgumentException("Expected: list");
        }

        listedGames = new ArrayList<>(server.listGames(authToken));

        if (listedGames.isEmpty()) {
            return "No games available.";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < listedGames.size(); i++) {
            GameData game = listedGames.get(i);

            String whitePlayer = game.whiteUsername() == null
                    ? "available"
                    : game.whiteUsername();

            String blackPlayer = game.blackUsername() == null
                    ? "available"
                    : game.blackUsername();

            result.append(i + 1)
                    .append(". ")
                    .append(game.gameName())
                    .append(" | White: ")
                    .append(whitePlayer)
                    .append(" | Black: ")
                    .append(blackPlayer);

            if (i < listedGames.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }
    private String playGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length != 2) {
            throw new IllegalArgumentException("Expected: play <GAME NUMBER> <WHITE|BLACK>");
        }

        if (listedGames.isEmpty()) {
            throw new IllegalArgumentException("Run list before playing a game.");
        }

        int gameNumber;

        try {
            gameNumber = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Game number must be an integer.");
        }

        if (gameNumber < 1 || gameNumber > listedGames.size()) {
            throw new IllegalArgumentException("Invalid game number.");
        }

        ChessGame.TeamColor color;

        try {
            color = ChessGame.TeamColor.valueOf(
                    params[1].toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Color must be WHITE or BLACK.");
        }

        GameData game = listedGames.get(gameNumber - 1);
        server.joinGame(authToken, game.gameID(), color);

        connectToGame(game.gameID());
        currentGameID = game.gameID();
        perspective = color;
        observing = false;
        state = State.GAMEPLAY;

        return "Connected to " + game.gameName() + " as " + color + ".";
    }

    private String observeGame(String... params) throws ResponseException {
        assertSignedIn();

        if (params.length != 1) {
            throw new IllegalArgumentException(
                    "Expected: observe <GAME NUMBER>");
        }

        if (listedGames.isEmpty()) {
            throw new IllegalArgumentException(
                    "Run list before observing a game.");
        }

        int gameNumber;
        try {
            gameNumber = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Game number must be an integer.");
        }

        if (gameNumber < 1 || gameNumber > listedGames.size()) {
            throw new IllegalArgumentException("Invalid game number.");
        }

        GameData game = listedGames.get(gameNumber - 1);

        connectToGame(game.gameID());
        currentGameID = game.gameID();
        perspective = ChessGame.TeamColor.WHITE;
        observing = true;
        state = State.GAMEPLAY;

        return "Observing " + game.gameName() + ".";
    }

    private void connectToGame(int gameID) throws ResponseException {
        websocket.connect();
        UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        websocket.send(connectCommand);
    }

    private void assertSignedIn() {
        if (state != State.SIGNED_IN) {
            throw new IllegalArgumentException("You must sign in first.");
        }
    }

    private void assertSignedOut() {
        if (state != State.SIGNED_OUT) {
            throw new IllegalArgumentException("You are already signed in.");
        }
    }

    private void assertInGame(){
        if (state != State.GAMEPLAY) {
            throw new IllegalArgumentException(
                    "You must be in a game first.");
        }
    }
}
