package handler;

import com.google.gson.Gson;
import dataaccess.*;
import datamodel.*;
import io.javalin.http.Context;
import model.*;
import service.*;

import java.util.Map;

public class Handler {
    private final UserService userService;
    private final GameService gameService;
    private final DataAccess dataAccess;
    private final Gson gson;

    public Handler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
        this.userService = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.gson = new Gson();
    }

    // Clear endpoint - DELETE /db
    public void clear(Context ctx) {
        try {
            dataAccess.clear();
            String gsonResult = gson.toJson(Map.of());
            ctx.status(200);
            ctx.json(gsonResult);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    // Register endpoint - POST /user
    public void register(Context ctx) {
        try {
            UserData request = gson.fromJson(ctx.body(), UserData.class);

            // Validate request
            if (request == null || request.username() == null ||
                    request.password() == null || request.email() == null) {
                throw new DataAccessException("bad request");
            }

            RegisterResult result = userService.register(request);
            String gsonresult = gson.toJson(result);
            ctx.status(200);
            ctx.json(gsonresult);
        } catch (DataAccessException e) {
            handleDataAccessException(ctx, e);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    // Login endpoint - POST /session
    public void login(Context ctx) {
        try {
            UserData request = gson.fromJson(ctx.body(), UserData.class);

            // Validate request
            if (request == null || request.username() == null || request.password() == null) {
                throw new DataAccessException("bad request");
            }

            LoginResult result = userService.login(request);
            String gsonresult = gson.toJson(result);
            ctx.status(200);
            ctx.json(gsonresult);
        } catch (DataAccessException e) {
            handleDataAccessException(ctx, e);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    // Logout endpoint - DELETE /session
    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            if (authToken == null || authToken.isEmpty()) {
                throw new DataAccessException("unauthorized");
            }

            AuthData authData = new AuthData(authToken, null);
            userService.logout(authData);
            String gsonResult = gson.toJson(Map.of());
            ctx.status(200);
            ctx.json(gsonResult);
        } catch (DataAccessException e) {
            handleDataAccessException(ctx, e);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                throw new DataAccessException("unauthorized");
            }

            ListGamesResult result = gameService.listGames(authToken);
            String gsonresult = gson.toJson(result);
            ctx.status(200);
            ctx.json(gsonresult);

        } catch (DataAccessException e) {
            handleDataAccessException(ctx, e);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }


    // Create game endpoint - POST /game
    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            if (authToken == null || authToken.isEmpty()) {
                throw new DataAccessException("unauthorized");
            }

            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);

            if (request == null || request.gameName() == null || request.gameName().isEmpty()) {
                throw new DataAccessException("bad request");
            }

            CreateGameResult result = gameService.createGame(request.gameName(), authToken);
            String gsonresult = gson.toJson(result);
            ctx.status(200);
            ctx.json(gsonresult);
        } catch (DataAccessException e) {
            handleDataAccessException(ctx, e);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            if (authToken == null || authToken.isEmpty()) {
                throw new DataAccessException("unauthorized");
            }

            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            if (request == null) {
                throw new DataAccessException("bad request");
            }

            Integer gameID = request.gameID();
            if (gameID == null || gameID <= 0) {
                throw new DataAccessException("bad request");
            }

            String playerColor = request.playerColor();

            // playerColor must be WHITE or BLACK or Observer
            if (playerColor == null || playerColor.isEmpty() ||
                    (!playerColor.equals("WHITE") && !playerColor.equals("BLACK") && !playerColor.equals("OBSERVER"))) {
                throw new DataAccessException("bad request");
            }

            gameService.joinGame(gameID, playerColor, authToken);
            String gsonResult = gson.toJson(Map.of());
            ctx.status(200);
            ctx.json(gsonResult);

        } catch (DataAccessException e) {
            handleDataAccessException(ctx, e);
        } catch (Exception e) {
            handleException(ctx, e);
        }
    }



    private void handleDataAccessException(Context ctx, DataAccessException e) {
        int statusCode = determineStatusCode(e.getMessage());
        ctx.status(statusCode);
        String gsonResult = gson.toJson(Map.of("message", "Error " + e.getMessage()));
        ctx.json(gsonResult);
    }

    private void handleException(Context ctx, Exception e) {
        ctx.status(500);
        String gsonResult = gson.toJson(Map.of("message", "Error " + e.getMessage()));
        ctx.json(gsonResult);
    }

    private int determineStatusCode(String errorMessage) {
        return switch (errorMessage.toLowerCase()) {
            case "bad request" -> 400;
            case "unauthorized" -> 401;
            case "already taken" -> 403;
            default -> 500;
        };
    }
}
