package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ServiceException;
import model.AuthData;

public abstract class ServiceHelper {

    protected AuthData verifyAuth(AuthDAO authDAO, String authToken)
            throws ServiceException, DataAccessException {

        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new ServiceException(401, "unauthorized");
        }

        return auth;
    }
}
