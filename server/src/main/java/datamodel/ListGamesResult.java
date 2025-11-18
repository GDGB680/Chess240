package datamodel;

import model.GameData;

import java.util.Collection;

public record ListGamesResult(Collection<GameDTO> games) {
}
