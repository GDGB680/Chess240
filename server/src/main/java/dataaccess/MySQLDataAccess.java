package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

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
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, hashedPassword);
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
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void createAuthToken(AuthData authToken) throws DataAccessException {

    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {

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
