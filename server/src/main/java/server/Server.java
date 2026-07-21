package server;

import dataaccess.ClearDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.MySqlDataAccess;
import io.javalin.*;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        //MemoryDataAccess memoryDataAccess = new MemoryDataAccess();
        try {
            MySqlDataAccess sqlDataAccess = new MySqlDataAccess();

            //clear
            ClearService clearService = new ClearService(sqlDataAccess);
            //register
            RegisterService registerService = new RegisterService(sqlDataAccess, sqlDataAccess);
            //login
            LoginService loginService = new LoginService(sqlDataAccess,sqlDataAccess);
            //logout
            LogoutService logoutService = new LogoutService(sqlDataAccess);
            //list games
            ListgameService listgameService = new ListgameService(sqlDataAccess, sqlDataAccess);
            //create game
            CreategameService creategameService = new CreategameService(sqlDataAccess,sqlDataAccess);
            //join game
            JoingameService joingameService = new JoingameService(sqlDataAccess,sqlDataAccess);
            Handler handler = new Handler(clearService,registerService,loginService,logoutService,
                    listgameService, creategameService, joingameService);
            javalin.delete("/db", handler::clear);
            javalin.post("/user", handler::register);
            javalin.post("/session", handler::login);
            javalin.delete("/session", handler::logout);
            javalin.get("/game", handler::listGames);
            javalin.post("/game", handler::createGame);
            javalin.put("/game", handler::joinGame);
        }
        catch (DataAccessException ex) {
            throw new RuntimeException(
                    "Unable to initialize database",
                    ex
            );
        }


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
