package client;

import datamodel.*;
import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        // Clear the database before each test
        try {
            facade.makeRequest("DELETE", "/db", null, null);
        } catch (Exception e) {
            // Database clear might not exist or might fail, that's ok
        }
    }

    // ===== REGISTER TESTS =====
    @Test
    void registerPositive() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    void registerDuplicate() {
        assertThrows(Exception.class, () -> {
            facade.register("player1", "password", "p1@email.com");
            facade.register("player1", "password2", "p1@email.com");
        });
    }

    @Test
    void registerEmptyUsername() {
        assertThrows(Exception.class, () -> facade.register("", "password", "email@test.com"));
    }

    // ===== LOGIN TESTS =====
    @Test
    void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var authData = facade.login("player1", "password");
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    void loginWrongPassword() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.login("player1", "wrongpassword"));
    }

    @Test
    void loginUserNotFound() {
        assertThrows(Exception.class, () -> facade.login("nonexistent", "password"));
    }

    // ===== LOGOUT TESTS =====
    @Test
    void logoutPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.logout();
        assertNull(facade.getAuthToken());
    }

    @Test
    void logoutWithoutLogin() {
        assertThrows(Exception.class, () -> facade.logout());
    }

    // ===== CREATE GAME TESTS =====
    @Test
    void createGamePositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var result = facade.createGame("My Game");
        assertNotNull(result.gameID());
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameWithoutAuth() {
        assertThrows(Exception.class, () -> facade.createGame("My Game"));
    }

    @Test
    void createGameEmptyName() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.createGame(""));
    }

    // ===== LIST GAMES TESTS =====
    @Test
    void listGamesEmpty() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var result = facade.listGames();
        assertEquals(0, result.games().size());
    }

    @Test
    void listGamesWithGames() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        facade.createGame("Game 1");
        facade.createGame("Game 2");
        var result = facade.listGames();
        assertEquals(2, result.games().size());
    }

    @Test
    void listGamesWithoutAuth() {
        assertThrows(Exception.class, () -> facade.listGames());
    }

    // ===== JOIN GAME TESTS =====
    @Test
    void joinGamePositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var gameResult = facade.createGame("Game 1");
        facade.joinGame(gameResult.gameID(), "WHITE");
    }

    @Test
    void joinGameAsBlack() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var gameResult = facade.createGame("Game 1");
        facade.joinGame(gameResult.gameID(), "BLACK");
    }

    @Test
    void joinGameAsObserver() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var gameResult = facade.createGame("Game 1");
        facade.joinGame(gameResult.gameID(), "OBSERVER");
    }

    @Test
    void joinGameTwice() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var gameResult = facade.createGame("Game 1");
        facade.joinGame(gameResult.gameID(), "WHITE");
        assertThrows(Exception.class, () ->
                facade.joinGame(gameResult.gameID(), "WHITE"));
    }

    @Test
    void joinGameInvalidId() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(Exception.class, () -> facade.joinGame(9999, "WHITE"));
    }

    @Test
    void joinGameWithoutAuth() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        var gameResult = facade.createGame("Game 1");
        facade.logout();
        assertThrows(Exception.class, () ->
                facade.joinGame(gameResult.gameID(), "WHITE"));
    }
}
