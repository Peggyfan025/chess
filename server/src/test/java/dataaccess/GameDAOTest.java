package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {
    @Test
    public void createGamePositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        GameData gameToCreate = new GameData(
                0,
                null,
                null,
                "Test Game",
                new ChessGame()
        );

        int gameID = dao.createGame(gameToCreate);
        GameData actualGame = dao.getGame(gameID);

        assertNotNull(actualGame);
        assertEquals(gameID, actualGame.gameID());
        assertEquals("Test Game", actualGame.gameName());
        assertNull(actualGame.whiteUsername());
        assertNull(actualGame.blackUsername());
        assertNotNull(actualGame.game());
    }

    @Test
    public void createGameNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        GameData invalidGame = new GameData(
                0,
                null,
                null,
                null,
                new ChessGame()
        );

        assertThrows(
                DataAccessException.class,
                () -> dao.createGame(invalidGame)
        );
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        GameData expectedGame = new GameData(
                0,
                null,
                null,
                "Get Game Test",
                new ChessGame()
        );

        int gameID = dao.createGame(expectedGame);
        GameData actualGame = dao.getGame(gameID);

        assertNotNull(actualGame);
        assertEquals(gameID, actualGame.gameID());
        assertEquals(expectedGame.gameName(), actualGame.gameName());
        assertEquals(expectedGame.whiteUsername(), actualGame.whiteUsername());
        assertEquals(expectedGame.blackUsername(), actualGame.blackUsername());
        assertEquals(expectedGame.game(), actualGame.game());
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        GameData actualGame = dao.getGame(999999);

        assertNull(actualGame);
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        dao.createGame(new GameData(
                0,
                null,
                null,
                "Game One",
                new ChessGame()
        ));

        dao.createGame(new GameData(
                0,
                null,
                null,
                "Game Two",
                new ChessGame()
        ));

        Collection<GameData> games = dao.listGames();

        assertNotNull(games);
        assertEquals(2, games.size());

        assertTrue(
                games.stream().anyMatch(
                        game -> game.gameName().equals("Game One")
                )
        );

        assertTrue(
                games.stream().anyMatch(
                        game -> game.gameName().equals("Game Two")
                )
        );
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        Collection<GameData> games = dao.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        GameData originalGame = new GameData(
                0,
                null,
                null,
                "Update Test",
                new ChessGame()
        );

        int gameID = dao.createGame(originalGame);

        GameData updatedGame = new GameData(
                gameID,
                "alice",
                "bob",
                "Update Test",
                new ChessGame()
        );

        dao.updateGame(updatedGame);

        GameData actualGame = dao.getGame(gameID);

        assertNotNull(actualGame);
        assertEquals(updatedGame.gameID(), actualGame.gameID());
        assertEquals(updatedGame.gameName(), actualGame.gameName());
        assertEquals("alice", actualGame.whiteUsername());
        assertEquals("bob", actualGame.blackUsername());
        assertEquals(updatedGame.game(), actualGame.game());
    }

    @Test
    public void updateGameNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        GameData nonexistentGame = new GameData(
                999999,
                "alice",
                "bob",
                "Does Not Exist",
                new ChessGame()
        );

        dao.updateGame(nonexistentGame);

        assertNull(dao.getGame(999999));
        assertTrue(dao.listGames().isEmpty());
    }
}
