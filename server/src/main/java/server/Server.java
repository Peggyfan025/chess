package server;

import dataaccess.ClearDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import service.ClearService;
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
        Handler handler = new Handler(clearService,registerService);
        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);




    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
