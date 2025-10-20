package dataaccess;

import chess.ChessGame;
import model.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;


public class MemoryDataAccess implements DataAccess {

    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<String, AuthData> authTokens = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public UserData createUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());
        users.put(user.username(), user);
        return user;
    }

    public AuthData createAuthToken(AuthData authToken) {
        authToken = new AuthData(authToken.authToken(), authToken.username());
        authTokens.put(authToken.authToken(), authToken);
        return authToken;
    }

    public GameData createGame(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        GameData newGameData = new GameData(generateGameID(), whiteUsername, blackUsername, gameName, game);
        games.put(newGameData.gameID(), newGameData);
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
        return random.nextInt(1000);
    }
}