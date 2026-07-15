package service;

import dataaccess.MemoryDataAccess;
import exception.ServiceException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LoginServiceTest {

    @Test
    public void loginSuccess() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);
        LoginService loginService = new LoginService(dataAccess, dataAccess);

        registerService.register("user", "password", "user@email.com");
        AuthData auth = loginService.login("user", "password");

        Assertions.assertNotNull(auth);
        Assertions.assertEquals("user", auth.username());
        Assertions.assertNotNull(auth.authToken());

        Assertions.assertNotNull(dataAccess.getAuth(auth.authToken()));
    }

    @Test
    public void loginUnauthorized() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);
        LoginService loginService = new LoginService(dataAccess, dataAccess);

        registerService.register("user", "password", "user@email.com");
        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> loginService.login("user", "wrongPassword"));
        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }

}
