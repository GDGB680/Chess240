package server;

import dataaccess.MemoryDataAccess;
import dataaccess.DataAccess;
import handler.Handler;
import io.javalin.Javalin;

public class Server {
    private final Javalin javalin;
    private final Handler handler;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        handler = new Handler(dataAccess);

        javalin = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/web";
            });
        });

        configureRoutes();
    }

    private void configureRoutes() {
        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);
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
