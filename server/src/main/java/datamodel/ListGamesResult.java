package datamodel;

import java.util.Collection;

public record ListGamesResult(Collection<GameDTO> games) {
}
