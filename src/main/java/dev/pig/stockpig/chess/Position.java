package dev.pig.stockpig.chess;

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
    // TODO: Candidate optimisation: Test different data structures (linked list, stack)
    private final List<State> history = new ArrayList<>(100);
    private record State(int move, byte castlingRights, Square enPassantTarget, int halfMoveClock){}

    // Moves (+ check, attack and pin information)
    private final MoveGenerator moveGenerator = new MoveGenerator();
    // TODO: Candidate optimisation: Test if array list is exactly the same to remove move list
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
    //                                  Accessors
    // ====================================================================================================

    /**
     * Get list of legal moves.
     * @return legal move list
     */
    public MoveList moves() {
        return this.moves;
    }


    // ====================================================================================================
    //                                  Piece and Board Queries
    // ====================================================================================================



    // ====================================================================================================
    //                                  Make / Unmake / Undo Moves
    // ====================================================================================================

    /**
     * Generate moves for the current position.
     */
    public void generateMoves() {
        this.moves.clear();
        if (halfMoveClock >= 50 || this.board.isDeadPosition()) return; // TODO: I think it's possible to return black/white win for dead position/half move clock if last move was check
        this.moveGenerator.generate(this, this.moves);
    }

    /**
     * Make the move to the position.
     * @param move move
     */
    public void makeMove(final int move) {
        this.history.add(new State(move, this.castlingRights, this.enPassantTarget, this.halfMoveClock));

        this.board.makeMove(this.sideToMove, move);
        this.castlingRights = Castling.applyMove(this.sideToMove, this.castlingRights, move);
        this.enPassantTarget = Move.isDoublePush(move) ? Move.from(move).move(this.sideToMove.forward()) : Square.EMPTY;
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
}
