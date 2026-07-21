package service;

import chess.ChessGame;
import dataaccess.MemoryDataAccess;
import exception.ServiceException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JoingameServiceTest {

    @Test
    public void joinGameSuccess() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);
        CreategameService creategameService = new CreategameService(dataAccess, dataAccess);
        JoingameService joingameService = new JoingameService(dataAccess, dataAccess);

        AuthData auth = registerService.register("user", "password", "user@email.com");

        int gameID = creategameService.createGame(auth.authToken(), "Test Game");
        joingameService.joinGame(auth.authToken(), ChessGame.TeamColor.WHITE, gameID);

        GameData updatedGame = dataAccess.getGame(gameID);
        Assertions.assertEquals("user", updatedGame.whiteUsername());
        Assertions.assertNull(updatedGame.blackUsername());
    }

    @Test
    public void joinGameColorAlreadyTaken() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);
        CreategameService creategameService = new CreategameService(dataAccess, dataAccess);
        JoingameService joingameService = new JoingameService(dataAccess, dataAccess);

        AuthData firstAuth = registerService.register("firstUser", "password", "first@email.com");
        AuthData secondAuth = registerService.register("secondUser", "password", "second@email.com");

        int gameID = creategameService.createGame(firstAuth.authToken(), "Test Game");
        joingameService.joinGame(firstAuth.authToken(), ChessGame.TeamColor.WHITE, gameID);

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> joingameService.joinGame(secondAuth.authToken(),
                ChessGame.TeamColor.WHITE, gameID));

        Assertions.assertEquals(403, exception.getStatusCode());
        Assertions.assertEquals("already taken", exception.getMessage());
    }
}