package handler;

import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import model.GameData;
import chess.ChessGame;


private final GameService gameService;

public WebSocketHandler(GameService gameService) {
    this.gameService = gameService;
}
