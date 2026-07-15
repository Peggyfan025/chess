package server;

import dataaccess.ClearDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        //clear
        ClearService clearService = new ClearService(memoryDataAccess);
        //register
        RegisterService registerService = new RegisterService(memoryDataAccess, memoryDataAccess);
        //login
        LoginService loginService = new LoginService(memoryDataAccess,memoryDataAccess);
        //logout
        LogoutService logoutService = new LogoutService(memoryDataAccess);
        //list games
        ListgameService listgameService = new ListgameService(memoryDataAccess, memoryDataAccess);
        //create game
        CreategameService creategameService = new CreategameService(memoryDataAccess,memoryDataAccess);
        //join game
        JoingameService joingameService = new JoingameService(memoryDataAccess,memoryDataAccess);
        Handler handler = new Handler(clearService,registerService,loginService,logoutService,listgameService, creategameService, joingameService);
        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);
        javalin.get("/game", handler::listGames);
        javalin.post("/game", handler::createGame);
        javalin.put("/game", handler::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
