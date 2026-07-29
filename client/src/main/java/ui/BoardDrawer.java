package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;


public class BoardDrawer {
    public static String drawBoard(ChessGame.TeamColor perspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        StringBuilder result = new StringBuilder();
        appendColumnLabels(result, perspective);

        for (int index = 0; index < 8; index++) {
            int row;
            if (perspective == ChessGame.TeamColor.WHITE) {
                row = 8 - index;
            }
            else {
                row = index + 1;
            }
            appendRow(result, board, row, perspective);
        }
        appendColumnLabels(result, perspective);
        result.append(RESET_BG_COLOR);
        result.append(RESET_TEXT_COLOR);

        return result.toString();
    }

    private static void appendRow(StringBuilder result, ChessBoard board, int row, ChessGame.TeamColor perspective) {

        result.append(RESET_BG_COLOR);
        result.append(SET_TEXT_COLOR_WHITE);
        result.append(" ").append(row).append(" ");

        for (int index = 0; index < 8; index++) {
            int column;

            if (perspective == ChessGame.TeamColor.WHITE) {
                column = index + 1;
            }
            else {
                column = 8 - index;
            }

            setSquareColor(result, row, column);

            ChessPosition position = new ChessPosition(row, column);
            ChessPiece piece = board.getPiece(position);
            result.append(getPieceSymbol(piece));
        }

        result.append(RESET_BG_COLOR);
        result.append(SET_TEXT_COLOR_WHITE);
        result.append(" ").append(row).append("\n");
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

    private static void setSquareColor(
            StringBuilder result,
            int row,
            int column) {

        // a1 is a dark square.
        if ((row + column) % 2 == 0) {
            result.append(SET_BG_COLOR_DARK_GREY);
            result.append(SET_TEXT_COLOR_WHITE);
        } else {
            result.append(SET_BG_COLOR_LIGHT_GREY);
            result.append(SET_TEXT_COLOR_BLACK);
        }
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
