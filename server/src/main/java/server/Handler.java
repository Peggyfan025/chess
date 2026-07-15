package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ServiceException;
import io.javalin.http.Context;
import model.AuthData;
import service.ClearService;
import service.RegisterService;

public class Handler {
    private final ClearService clearService;
    private final RegisterService registerService;

    public Handler(ClearService clearService, RegisterService registerService){
        this.clearService = clearService;
        this.registerService = registerService;
    }

    public void clear(Context ctx){
        try{
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");

        }
        catch (DataAccessException e) {
           ctx.status(500);
           ctx.result("{\"message\":\"Error: " + e.getMessage()+"\"}");
        }
    }

    private record RegisterRequest(String username,String password, String email){}
    public record ErrorResult(String message) {}

    public void register(Context ctx) throws DataAccessException {
        var serializer = new Gson();
        try {
            RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);
            AuthData result = registerService.register(request.username(), request.password(), request.email());
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (ServiceException e){
            ctx.status(e.getStatusCode());
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
        catch (DataAccessException e){
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }
}
