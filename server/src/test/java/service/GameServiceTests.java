package service;

import dataaccess.*;
import datamodel.*;
import datamodel.ListGamesResult;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private GameService gameService;
    private UserService userService;
    private String validAuthToken;

    @BeforeEach
    public void setup() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);

        // Create a user and get auth token for tests
        UserData user = new UserData("testuser", "password", "test@example.com");
        RegisterResult result = userService.register(user);
        validAuthToken = result.authToken();
    }


    @Test
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        try {
            CreateGameResult result = gameService.createGame("TestGame", validAuthToken);
            assertNotNull(result, "Result should not be null");
            assertTrue(result.gameID() > 0, "Game ID should be positive");
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Create Game Failure - Unauthorized")
    public void createGameFailureUnauthorized() {
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.createGame("TestGame", "invalid-token"),
                "Should throw exception for invalid auth token");
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        try {
            // Create a few games first
            gameService.createGame("Game1", validAuthToken);
            gameService.createGame("Game2", validAuthToken);

            ListGamesResult result = gameService.listGames(validAuthToken);
            assertNotNull(result, "Result should not be null");
            assertNotNull(result.games(), "Games collection should not be null");
            assertEquals(2, result.games().size(), "Should have 2 games");
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("List Games Failure - Unauthorized")
    public void listGamesFailureUnauthorized() {
        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> gameService.listGames("invalid-token"),
                "Should throw exception for invalid auth token");
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        try {
            // Create a game first
            CreateGameResult createResult = gameService.createGame("TestGame", validAuthToken);
            int gameID = createResult.gameID();

            // Join as white player
            assertDoesNotThrow(() -> gameService.joinGame(gameID, "WHITE", validAuthToken),
                    "Should successfully join game as WHITE");
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Join Game Failure - Already Taken")
    public void joinGameFailureAlreadyTaken() {
        try {
            // Create a game and join as white
            CreateGameResult createResult = gameService.createGame("TestGame", validAuthToken);
            int gameID = createResult.gameID();
            gameService.joinGame(gameID, "WHITE", validAuthToken);

            // Create another user
            UserData user2 = new UserData("user2", "password2", "user2@example.com");
            RegisterResult registerResult = userService.register(user2);
            String authToken2 = registerResult.authToken();

            // Try to join as white again (should fail)
            DataAccessException exception = assertThrows(DataAccessException.class,
                    () -> gameService.joinGame(gameID, "WHITE", authToken2),
                    "Should throw exception when color is already taken");
            assertEquals("already taken", exception.getMessage());
        } catch (DataAccessException e) {
            fail("Setup should not have thrown an exception: " + e.getMessage());
        }
    }
}
