package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;

import static ui.EscapeSequences.*;


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

    private static void appendColumnLabels(
            StringBuilder result,
            ChessGame.TeamColor perspective) {

        result.append(RESET_BG_COLOR);
        result.append(SET_TEXT_COLOR_WHITE);
        result.append("   ");

        for (int index = 0; index < 8; index++) {
            int column;

            if (perspective == ChessGame.TeamColor.WHITE) {
                column = index + 1;
            }
            else {
                column = 8 - index;
            }

            char letter = (char) ('a' + column - 1);

            result.append(" ")
                    .append(letter)
                    .append(" ");
        }
        result.append("\n");
    }
    private static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case ROOK -> WHITE_ROOK;
                case PAWN -> WHITE_PAWN;
            };
        }

        return switch (piece.getPieceType()) {
            case KING -> BLACK_KING;
            case QUEEN -> BLACK_QUEEN;
            case BISHOP -> BLACK_BISHOP;
            case KNIGHT -> BLACK_KNIGHT;
            case ROOK -> BLACK_ROOK;
            case PAWN -> BLACK_PAWN;
        };
    }
}
