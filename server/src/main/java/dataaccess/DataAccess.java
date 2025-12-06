package dataaccess;

import chess.ChessGame;
import model.*;
import java.util.Collection;

public interface DataAccess {


    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String userName) throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;

    void createAuthToken(AuthData authToken) throws DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;

    void updateGame(int gameID, GameData gameData) throws DataAccessException;
}
