import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import exception.ServiceException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.ListgameService;
import service.RegisterService;

import java.util.Collection;

public class ListgameServiceTest {
    @Test
    public void listGamesSuccess() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);
        ListgameService listgameService = new ListgameService(dataAccess, dataAccess);

        AuthData auth = registerService.register("user", "password", "user@email.com");
        dataAccess.createGame(new GameData(1, null, null, "Game One", new ChessGame()));
        dataAccess.createGame(new GameData(2, null, null, "Game Two", new ChessGame()));

        Collection<GameData> games = listgameService.listGames(auth.authToken());
        Assertions.assertEquals(2, games.size());
    }

    @Test
    public void listGamesUnauthorized() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        ListgameService listgameService = new ListgameService(dataAccess, dataAccess);
        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> listgameService.listGames("badToken"));

        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }
}