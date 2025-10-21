package service;

import dataaccess.*;
import datamodel.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private DataAccess dataAccess;
    private UserService userService;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    @DisplayName("Register Success")
    public void registerSuccess() {
        UserData request = new UserData("username1", "password1", "email1@example.com");

        try {
            RegisterResult result = userService.register(request);

            assertNotNull(result, "Result should not be null");
            assertEquals("username1", result.username(), "Username should match");
            assertNotNull(result.authToken(), "Auth token should not be null");
            assertTrue(result.authToken().length() > 0, "Auth token should not be empty");
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Register Failure - Already Taken")
    public void registerFailureAlreadyTaken() {
        UserData request1 = new UserData("username1", "password1", "email1@example.com");
        UserData request2 = new UserData("username1", "password2", "email2@example.com");

        try {
            userService.register(request1);

            DataAccessException exception = assertThrows(DataAccessException.class,
                    () -> userService.register(request2),
                    "Should throw exception for duplicate username");
            assertEquals("already taken", exception.getMessage());
        } catch (DataAccessException e) {
            fail("First registration should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Login Success")
    public void loginSuccess() {
        UserData registerRequest = new UserData("username1", "password1", "email1@example.com");

        try {
            userService.register(registerRequest);
            UserData loginRequest = new UserData("username1", "password1", null);

            LoginResult result = userService.login(loginRequest);

            assertNotNull(result, "Result should not be null");
            assertEquals("username1", result.username(), "Username should match");
            assertNotNull(result.authToken(), "Auth token should not be null");
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Login Failure - Invalid Credentials")
    public void loginFailureInvalidCredentials() {
        UserData registerRequest = new UserData("username1", "password1", "email1@example.com");

        try {
            userService.register(registerRequest);
            UserData loginRequest = new UserData("username1", "wrongpassword", null);

            // Note: Your current login implementation doesn't verify password,
            // so this test will fail. You need to fix login() to check passwords.
            LoginResult result = userService.login(loginRequest);

            // This assertion will fail with current implementation
            fail("Should have thrown exception for wrong password");

        } catch (DataAccessException e) {
            assertEquals("unauthorized", e.getMessage());
        }
    }

    @Test
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        UserData registerRequest = new UserData("username1", "password1", "email1@example.com");

        try {
            RegisterResult registerResult = userService.register(registerRequest);

            // Create AuthData with the token (matching logout parameter)
            AuthData authData = new AuthData(registerResult.authToken(), registerResult.username());

            assertDoesNotThrow(() -> userService.logout(authData),
                    "Logout should not throw exception with valid token");

            // Verify token was deleted
            AuthData deletedAuth = dataAccess.getAuthToken(registerResult.authToken());
            assertNull(deletedAuth, "Auth token should be deleted after logout");
        } catch (DataAccessException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Logout Failure - Invalid Token")
    public void logoutFailureInvalidToken() {
        // Create AuthData with invalid token
        AuthData authData = new AuthData("invalid-token", "someuser");

        DataAccessException exception = assertThrows(DataAccessException.class,
                () -> userService.logout(authData),
                "Should throw exception for invalid token");
        assertEquals("unauthorized", exception.getMessage());
    }
}
