package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }
        Collection <ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection <ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move:moves){
            ChessPosition end = move.getEndPosition();
            ChessPiece end_piece = board.getPiece(end);
            ChessPiece.PieceType promotionType = move.getPromotionPiece();
            if (promotionType != null){
                ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(),promotionType);
                board.addPiece(end,promotionPiece);
            }
            else {
                board.addPiece(end, piece);
            }

            board.addPiece(startPosition, null);
            //save original board and temporarily apply the move to
            if (!isInCheck(piece.getTeamColor())){
                validMoves.add(move);
            }
            board.addPiece(end, end_piece);
            board.addPiece(startPosition, piece);
        }
        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        ChessPiece end_piece = null;
        if (piece == null){
            throw new InvalidMoveException("No piece at start");
        }
        else if (piece.getTeamColor() != teamTurn){
            throw new InvalidMoveException("Not in turn");
        }

        ChessPiece.PieceType promotion_type = move.getPromotionPiece();
        Collection<ChessMove> validMoves = validMoves(start);
        if (!validMoves.contains(move)){
            throw new InvalidMoveException("illegal move");
        }
        if (promotion_type != null){
            end_piece = new ChessPiece(teamTurn,promotion_type);
            board.addPiece(end,end_piece);
            board.addPiece(start,null);
        }
        else {
            board.addPiece(end, piece);
            board.addPiece(start, null);
        }

        if (teamTurn == TeamColor.BLACK){
            teamTurn = TeamColor.WHITE;
        }
        else {
            teamTurn = TeamColor.BLACK;
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> opponent_positions = new ArrayList<>();
        ChessPosition king_position = null;
        for (int i=1; i<=8;i++){
            for (int j=1; j<=8;j++){
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    king_position = position;
                }
                //get opponent pieces
                if (piece != null && piece.getTeamColor() != teamColor){
                    opponent_positions.add(position);
                }
            }
        }
        for (ChessPosition position: opponent_positions){
            ChessPiece piece = board.getPiece(position);
            Collection<ChessMove> moves = piece.pieceMoves(board,position);
            for (ChessMove move:moves){
                ChessPosition end = move.getEndPosition();
                if (end.equals(king_position)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
