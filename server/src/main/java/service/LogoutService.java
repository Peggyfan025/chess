package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ServiceException;
import model.AuthData;

public class LogoutService extends ServiceHelper{
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws DataAccessException, ServiceException {
        if (authToken == null) {
            throw new ServiceException(401, "unauthorized");
        }

        AuthData auth = verifyAuth(authDAO,authToken);
        authDAO.deleteAuth(authToken);
    }
}
