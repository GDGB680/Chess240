package service;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
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

}
