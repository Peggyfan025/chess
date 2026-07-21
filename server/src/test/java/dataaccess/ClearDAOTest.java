package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearDAOTest {
    @Test
    public void clearPositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();

        dao.createUser(new UserData("alice", "password", "alice@email.com"));
        dao.createAuth(new AuthData("token", "alice"));
        dao.createGame(new GameData(
                0,
                null,
                null,
                "Test Game",
                new ChessGame()
        ));

        dao.clear();

        assertNull(dao.getUser("alice"));
        assertNull(dao.getAuth("token"));
        assertTrue(dao.listGames().isEmpty());
    }

}
