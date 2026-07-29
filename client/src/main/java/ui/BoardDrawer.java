package ui;

import chess.ChessBoard;
import chess.ChessGame;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;


public class BoardDrawer {
    public static String drawBoard(ChessGame.TeamColor perspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        for (int index = 0; index < 8; index++) {
            int row;
            if (perspective == ChessGame.TeamColor.WHITE) {
                row = 8 - index;
            } else {
                row = index + 1;
            }

        }
}
