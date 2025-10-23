package server;

import dataaccess.*;
import handler.Handler;
import io.javalin.Javalin;

public class Server {
    private final Javalin javalin;
    private final Handler handler;

    public Server() {
        // Initialize data access and services
        DataAccess dataAccess = new MemoryDataAccess();
        handler = new Handler(dataAccess);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
        });

        // Register endpoints
        configureRoutes();
    }

    private void configureRoutes() {
        // Clear endpoint
        javalin.delete("/db", handler::clear);

        // User endpoints
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);

        // Game endpoints
        javalin.get("/game", handler::listGames);
        javalin.post("/game", handler::createGame);
        javalin.put("/game", handler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
