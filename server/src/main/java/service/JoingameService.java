package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ServiceException;
import model.AuthData;
import model.GameData;

public class JoingameService extends ServiceHelper{

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public JoingameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void joinGame(String authToken, ChessGame.TeamColor playerColor, Integer gameID) throws DataAccessException, ServiceException {
        if (playerColor == null || gameID == null) {
            throw new ServiceException(400, "bad request");
        }
        AuthData auth = verifyAuth(authDAO,authToken);

        GameData game;
        try {
            game = gameDAO.getGame(gameID);
        }
        catch (DataAccessException e) {
            throw new ServiceException(400, "bad request");
        }

        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new ServiceException(403, "already taken");
            }
            game = new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game());
        }
        else {
            if (game.blackUsername() != null) {
                throw new ServiceException(403, "already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }
        gameDAO.updateGame(game);
    }
}
