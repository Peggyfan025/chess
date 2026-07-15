package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ServiceException;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import service.*;

import java.util.ArrayList;
import java.util.Collection;

public class Handler {
    private final ClearService clearService;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ListgameService listgameService;
    private final CreategameService creategameService;
    private final JoingameService joingameService;

    public Handler(ClearService clearService, RegisterService registerService, LoginService loginService, LogoutService logoutService, ListgameService listgameService, CreategameService creategameService, JoingameService joingameService){
        this.clearService = clearService;
        this.registerService = registerService;
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.listgameService = listgameService;
        this.creategameService = creategameService;
        this.joingameService = joingameService;
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
    private final Gson serializer = new Gson();

    public void register(Context ctx) throws DataAccessException {
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

    public record LoginRequest(String username, String password) {}
    public void login(Context ctx) {
        try {
            LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);
            AuthData result = loginService.login(request.username(), request.password());
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            logoutService.logout(authToken);
            ctx.status(200);
            ctx.result("{}");
        }
        catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }

    public record GameListEntry(int gameID, String whiteUsername, String blackUsername, String gameName) {}
    public record ListGamesResult(Collection<GameListEntry> games) {}

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            Collection<GameData> games = listgameService.listGames(authToken);
            Collection<GameListEntry> gameList = new ArrayList<>();
            for (GameData game : games) {
                gameList.add(new GameListEntry(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            ListGamesResult result = new ListGamesResult(gameList);
            ctx.status(200);
            ctx.result(serializer.toJson(result));
        }
        catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }

    public record CreateGameRequest(String gameName) {}
    public record CreateGameResult(int gameID) {}
    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            CreateGameRequest request = serializer.fromJson(ctx.body(), CreateGameRequest.class);

            int gameID = creategameService.createGame(authToken, request.gameName());
            CreateGameResult result = new CreateGameResult(gameID);
            ctx.status(200);
            ctx.result(serializer.toJson(result));

        }
        catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }

    public record JoinGameRequest(ChessGame.TeamColor playerColor, Integer gameID) {}
    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JoinGameRequest request = serializer.fromJson(ctx.body(), JoinGameRequest.class);
            joingameService.joinGame(authToken, request.playerColor(), request.gameID());
            ctx.status(200);
            ctx.result("{}");
        }
        catch (ServiceException e) {
            ctx.status(e.getStatusCode());
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
        catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(new ErrorResult("Error: " + e.getMessage())));
        }
    }
}

