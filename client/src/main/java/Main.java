package ui;

import client.ServerFacade;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 8080;

        ServerFacade serverFacade = new ServerFacade(host, port);

        while (true) {
            PreloginUI preloginUI = new PreloginUI(serverFacade);
            if (!preloginUI.run()) break; // User quit

            // User is now logged in
            PostloginUI postloginUI = new PostloginUI(serverFacade);
            postloginUI.run(); // Returns when user logs out
        }
    }
}
