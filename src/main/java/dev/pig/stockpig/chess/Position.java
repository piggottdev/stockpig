package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Position stores all data required to represent a chess position (everything
 * that can be encoded/decoded to/from a FEN string).
 * Also, stores a history of moves and previous states for move un-make.
 * Wraps a Board providing material data and a MoveGenerator providing checks,
 * attacks and pin data.
 */
public final class Position {

    // State (package private to allow use in movegen)
    final Board board;
    Colour sideToMove;
    byte castlingRights;
    Square enPassantTarget;
    private int halfMoveClock;
    private int turn;

    // History
    private final List<State> history = new ArrayList<>(100);
    private record State(int move, byte castlingRights, Square enPassantTarget, int halfMoveClock){}

    // Moves (+ check, attack and pin information)
    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveList moves = new MoveList();


    // ====================================================================================================
    //                                  Constructors and Builders
    // ====================================================================================================

    private Position(final String fen) {
        final String[] parts = fen.split(" ");
        this.board              = Board.fromFen(parts[0]);
        this.sideToMove         = Colour.fromString(parts[1]);
        this.castlingRights     = Castling.fromString(parts[2]);
        this.enPassantTarget    = Square.fromString(parts[3]);
        this.halfMoveClock      = Integer.parseInt(parts[4]);
        this.turn               = Integer.parseInt(parts[5]);
        generateMoves();
    }

    /**
     * Create a chess starting position.
     * @return starting position
     */
    public static Position starting() {
        return new Position("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Create a chess position from a FEN string.
     * @return position from FEN
     */
    public static Position fromFen(final String fen) {
        return new Position(fen);
    }


    // ====================================================================================================
    //                                  Game States
    // ====================================================================================================

    /**
     * Get whether the position is terminal, whether from checkmate, stalemate or other
     * draw states.
     * @return is game over
     */
    public boolean isGameOver() {
        return this.moves.isEmpty();
    }

    /**
     * Get whether the position is currently checkmate.
     * @return is checkmate
     */
    public boolean isCheckmate() {
        return this.moves.isEmpty() && isCheck();
    }


    // ====================================================================================================
    //                                  Make / Unmake / Undo Moves
    // ====================================================================================================

    /**
     * Generate moves for the current position.
     */
    public void generateMoves() {
        this.moves.clear();
        this.moveGenerator.isCheck = false;
        if (halfMoveClock >= 50 || this.board.isDeadPosition()) return;
        this.moveGenerator.generate(this, this.moves);
    }

    /**
     * Make the move to the position.
     * @param move move
     */
    public void makeMove(final int move) {
        this.history.add(new State(move, this.castlingRights, this.enPassantTarget, this.halfMoveClock));

        this.board.makeMove(this.sideToMove, move);
        this.castlingRights = Castling.update(this.castlingRights, move);
        this.enPassantTarget = Move.isDoublePush(move) ? Move.from(move).shift(this.sideToMove.forward()) : Square.EMPTY;
        this.halfMoveClock = Move.isCapture(move) || Move.mover(move) == PieceType.PAWN ? 0 : this.halfMoveClock + 1;
        this.sideToMove = this.sideToMove.flip();
        if (this.sideToMove == Colour.WHITE) this.turn++;
        generateMoves();
    }

    /**
     * Unmake the last move to the position and regenerate legal move list.
     */
    public void undo() {
        unmakeMove();
        generateMoves();
    }

    /**
     * Unmake the last move to the position if present - do not regenerate legal move list,
     * useful for avoiding computation during search.
     */
    public void unmakeMove() {
        if (this.history.isEmpty()) return;
        final State prev = this.history.removeLast();

        this.sideToMove = this.sideToMove.flip();
        this.board.unmakeMove(this.sideToMove, prev.move);
        this.castlingRights = prev.castlingRights;
        this.enPassantTarget = prev.enPassantTarget;
        this.halfMoveClock = prev.halfMoveClock;
        if (this.sideToMove == Colour.BLACK) this.turn--;
    }


    // ====================================================================================================
    //                                  Accessors
    // ====================================================================================================

    /**
     * Get the list of legal moves.
     * @return legal move list
     */
    public MoveList moves() {
        return this.moves;
    }

    /**
     * Get the current team, the side to move.
     * @return side to move
     */
    public Colour sideToMove() {
        return this.sideToMove;
    }

    /**
     * Get the castling rights of both teams.
     * @return castling rights
     */
    public byte castlingRights() {
        return this.castlingRights;
    }

    /**
     * Get the en passant target, if any.
     * @return en passant target
     */
    public Square enPassantTarget() {
        return this.enPassantTarget;
    }

    /**
     * Get the half move clock, amount of moves without a capture or pawn push.
     * @return half move clock
     */
    public int halfMoveClock() {
        return this.halfMoveClock;
    }

    /**
     * Get the turn number.
     * @return turn number
     */
    public int turn() {
        return this.turn;
    }


    // ====================================================================================================
    //                                  Board Delegators
    // ====================================================================================================

    /**
     * Add a piece to a square on the board
     * @param piece piece
     * @param sq square
     */
    public void addPiece(final Piece piece, final Square sq) {
        this.board.addPiece(piece.colour(), piece.type(), sq);
    }

    /**
     * Remove a piece from a square on the board.
     * @param piece piece
     * @param sq square
     */
    public void removePiece(final Piece piece, final Square sq) {
        this.board.removePiece(piece.colour(), piece.type(), sq);
    }

    /**
     * Get the bitboard for a given colour's pieces.
     * @param c colour
     * @return pieces
     */
    public long pieces(final Colour c) {
        return this.board.pieces(c);
    }

    /**
     * Get the bitboard for a given piece type.
     * @param pt piece type
     * @return pieces
     */
    public long pieces(final PieceType pt) {
        return this.board.pieces(pt);
    }

    /**
     * Get the bitboard for a given piece.
     * @param p piece
     * @return pieces
     */
    public long pieces(final Piece p) {
        return this.board.pieces(p.colour(), p.type());
    }

    /**
     * Get the unoccupied bitboard.
     * @return unoccupied bitboard
     */
    public long unoccupied() {
        return this.board.unoccupied();
    }

    /**
     * Get the occupied bitboard.
     * @return occupied bitboard
     */
    public long occupied() {
        return this.board.occupied();
    }

    /**
     * Get the piece type on a square on the board.
     * @param sq square
     * @return piece type
     */
    public PieceType pieceTypeAt(final Square sq) {
        return this.board.pieceType(sq);
    }

    /**
     * Get the piece on a square on the board.
     * @param sq square
     * @return piece
     */
    public Piece pieceAt(final Square sq) {
        return this.board.piece(sq);
    }

    /**
     * Get whether the position is dead, insufficient material for a checkmate.
     * @return is position dead
     */
    public boolean isDeadPosition() {
        return this.board.isDeadPosition();
    }


    // ====================================================================================================
    //                                  Move Generator Delegators
    // ====================================================================================================

    /**
     * Get whether the current side's king is in check.
     * @return is king in check
     */
    public boolean isCheck() {
        return this.moveGenerator.isCheck;
    }

    /**
     * Get whether the current side's king is in double check.
     * @return is king in double check
     */
    public boolean isDoubleCheck() {
        return this.moveGenerator.isDoubleCheck;
    }

    /**
     * Get the bitboard of squares attacked/threatened by the other side.
     * @return attacked bitboard
     */
    public long attacked() {
        return this.moveGenerator.attacked;
    }

    /**
     * Get the bitboard of all currently checking pieces.
     * @return checking pieces bitboard
     */
    public long checkers() {
        return this.moveGenerator.checkers;
    }

    /**
     * Get the check ray bitboard, this is the line from a sliding piece causing check.
     * @return check ray bitboard
     */
    public long checkray() {
        return this.moveGenerator.checkRay;
    }

    /**
     * Get the bitboard of currently pinned pieces.
     * @return pinned pieces bitboard
     */
    public long pinned() {
        return this.moveGenerator.pinned;
    }

    /**
     * Get all current pins.
     * @return pins bitboard
     */
    public long pins() {
        return this.moveGenerator.pins[MoveGenerator.ALL];
    }

    /**
     * Get the current side's target square bitboard. This is the checkray/checkers if
     * the position in check or all enemy and unoccupied pieces if not.
     * @return target bitboard
     */
    public long target() {
        return this.moveGenerator.target;
    }
}
