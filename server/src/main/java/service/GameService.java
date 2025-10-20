package service;

import model.GameData;

import datamodel.CreateGameResult;
//import datamodel.ListGamesResult;

public class GameService {

    public CreateGameResult createGame(GameData game) {
        return new CreateGameResult("newexampletobreplacesedgameid");
    }

}
