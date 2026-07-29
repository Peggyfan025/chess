package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import model.GameData;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private final Gson serializer = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        return makeRequest("POST", "/user", Map.of("username", username, "password", password, "email", email), AuthData.class, null);
    }
    public AuthData login(String username, String password) throws ResponseException {
        return makeRequest("POST", "/session", Map.of("username", username, "password", password), AuthData.class, null);
    }
    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, null, authToken);
    }
    public int createGame(String authToken, String gameName) throws ResponseException {
        JsonObject response = makeRequest("POST", "/game", Map.of("gameName", gameName), JsonObject.class, authToken);
        return serializer.fromJson(response.get("gameID"), Integer.class);
    }
    public Collection<GameData> listGames(String authToken) throws ResponseException {
        JsonObject response = makeRequest("GET", "/game", null, JsonObject.class, authToken);
        GameData[] games = serializer.fromJson(response.get("games"), GameData[].class);
        return Arrays.asList(games);
    }
    public void joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        makeRequest("PUT", "/game", Map.of("playerColor", color, "gameID", gameID), null, authToken);
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db",null,null,null);
    }

    private <T>  T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(serverUrl + path).toURL().openConnection();
            connection.setRequestMethod(method);

            if (authToken != null) {
                connection.setRequestProperty("authorization", authToken);
            }

            if (request != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    serializer.toJson(request, writer);
                }
            }

            int statusCode = connection.getResponseCode();

            if (statusCode != 200) {
                try (InputStreamReader reader = new InputStreamReader(connection.getErrorStream())) {
                    JsonObject error = serializer.fromJson(reader, JsonObject.class);
                    String message = "Request failed.";

                    if (error != null && error.has("message")) {
                        message = error.get("message").getAsString();
                    }


                    if (message.startsWith("Error: ")) {
                        message = message.substring(7);
                    }

                    throw new ResponseException(message);
                }
            }

            if (responseClass == null) {
                return null;
            }

            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                return serializer.fromJson(reader, responseClass);
            }
        }
        catch (ResponseException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ResponseException(e.getMessage());
        }
    }
}
