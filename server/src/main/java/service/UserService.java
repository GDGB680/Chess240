package service;


//import model;

//import datamodel.LoginResult;
import datamodel.RegisterResult;
import datamodel.UserData;

public class UserService {
    public RegisterResult register(UserData user) {
        return new RegisterResult(user.username(), "authtokenstringplaceholder");
    }

//    public LoginResult login(LoginRequest loginRequest) {}
//    public void logout(LogoutRequest logoutRequest) {}
}
