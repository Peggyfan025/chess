package client;

import chess.*;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        ServerFacade server = new ServerFacade(8080);
        ChessClient client = new ChessClient(server);
        Repl repl = new Repl(client);

        repl.run();
    }
}