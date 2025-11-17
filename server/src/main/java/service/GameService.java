package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import datamodel.CreateGameResult;
import datamodel.ListGamesResult;
import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException {
        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("bad request");
        }
        AuthData authData = dataAccess.getAuthToken(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        GameData createdGame = dataAccess.createGame(gameName);
        return new CreateGameResult(createdGame.gameID());
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        AuthData authData = dataAccess.getAuthToken(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        Collection<GameData> allGames = dataAccess.listGames();
        Collection<datamodel.GameDTO> games = new ArrayList<>();

        for (GameData game : allGames) {
            // Convert to DTO WITHOUT ChessGame
            games.add(new datamodel.GameDTO(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            ));
        }
        return new ListGamesResult(games);
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws DataAccessException {
        // Validate auth token
        AuthData authData = dataAccess.getAuthToken(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        // Validate game ID
        if (gameID <= 0) {
            throw new DataAccessException("bad request");
        }

        // Get the game
        GameData gameData = dataAccess.getGame(gameID);
        if (gameData == null) {
            throw new DataAccessException("bad request");
        }

        // If no color specified, user joins as observer
        if (playerColor == null || playerColor.isEmpty() || playerColor.equalsIgnoreCase("OBSERVER")) {
            return;
        }

        // Update game with player
        String username = authData.username();
        GameData updatedGameData = assignPlayerToGame(gameData, username, playerColor);
        dataAccess.updateGame(updatedGameData);
    }

    private GameData assignPlayerToGame(GameData gameData, String username, String color)
            throws DataAccessException {
        if (color.equalsIgnoreCase("WHITE")) {
            if (gameData.whiteUsername() != null) {
                throw new DataAccessException("already taken");
            }
            return new GameData(
                    gameData.gameID(),
                    username,
                    gameData.blackUsername(),
                    gameData.gameName(),
                    gameData.game()
            );
        } else if (color.equalsIgnoreCase("BLACK")) {
            if (gameData.blackUsername() != null) {
                throw new DataAccessException("already taken");
            }
            return new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    username,
                    gameData.gameName(),
                    gameData.game()
            );
        } else {
            throw new DataAccessException("bad request");
        }
    }
}
