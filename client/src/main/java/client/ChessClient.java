package client;


import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessClient {
    private final ServerFacade server;
    private State state = State.SIGNED_OUT;
    private String authToken;
    private List<GameData> listedGames = new ArrayList<>();

    private enum State {
        SIGNED_OUT,
        SIGNED_IN
    }

    public ChessClient(ServerFacade server) {
        this.server = server;
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
                case "help" -> help();
                case "quit" -> "quit";
                default -> "Unknown command. Type help to see available commands.";
            };

        }
        catch (ResponseException | IllegalArgumentException e) {
            return "Error: " + e.getMessage();
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

    private String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length != 3) {
            throw new IllegalArgumentException("Expected: register <USERNAME> <PASSWORD> <EMAIL>");
        }

        AuthData authData = server.register(params[0], params[1], params[2]);

        authToken = authData.authToken();
        state = State.SIGNED_IN;

        return "Registered and signed in as " + authData.username() + ".";
    }

    private String login(String...params) throws ResponseException {
        assertSignedOut();
        if (params.length != 2) {
            throw new IllegalArgumentException("Expected: login <USERNAME> <PASSWORD>");
        }

        AuthData authData = server.login(params[0], params[1]);

        authToken = authData.authToken();
        state = State.SIGNED_IN;
        return "Signed in as " + authData.username() + ".";
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
        return "Signed out successfully.";
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
}
