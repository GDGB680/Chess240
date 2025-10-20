package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import datamodel.LoginResult;
import datamodel.RegisterResult;

import java.util.UUID;


public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {this.dataAccess = dataAccess;}


    public RegisterResult register(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null || user.email() == null) {throw new DataAccessException("bad request");}
        if (dataAccess.getUser(user.username()) != null) {throw new DataAccessException("already taken");}
        UserData newUser = new UserData(user.username(), user.password(), user.email());
        dataAccess.createUser(newUser);
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        dataAccess.createAuthToken(authData);
        return new RegisterResult(user.username(), authToken);
    }

    public LoginResult login(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null) {throw new DataAccessException("bad request");}
//        if (user.password() != user.password()) {
//            throw new DataAccessException("unauthorized");
//        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, user.username());
        dataAccess.createAuthToken(authData);
        return new LoginResult(user.username(), authToken);
    }




    public void logout(AuthData user) throws DataAccessException {
        if (user.authToken() == null) {throw new DataAccessException("bad request");}
        AuthData authData = dataAccess.getAuthToken(user.authToken());
        if (authData == null) {throw new DataAccessException("unauthorized");}
        dataAccess.deleteAuthToken(user.authToken());
    }
}
