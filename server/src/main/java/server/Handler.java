package server;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;

public class Handler {
    private final ClearService clearService;

    public Handler(ClearService clearService){
        this.clearService = clearService;
    }

    public void clear(Context ctx){
        try{
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");

        }
        catch (DataAccessException e) {
           ctx.status(500);
           ctx.result("{\"message\":\"Error:" + e.getMessage()+"\"}");
        }
    }
}
