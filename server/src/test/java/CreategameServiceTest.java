import dataaccess.MemoryDataAccess;
import exception.ServiceException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.CreategameService;
import service.RegisterService;

public class CreategameServiceTest {

    @Test
    public void createGameSuccess() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);
        CreategameService createGameService = new CreategameService(dataAccess, dataAccess);

        AuthData auth = registerService.register("user", "password", "user@email.com");
        int gameID = createGameService.createGame(auth.authToken(), "Test Game");

        Assertions.assertTrue(gameID > 0);
        GameData game = dataAccess.getGame(gameID);
        Assertions.assertNotNull(game);
        Assertions.assertEquals("Test Game", game.gameName());
    }

    @Test
    public void createGameUnauthorized() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        CreategameService createGameService = new CreategameService(dataAccess, dataAccess);
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> createGameService.createGame("badToken", "Test Game"));

        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }
}
