package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        for (int i=1; i<=8; i++){
            ChessPiece w_pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(new ChessPosition(2,i),w_pawn);
            ChessPiece b_pawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(new ChessPosition(7,i),b_pawn);
        }
        for (int i=0; i<=1;i++){
            ChessPiece w_rook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            addPiece(new ChessPosition(1,1),w_rook);
            addPiece(new ChessPosition(1,8),w_rook);
            ChessPiece b_rook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
            addPiece(new ChessPosition(8,1),b_rook);
            addPiece(new ChessPosition(8,8),b_rook);
            ChessPiece w_bishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
            addPiece(new ChessPosition(1,3),w_bishop);
            addPiece(new ChessPosition(1,6),w_bishop);
            ChessPiece b_bishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
            addPiece(new ChessPosition(8,3),b_bishop);
            addPiece(new ChessPosition(8,6),b_bishop);
            ChessPiece w_knight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            addPiece(new ChessPosition(1,2),w_knight);
            addPiece(new ChessPosition(1,7),w_knight);
            ChessPiece b_knight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
            addPiece(new ChessPosition(8,2),b_knight);
            addPiece(new ChessPosition(8,7),b_knight);
        }
        ChessPiece w_queen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        addPiece(new ChessPosition(1, 4),w_queen);
        ChessPiece b_queen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        addPiece(new ChessPosition(8, 4),b_queen);
        ChessPiece w_king = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        addPiece(new ChessPosition(1, 5),w_king);
        ChessPiece b_king = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        addPiece(new ChessPosition(8, 5),b_king);
    }
}
