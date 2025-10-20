package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;

import datamodel.LoginResult;
import datamodel.RegisterResult;



public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(UserData user) throws DataAccessException {
        return new RegisterResult(user.username(), "authtokenstringplaceholder");
    }

    public LoginResult login(UserData user) {
        return new LoginResult(user.username(), "sgldruewfoahla;f");
    }


    public void logout(UserData user) {}


}
