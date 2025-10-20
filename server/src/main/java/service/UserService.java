package service;

import model.UserData;

import datamodel.LoginResult;
import datamodel.RegisterResult;

public class UserService {
    public RegisterResult register(UserData user) {
        return new RegisterResult(user.username(), "authtokenstringplaceholder");
    }

    public LoginResult login(UserData user) {
        return new LoginResult(user.username(), "sgldruewfoahla;f");
    }



//    public void logout(LogoutRequest logoutRequest) {}
}
