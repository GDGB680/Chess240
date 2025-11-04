package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import java.sql.*;
import java.util.*;

public class MySQLDataAccess implements DataAccess {

    private final Gson gson = new Gson();

    public MySQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            createTables(conn);
        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database: " + e.getMessage());
        }
    }

    private void createTables(Connection conn) throws SQLException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS auth_tokens (
                authToken VARCHAR(255) PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS games (
                gameID INT PRIMARY KEY AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                gameState TEXT NOT NULL
            )
            """
        };
        for (var statement : createStatements) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // Duplicate key error
                throw new DataAccessException("already taken");
            }
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }


    @Override
    public UserData getUser(String userName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return null;
    }


    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?)";
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, null);  // whiteUsername starts as null
                ps.setString(2, null);  // blackUsername starts as null
                ps.setString(3, gameName);
                ps.setString(4, gson.toJson(new ChessGame()));  // Create empty ChessGame as JSON
                ps.executeUpdate();

                // Get the generated game ID
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int gameID = rs.getInt(1);
                        return new GameData(gameID, null, null, gameName, new ChessGame());
                    }
                }
                throw new DataAccessException("Failed to create game");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM games WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String gameStateJson = rs.getString("gameState");
                        ChessGame game = gson.fromJson(gameStateJson, ChessGame.class);

                        return new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameState FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String gameStateJson = rs.getString("gameState");
                        ChessGame game = gson.fromJson(gameStateJson, ChessGame.class);

                        GameData gameData = new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                game
                        );
                        games.add(gameData);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }

        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ? WHERE gameID = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, gson.toJson(game.game()));  // Serialize ChessGame to JSON
                ps.setInt(5, game.gameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    @Override
    public void createAuthToken(AuthData authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO auth_tokens (authToken, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken.authToken());
                ps.setString(2, authToken.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // Duplicate key error
                throw new DataAccessException("authToken already exists");
            }
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        }
    }


    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM auth_tokens WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
        return null;
    }


    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM auth_tokens WHERE authToken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // Delete from auth_tokens first (foreign key dependency)
            try (var ps = conn.prepareStatement("DELETE FROM auth_tokens")) {
                ps.executeUpdate();
            }
            // Delete from games
            try (var ps = conn.prepareStatement("DELETE FROM games")) {
                ps.executeUpdate();
            }
            // Delete from users
            try (var ps = conn.prepareStatement("DELETE FROM users")) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database: " + e.getMessage());
        }
    }

}
