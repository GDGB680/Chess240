package client;

import com.google.gson.Gson;
import model.*;
import datamodel.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String serverUrl;
    private final HttpClient httpClient;
    private final Gson gson;
    private String authToken;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.authToken = null;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var user = new UserData(username, password, email);
        var response = makeRequest("POST", "/user", gson.toJson(user), null);
        AuthData authData = gson.fromJson(response, AuthData.class);
        this.authToken = authData.authToken();
        return authData;
    }

    public AuthData login(String username, String password) throws Exception {
        var user = new UserData(username, password, null);
        var response = makeRequest("POST", "/session", gson.toJson(user), null);
        AuthData authData = gson.fromJson(response, AuthData.class);
        this.authToken = authData.authToken();
        return authData;
    }

    public void logout() throws Exception {
        makeRequest("DELETE", "/session", null, authToken);
        this.authToken = null;
    }

    public ListGamesResult listGames() throws Exception {
        var response = makeRequest("GET", "/game", null, authToken);
        return gson.fromJson(response, ListGamesResult.class);
    }

    public CreateGameResult createGame(String gameName) throws Exception {
        var request = new CreateGameRequest(gameName);
        var response = makeRequest("POST", "/game", gson.toJson(request), authToken);
        return gson.fromJson(response, CreateGameResult.class);
    }

    public void joinGame(int gameId, String playerColor) throws Exception {
        var request = new JoinGameRequest(playerColor, gameId);
        makeRequest("PUT", "/game", gson.toJson(request), authToken);
    }

    private String makeRequest(String method, String path, String body, String token) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(serverUrl + path))
                .method(method, body == null ?
                        HttpRequest.BodyPublishers.noBody() :
                        HttpRequest.BodyPublishers.ofString(body));

        if (token != null) {
            requestBuilder.header("Authorization", token);
        }

        var request = requestBuilder.build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            ErrorResponse error = gson.fromJson(response.body(), ErrorResponse.class);
            throw new Exception(error.message());
        }

        return response.body();
    }

    public String getAuthToken() {
        return authToken;
    }
}
