package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ServiceException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class RegisterService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO,AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(String username, String password, String email) throws DataAccessException, ServiceException {
        if (username == null || password == null || email == null) {
            throw new ServiceException(400, "bad request");
        }
        UserData existingUser = userDAO.getUser(username);

        if (existingUser != null) {
            throw new ServiceException(403, "already taken");
        }

        UserData new_user = new UserData(username,password,email);
        userDAO.createUser(new_user);

        String authToken = UUID.randomUUID().toString();
        AuthData new_auth = new AuthData(authToken,username);
        authDAO.createAuth(new_auth);
        return new_auth;
    }
}
