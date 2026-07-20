package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.*;
import java.util.Collection;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlDataAccess implements UserDAO,AuthDAO,GameDAO,ClearDAO{
    public MySqlDataAccess() throws DataAccessException{
        configureDatabase();
    }
    //Auth methods
    public void createAuth(AuthData auth) throws DataAccessException{
        String statement = """
            INSERT INTO auth (authToken, username)
            VALUES (?, ?)
            """;

        executeUpdate(statement, auth.authToken(), auth.username());
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        String statement = """
            SELECT authToken, username
            FROM auth
            WHERE authToken = ?
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1,authToken);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
            return null;
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to get user: %s", ex.getMessage()),ex);
        }
    }

    public void deleteAuth(String authToken) throws DataAccessException{

    }

    //User methods
    public void createUser(UserData user) throws DataAccessException{
        String statement = """
            INSERT INTO user (username, password, email)
            VALUES (?, ?, ?)
            """;

        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    public UserData getUser(String username) throws DataAccessException{
        String statement = """
            SELECT username, password, email
            FROM user
            WHERE username = ?
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1,username);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
            return null;
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to get user: %s", ex.getMessage()),ex);
        }
    }

    //Game methods
    public int createGame(GameData game) throws DataAccessException{
        String statement = """
            INSERT INTO game
            (whiteUsername, blackUsername, gameName, game)
            VALUES (?, ?, ?, ?)
            """;
        return executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gson.toJson(game.game()));
    }

    public GameData getGame(int gameID) throws DataAccessException{
        String statement = """
            SELECT gameID, whiteUsername, blackUsername, gameName, game
            FROM game
            WHERE gameID = ?
            """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setInt(1,gameID);
            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    String gameJson = rs. getString("game");
                    ChessGame game = gson.fromJson(gameJson, ChessGame.class);
                    return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"), rs.getString("blackUsername"), rs.getString("gameName"),game);
                }
            }
            return null;
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to get user: %s", ex.getMessage()),ex);
        }
    }

    public Collection<GameData> listGames() throws DataAccessException{

    }

    public void updateGame(GameData game) throws DataAccessException{

    }

    //clear method
    public void clear() throws DataAccessException{

    }


    //private variables
    private final Gson gson = new Gson();
    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
        )
        """,

            """
        CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
        )
        """,

            """
        CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255) NOT NULL,
            game TEXT NOT NULL,
            PRIMARY KEY (gameID)
        )
        """
    };

    //private methods
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()), ex);
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];

                if (param instanceof String value) {
                    ps.setString(i + 1, value);
                } else if (param instanceof Integer value) {
                    ps.setInt(i + 1, value);
                } else if (param == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    throw new DataAccessException("Unsupported SQL parameter type: "
                            + param.getClass().getName());
                }
            }

            ps.executeUpdate();

            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
            return 0;
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to update database: %s, %s", statement, ex.getMessage()), ex);
        }
    }
}