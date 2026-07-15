package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ServiceException;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logout(String authToken) throws DataAccessException, ServiceException {
        if (authToken == null) {
            throw new ServiceException(401, "unauthorized");
        }

        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceException(401, "unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}
