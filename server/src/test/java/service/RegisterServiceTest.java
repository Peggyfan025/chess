package service;

import dataaccess.MemoryDataAccess;
import exception.ServiceException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegisterServiceTest {

    @Test
    public void registerSuccess() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);

        AuthData auth = registerService.register("user", "password", "user@email.com");
        Assertions.assertNotNull(auth);
        Assertions.assertEquals("user", auth.username());
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertNotNull(dataAccess.getUser("user"));
        Assertions.assertNotNull(dataAccess.getAuth(auth.authToken()));
    }

    @Test
    public void registerAlreadyTaken() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);

        registerService.register("user", "password", "user@email.com");
        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> registerService.register("user", "differentPassword", "another@email.com"));

        Assertions.assertEquals(403, exception.getStatusCode());
        Assertions.assertEquals("already taken", exception.getMessage());
    }
}