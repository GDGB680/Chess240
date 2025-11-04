package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

public class MySQLDataAccessTests {
    private DataAccess dataAccess;

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    @DisplayName("Create User Success")
    public void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("name", "pw", "name@email.com");
        dataAccess.createUser(user);
        UserData retrieved = dataAccess.getUser("name");
        assertNotNull(retrieved);
        assertEquals("name", retrieved.username());
    }

    @Test
    @DisplayName("Create User Fails on Duplicate")
    public void createUserDuplicate() throws DataAccessException {
        UserData user = new UserData("name", "pw", "name@email.com");
        dataAccess.createUser(user);
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(user));
    }


    // ========== GET USER TESTS ==========

    @Test
    @DisplayName("Get User Success")
    public void getUserSuccess() throws DataAccessException {
        UserData user = new UserData("bob", "pass456", "bob@email.com");
        dataAccess.createUser(user);

        UserData retrieved = dataAccess.getUser("bob");
        assertNotNull(retrieved);
        assertEquals("bob", retrieved.username());
        assertEquals("bob@email.com", retrieved.email());
    }

    @Test
    @DisplayName("Get User Returns Null for Non-existent")
    public void getUserNotFound() throws DataAccessException {
        UserData result = dataAccess.getUser("nonexistent");
        assertNull(result);
    }

    // ========== CREATE AUTH TOKEN TESTS ==========

    @Test
    @DisplayName("Create Auth Token Success")
    public void createAuthTokenSuccess() throws DataAccessException {
        // First create a user
        UserData user = new UserData("charlie", "pass789", "charlie@email.com");
        dataAccess.createUser(user);

        // Now create auth token
        AuthData authToken = new AuthData("token123", "charlie");
        dataAccess.createAuthToken(authToken);

        AuthData retrieved = dataAccess.getAuthToken("token123");
        assertNotNull(retrieved);
        assertEquals("charlie", retrieved.username());
    }

    @Test
    @DisplayName("Create Auth Token Fails on Duplicate")
    public void createAuthTokenDuplicate() throws DataAccessException {
        UserData user = new UserData("diana", "pass000", "diana@email.com");
        dataAccess.createUser(user);

        AuthData authToken = new AuthData("token456", "diana");
        dataAccess.createAuthToken(authToken);

        assertThrows(DataAccessException.class, () -> {
            dataAccess.createAuthToken(authToken);
        });
    }

    // ========== GET AUTH TOKEN TESTS ==========

    @Test
    @DisplayName("Get Auth Token Success")
    public void getAuthTokenSuccess() throws DataAccessException {
        UserData user = new UserData("eve", "pass111", "eve@email.com");
        dataAccess.createUser(user);

        AuthData authToken = new AuthData("token789", "eve");
        dataAccess.createAuthToken(authToken);

        AuthData retrieved = dataAccess.getAuthToken("token789");
        assertNotNull(retrieved);
        assertEquals("eve", retrieved.username());
    }

    @Test
    @DisplayName("Get Auth Token Returns Null for Non-existent")
    public void getAuthTokenNotFound() throws DataAccessException {
        AuthData result = dataAccess.getAuthToken("nonexistent_token");
        assertNull(result);
    }

    // ========== DELETE AUTH TOKEN TESTS ==========

    @Test
    @DisplayName("Delete Auth Token Success")
    public void deleteAuthTokenSuccess() throws DataAccessException {
        UserData user = new UserData("frank", "pass222", "frank@email.com");
        dataAccess.createUser(user);

        AuthData authToken = new AuthData("tokenABC", "frank");
        dataAccess.createAuthToken(authToken);

        // Verify it exists
        assertNotNull(dataAccess.getAuthToken("tokenABC"));

        // Delete it
        dataAccess.deleteAuthToken("tokenABC");

        // Verify it's gone
        assertNull(dataAccess.getAuthToken("tokenABC"));
    }

    @Test
    @DisplayName("Delete Auth Token Non-existent (Should Not Throw)")
    public void deleteAuthTokenNotFound() throws DataAccessException {
        // Should not throw exception
        assertDoesNotThrow(() -> dataAccess.deleteAuthToken("nonexistent_token"));
    }

    // ========== CREATE GAME TESTS ==========

    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() throws DataAccessException {
        GameData game = dataAccess.createGame("Game 1");

        assertNotNull(game);
        assertTrue(game.gameID() > 0);
        assertEquals("Game 1", game.gameName());
    }

    @Test
    @DisplayName("Create Game Fails on Duplicate Name (Should Still Create)")
    public void createGameDuplicateName() throws DataAccessException {
        GameData game1 = dataAccess.createGame("Game 1");
        GameData game2 = dataAccess.createGame("Game 1");

        // Both should be created with different IDs
        assertNotEquals(game1.gameID(), game2.gameID());
    }

    // ========== GET GAME TESTS ==========

    @Test
    @DisplayName("Get Game Success")
    public void getGameSuccess() throws DataAccessException {
        GameData created = dataAccess.createGame("Chess Game");
        int gameID = created.gameID();

        GameData retrieved = dataAccess.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals(gameID, retrieved.gameID());
        assertEquals("Chess Game", retrieved.gameName());
    }

    @Test
    @DisplayName("Get Game Returns Null for Non-existent")
    public void getGameNotFound() throws DataAccessException {
        GameData result = dataAccess.getGame(9999);
        assertNull(result);
    }

    // ========== LIST GAMES TESTS ==========

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() throws DataAccessException {
        dataAccess.createGame("Game A");
        dataAccess.createGame("Game B");
        dataAccess.createGame("Game C");

        Collection<GameData> games = dataAccess.listGames();
        assertNotNull(games);
        assertEquals(3, games.size());
    }

    @Test
    @DisplayName("List Games Empty")
    public void listGamesEmpty() throws DataAccessException {
        Collection<GameData> games = dataAccess.listGames();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    // ========== UPDATE GAME TESTS ==========

    @Test
    @DisplayName("Update Game Success")
    public void updateGameSuccess() throws DataAccessException {
        GameData game = dataAccess.createGame("Initial Game");

        // Create updated version with players
        GameData updated = new GameData(
                game.gameID(),
                "alice",
                "bob",
                "Initial Game",
                new ChessGame()
        );

        dataAccess.updateGame(updated);

        GameData retrieved = dataAccess.getGame(game.gameID());
        assertEquals("alice", retrieved.whiteUsername());
        assertEquals("bob", retrieved.blackUsername());
    }

    @Test
    @DisplayName("Update Game Non-existent (Should Not Throw)")
    public void updateGameNotFound() throws DataAccessException {
        GameData game = new GameData(9999, "alice", null, "Fake Game", new ChessGame());

        // Should not throw exception
        assertDoesNotThrow(() -> dataAccess.updateGame(game));
    }

    // ========== CLEAR TESTS ==========

    @Test
    @DisplayName("Clear Success")
    public void clearSuccess() throws DataAccessException {
        // Create some data
        dataAccess.createUser(new UserData("user1", "pass", "user1@email.com"));
        dataAccess.createGame("game1");

        // Clear everything
        dataAccess.clear();

        // Verify data is gone
        assertNull(dataAccess.getUser("user1"));
        assertEquals(0, dataAccess.listGames().size());
    }
}