package client;

import chess.*;
import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        ServerFacade server = new ServerFacade(port);
        ChessClient client = new ChessClient(server,port);
        Repl repl = new Repl(client);

        repl.run();
    }
}