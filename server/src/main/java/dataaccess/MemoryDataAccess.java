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
        //should return int gameid
    }
    public GameData getGame(int gameID) throws DataAccessException{

    }

    public Collection<GameData> listGames() throws DataAccessException{

    }
    public void updateGame(GameData game) throws DataAccessException{

    }

    //clear method
    public void clear() throws DataAccessException{

    }

}
