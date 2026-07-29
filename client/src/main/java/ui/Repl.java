package ui;

import client.ChessClient;

import java.util.Scanner;

public class Repl {
    private final ChessClient client;

    public Repl(ChessClient client) {
        this.client = client;
    }

    public void run() {
        System.out.println("♕ Welcome to the 240 Chess Client");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printPrompt();
            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine();
            String result = client.eval(input);

            if (result.equals("quit")) {
                break;
            }

            if (!result.isBlank()) {
                System.out.println(result);
            }
        }

        System.out.println("Goodbye!");
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }
}
