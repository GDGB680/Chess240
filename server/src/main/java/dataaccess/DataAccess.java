package dataaccess;

import chess.ChessGame;
import model.*;
import java.util.Collection;

public interface DataAccess {


    UserData createUser(UserData user) throws DataAccessException;
    UserData getUser(String userName) throws DataAccessException;

    //    GameData createGame(GameData game) throws DataAccessException;
    GameData createGame(String whiteUsername, String blackUsername, String gameName, ChessGame game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;

    AuthData createAuthToken(AuthData authToken) throws DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}
