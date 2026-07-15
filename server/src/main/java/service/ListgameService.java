package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ServiceException;
import model.GameData;

import java.util.Collection;

public class ListgameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListgameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException, ServiceException {
        if (authToken == null) {
            throw new ServiceException(401, "unauthorized");
        }

        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceException(401, "unauthorized");
        }

        return gameDAO.listGames();
    }
}
