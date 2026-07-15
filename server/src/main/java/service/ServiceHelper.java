package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import exception.ServiceException;
import model.AuthData;

public abstract class ServiceHelper {

    protected AuthData verifyAuth(AuthDAO authDAO, String authToken)
            throws ServiceException {

        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new ServiceException(401, "unauthorized");
        }
    }
}
