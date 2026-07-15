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


        MemoryDataAccess MDA = new MemoryDataAccess();
        //clear
        ClearService clearService = new ClearService(MDA);
        //register
        RegisterService registerService = new RegisterService(MDA, MDA);
        //login
        LoginService loginService = new LoginService(MDA,MDA);
        //logout
        LogoutService logoutService = new LogoutService(MDA);
        //list games
        ListgameService listgameService = new ListgameService(MDA, MDA);
        //create game
        CreategameService creategameService = new CreategameService(MDA,MDA);
        //join game
        JoingameService joingameService = new JoingameService(MDA,MDA);
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
