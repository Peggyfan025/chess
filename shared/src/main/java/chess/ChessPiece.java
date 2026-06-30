package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP) {
            PieceMoveCalculator calculator = new BishopMove(board, myPosition, piece);
            return calculator.pieceMove();
        }
        else if (piece.getPieceType() == PieceType.KNIGHT){
            PieceMoveCalculator calculator = new KnightMove(board, myPosition, piece);
            return calculator.pieceMove();
        }
        else if (piece.getPieceType() == PieceType.ROOK){
            PieceMoveCalculator calculator = new RookMove(board, myPosition, piece);
            return calculator.pieceMove();
        }
        else if (piece.getPieceType() == PieceType.QUEEN){
            PieceMoveCalculator calculator = new QueenMove(board, myPosition, piece);
            return calculator.pieceMove();
        }
        else if (piece.getPieceType() == PieceType.KING){
            PieceMoveCalculator calculator = new KingMove(board, myPosition, piece);
            return calculator.pieceMove();
        }
        else if (piece.getPieceType() == PieceType.PAWN){
            PieceMoveCalculator calculator = new PawnMove(board, myPosition, piece);
            return calculator.pieceMove();
        }
        return List.of();
    }
}

abstract class PieceMoveCalculator{
    protected final ChessBoard squares;
    protected final ChessPosition position;
    protected final ChessPiece piece;

    //common direction help
    protected static final int[][] STRAIGHT_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    protected static final int[][] DIAGONAL_DIRECTIONS = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    };

    //not changed for all variable
    public PieceMoveCalculator(ChessBoard squares,ChessPosition position,ChessPiece piece){
        this.squares = squares;
        this.position = position;
        this.piece = piece;
    }

    public abstract Collection<ChessMove> pieceMove();

    protected boolean isInsideBoard(int row, int col) {
    return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    protected void addSlidingMoves(Collection<ChessMove> moves, int[][] directions) {
        for (int[] direction : directions) {
            int row = position.getRow() + direction[0];
            int col = position.getColumn() + direction[1];

            while (isInsideBoard(row, col)) {
                ChessPosition end = new ChessPosition(row, col);
                moves.add(new ChessMove(position, end, null));

                row += direction[0];
                col += direction[1];
            }
        }
    }
}

class BishopMove extends PieceMoveCalculator {
    public BishopMove(ChessBoard squares, ChessPosition position, ChessPiece piece) {
        super(squares, position, piece);
    }

    @Override
    public Collection<ChessMove> pieceMove() {
        Collection<ChessMove> moves = new ArrayList<>();
        addSlidingMoves(moves, DIAGONAL_DIRECTIONS);
        return moves;
    }

}

class RookMove extends PieceMoveCalculator{
    public RookMove(ChessBoard squares, ChessPosition position, ChessPiece piece) {
        super(squares, position, piece);
    }

    @Override
    public Collection<ChessMove> pieceMove() {
        Collection<ChessMove> moves = new ArrayList<>();
        addSlidingMoves(moves, STRAIGHT_DIRECTIONS);
        return moves;
    }
}
class QueenMove extends PieceMoveCalculator{
    public QueenMove(ChessBoard board, ChessPosition position, ChessPiece piece) {
        super(board, position, piece);
    }

    @Override
    public Collection<ChessMove> pieceMove() {
        Collection<ChessMove> moves = new ArrayList<>();
        addSlidingMoves(moves, STRAIGHT_DIRECTIONS);
        addSlidingMoves(moves, DIAGONAL_DIRECTIONS);
        return moves;
    }
}
class KingMove extends PieceMoveCalculator{
    public KingMove(ChessBoard board, ChessPosition position, ChessPiece piece) {
        super(board, position, piece);
    }
    @Override
    public Collection<ChessMove> pieceMove() {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[][] directions = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] direction : directions) {
            int r = row + direction[0];
            int c = col + direction[1];

            if (isInsideBoard(r, c)) {
                ChessPosition end = new ChessPosition(r, c);
                moves.add(new ChessMove(position, end, null));
            }
        }
        return moves;
    }
}
class KnightMove extends PieceMoveCalculator{
    public KnightMove(ChessBoard board, ChessPosition position, ChessPiece piece) {
        super(board, position, piece);
    }
    @Override
    public Collection<ChessMove> pieceMove() {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] direction : directions) {
            int r = row + direction[0];
            int c = col + direction[1];

            if (isInsideBoard(r, c)) {
                ChessPosition end = new ChessPosition(r, c);
                moves.add(new ChessMove(position, end, null));
            }
        }
        return moves;
    }
}
class PawnMove extends PieceMoveCalculator{
    public PawnMove(ChessBoard board, ChessPosition position, ChessPiece piece) {
        super(board, position, piece);
    }
    @Override
    public Collection<ChessMove> pieceMove() {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[][] directions = {
                {2, 1}, {2, -1}, {-2, 1}, {2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] direction : directions) {
            int r = row + direction[0];
            int c = col + direction[1];

            if (isInsideBoard(r, c)) {
                ChessPosition end = new ChessPosition(r, c);
                moves.add(new ChessMove(position, end, null));
            }
        }
        return moves;
    }
}