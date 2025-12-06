package server;

import dataaccess.*;
import handler.Handler;
import io.javalin.Javalin;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;
import server.websocket.WebSocketHandler;

public class Server {
    private final Javalin javalin;
    private final Handler handler;

    public Server() {
        try {
            DataAccess dataAccess = new MySQLDataAccess();
            handler = new Handler(dataAccess);

            javalin = Javalin.create(config -> {
                config.staticFiles.add(staticFileConfig -> {
                    staticFileConfig.hostedPath = "/";
                    staticFileConfig.directory = "/web";
                });

                JettyWebSocketServletContainerInitializer.initialize(
                        config.jetty.server,
                        wsContainer -> wsContainer.addMapping("/ws",
                                (req, resp) -> new WebSocketHandler(handler.getGameService()))
                );
            });

            configureRoutes();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void configureRoutes() {
        javalin.delete("/db", handler::clear);
        javalin.post("/user", handler::register);
        javalin.post("/session", handler::login);
        javalin.delete("/session", handler::logout);
        javalin.get("/game", handler::listGames);
        javalin.post("/game", handler::createGame);
        javalin.put("/game", handler::joinGame);
        javalin.get("/game/{gameID}", handler::getGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
