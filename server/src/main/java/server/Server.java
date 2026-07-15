package server;

import dataaccess.ClearDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import service.ClearService;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        MemoryDataAccess MDA = new MemoryDataAccess();
        //clear
        ClearService clearService = new ClearService(MDA);
        //register
        RegisterService registerService = new RegisterService(MDA, MDA);
        //login
        LoginService loginService = new LoginService(MDA,MDA);
        //logout
        LogoutService logoutService = new LogoutService(MDA);
        Handler handler = new Handler(clearService,registerService,loginService,logoutService);
        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);




    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
