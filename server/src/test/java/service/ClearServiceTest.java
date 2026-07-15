package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTest {
    @Test
    public void clearSuccess() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        ClearService clearService = new ClearService(dataAccess);
        UserData user = new UserData("test1","password", "xxx@email.com");
        AuthData auth = new AuthData("token",user.username());
        GameData game = new GameData(1,null,null,"game",new ChessGame());

        dataAccess.createUser(user);
        dataAccess.createAuth(auth);
        dataAccess.createGame(game);

        clearService.clear();

        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.getUser(user.username()));
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.getAuth(auth.authToken()));
        Assertions.assertTrue(dataAccess.listGames().isEmpty(),"games where not removed");
    }
}
