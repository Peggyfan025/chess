package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ServiceException;
import model.GameData;

public class CreategameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreategameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public int createGame(String authToken, String gameName) throws DataAccessException, ServiceException {
        if (gameName == null) {
            throw new ServiceException(400, "bad request");
        }

        if (authToken == null) {
            throw new ServiceException(401, "unauthorized");
        }

        try {
            authDAO.getAuth(authToken);
        }
        catch (DataAccessException e) {
            throw new ServiceException(401, "unauthorized");
        }

        GameData gameData = new GameData(0, null, null, gameName, new ChessGame());
        return gameDAO.createGame(gameData);
    }
}
