package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @Test
    void registerPositive() throws ResponseException {
        AuthData authData = facade.register("registerUser", "password", "register@email.com");

        assertNotNull(authData);
        assertEquals("registerUser", authData.username());
        assertNotNull(authData.authToken());
        assertFalse(authData.authToken().isBlank());
    }

    @Test
    void registerNegative() throws ResponseException {
        facade.register("duplicateUser", "password", "first@email.com");

        assertThrows(
                ResponseException.class,
                () -> facade.register("duplicateUser", "password", "second@email.com"));
    }


    @Test
    void loginPositive() throws ResponseException {
        facade.register("loginUser", "password", "login@email.com");

        AuthData authData = facade.login("loginUser", "password");

        assertNotNull(authData);
        assertEquals("loginUser", authData.username());
        assertNotNull(authData.authToken());
        assertFalse(authData.authToken().isBlank());
    }

    @Test
    void loginNegative() throws ResponseException {
        facade.register("loginUser", "correctPassword", "login@email.com");

        assertThrows(ResponseException.class,
                () -> facade.login(
                        "loginUser", "wrongPassword"));
    }


    @Test
    void logoutPositive() throws ResponseException {
        AuthData authData = facade.register("logoutUser", "password", "logout@email.com");

        assertDoesNotThrow(
                () -> facade.logout(authData.authToken()));

        // The token should no longer be authorized.
        assertThrows(ResponseException.class,
                () -> facade.listGames(authData.authToken()));
    }

    @Test
    void logoutNegative() {
        assertThrows(
                ResponseException.class,
                () -> facade.logout("invalid-auth-token")
        );
    }

    @Test
    void createGamePositive() throws ResponseException {
        AuthData authData = facade.register("createUser", "password", "create@email.com");

        int gameID = facade.createGame(
                authData.authToken(), "My Chess Game");

        assertTrue(gameID > 0);
    }

    @Test
    void createGameNegative() {
        assertThrows(ResponseException.class,
                () -> facade.createGame("invalid-auth-token", "Unauthorized Game"));
    }

    @Test
    void listGamesPositive() throws ResponseException {
        AuthData authData = facade.register("listUser", "password", "list@email.com");

        int gameID = facade.createGame(authData.authToken(), "Listed Game");

        Collection<GameData> games = facade.listGames(authData.authToken());

        assertNotNull(games);
        assertEquals(1, games.size());

        GameData game = games.iterator().next();

        assertEquals(gameID, game.gameID());
        assertEquals("Listed Game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void listGamesNegative() {
        assertThrows(ResponseException.class,
                () -> facade.listGames("invalid-auth-token"));
    }

    @Test
    void joinGamePositive() throws ResponseException {
        AuthData owner = facade.register("gameOwner", "password", "owner@email.com");

        int gameID = facade.createGame(owner.authToken(), "Joinable Game");

        AuthData player = facade.register("whitePlayer", "password", "player@email.com");

        assertDoesNotThrow(() -> facade.joinGame(
                        player.authToken(), gameID, ChessGame.TeamColor.WHITE));

        Collection<GameData> games = facade.listGames(player.authToken());

        GameData game = games.iterator().next();
        assertEquals("whitePlayer", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameNegative() throws ResponseException {
        AuthData authData = facade.register(
                "joinUser", "password", "join@email.com");

        assertThrows(ResponseException.class,
                () -> facade.joinGame(
                        authData.authToken(), 999999, ChessGame.TeamColor.WHITE));
    }


    @Test
    void clearPositive() throws ResponseException {
        facade.register("clearUser", "password", "clear@email.com");

        assertDoesNotThrow(() -> facade.clear());
        // Confirm that the registered user was removed.
        assertThrows(ResponseException.class, () -> facade.login("clearUser", "password"));
    }
}
