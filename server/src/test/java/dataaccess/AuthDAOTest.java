package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {
    @Test
    public void createAuthPositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        AuthData expectedAuth = new AuthData("positive-create-token", "alice");
        dao.createAuth(expectedAuth);

        AuthData actualAuth = dao.getAuth("positive-create-token");

        assertNotNull(actualAuth);
        assertEquals(expectedAuth, actualAuth);
    }

    @Test
    public void createAuthNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        AuthData firstAuth =
                new AuthData("duplicate-token", "alice");

        AuthData duplicateAuth =
                new AuthData("duplicate-token", "bob");

        dao.createAuth(firstAuth);

        assertThrows(
                DataAccessException.class,
                () -> dao.createAuth(duplicateAuth)
        );
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        AuthData expectedAuth =
                new AuthData("positive-get-token", "charlie");

        dao.createAuth(expectedAuth);

        AuthData actualAuth =
                dao.getAuth("positive-get-token");

        assertEquals(expectedAuth, actualAuth);
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        AuthData actualAuth =
                dao.getAuth("nonexistent-token");

        assertNull(actualAuth);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        AuthData auth =
                new AuthData("delete-token", "david");

        dao.createAuth(auth);
        assertNotNull(dao.getAuth("delete-token"));

        dao.deleteAuth("delete-token");

        assertNull(dao.getAuth("delete-token"));
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        assertDoesNotThrow(
                () -> dao.deleteAuth("nonexistent-token")
        );

        assertNull(dao.getAuth("nonexistent-token"));
    }
}

