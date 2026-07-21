package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    @Test
    public void createUserPositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        UserData expectedUser = new UserData("alice", "password123", "alice@email.com");

        dao.createUser(expectedUser);

        UserData actualUser = dao.getUser("alice");

        assertNotNull(actualUser);
        assertEquals(expectedUser.username(), actualUser.username());
        assertEquals(expectedUser.email(), actualUser.email());
        assertTrue(BCrypt.checkpw(expectedUser.password(), actualUser.password()));
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        UserData firstUser =
                new UserData("alice", "password123", "alice@email.com");

        UserData duplicateUser =
                new UserData("alice", "differentPassword", "other@email.com");

        dao.createUser(firstUser);

        assertThrows(
                DataAccessException.class,
                () -> dao.createUser(duplicateUser)
        );
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        UserData expectedUser =
                new UserData("bob", "password456", "bob@email.com");

        dao.createUser(expectedUser);

        UserData actualUser = dao.getUser("bob");

        assertNotNull(actualUser);
        assertEquals("bob", actualUser.username());
        assertEquals("bob@email.com", actualUser.email());
        assertNotNull(actualUser.password());
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        MySqlDataAccess dao = new MySqlDataAccess();
        dao.clear();

        UserData actualUser = dao.getUser("nonexistent-user");

        assertNull(actualUser);
    }
}
