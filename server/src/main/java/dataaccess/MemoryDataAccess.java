package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements UserDAO,AuthDAO,GameDAO,ClearDAO{
    private Map<String, UserData> users;
    private Map<String, AuthData> auths;
    private Map<Integer, GameData> games;

    public MemoryDataAccess(){
        users = new HashMap<>();
        auths = new HashMap<>();
        games = new HashMap<>();
    }


    //Auth methods
    public void createAuth(AuthData auth) throws DataAccessException{
        String authToken = auth.authToken();
        if (auths.containsKey(authToken)) {
            throw new DataAccessException("auth already exist");
        }
        else {
            auths.put(authToken, auth);
        }
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        if (auths.containsKey(authToken)) {
            return auths.get(authToken);
        }
        else {
            throw new DataAccessException("Token does not exist");
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{
        if (auths.containsKey(authToken)) {
            auths.remove(authToken);
        }
        else {
            throw new DataAccessException("Token does not exist");
        }
    }

    //User methods
    public void createUser(UserData user) throws DataAccessException{
        String username = user.username();
        if (users.containsKey(username)){
            throw new DataAccessException("Username taken");
        }
        else {
            users.put(username,user);
        }
    }

    public UserData getUser(String username) throws DataAccessException{
        if (users.containsKey(username)){
            return users.get(username);
        }
        else {
            throw new DataAccessException("User does not exist");
        }
    }

    //Game methods
    public void createGame(GameData game) throws DataAccessException{
        int id = game.gameID();
        games.put(id,game);
    }

    public GameData getGame(int gameID) throws DataAccessException{
        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }
        else {
            throw new DataAccessException("Game does not exist");
        }
    }

    public Collection<GameData> listGames() throws DataAccessException{
        return games.values();
    }

    public void updateGame(GameData game) throws DataAccessException{
        int id = game.gameID();
        if (games.containsKey(id)){
            games.put(id,game);
        }
        else {
            throw new DataAccessException("Game does not exist");
        }
    }

    //clear method
    public void clear() throws DataAccessException{
        users.clear();
        auths.clear();
        games.clear();
    }

}
