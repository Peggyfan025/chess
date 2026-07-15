package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import exception.ServiceException;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LogoutServiceTest {
    @Test
    public void logoutSuccess() throws Exception {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        RegisterService registerService = new RegisterService(dataAccess, dataAccess);
        LogoutService logoutService = new LogoutService(dataAccess);

        AuthData auth = registerService.register("user", "password", "user@email.com");
        logoutService.logout(auth.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.getAuth(auth.authToken()));
    }

    @Test
    public void logoutUnauthorized() {

        MemoryDataAccess dataAccess = new MemoryDataAccess();
        LogoutService logoutService = new LogoutService(dataAccess);

        ServiceException exception = Assertions.assertThrows(ServiceException.class, () -> logoutService.logout("fakeToken"));
        Assertions.assertEquals(401, exception.getStatusCode());
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }

}
