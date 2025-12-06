package dataaccess;

import chess.ChessGame;
import model.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;


public class MemoryDataAccess implements DataAccess {

    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> authTokens = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public void createUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());
        users.put(user.username(), user);
    }

    public void createAuthToken(AuthData authToken) {
        authToken = new AuthData(authToken.authToken(), authToken.username());
        authTokens.put(authToken.authToken(), authToken);
    }

    public GameData createGame(String gameName) {
        int newGameID = generateGameID();
        ChessGame newGame = new ChessGame();
        GameData newGameData = new GameData(newGameID, null,null, gameName,newGame);
        games.put(newGameID, newGameData);
        return newGameData;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public AuthData getAuthToken(String token) {
        return authTokens.get(token);
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public void updateGame(GameData game) {
        if (games.containsKey(game.gameID())) {
            games.put(game.gameID(), game);
        } else {
            throw new RuntimeException("Game not found");
        }
    }

    public void deleteAuthToken(String token) {
        authTokens.remove(token);
    }

    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }

    private int generateGameID() {
        Random random = new Random();
        int id;
        do {
            id = random.nextInt(10000); // Generate unique ID
        } while (games.containsKey(id)); // Check for collision
        return id;
    }


    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
    }
}