package dev.pig.stockpig.chess.core;

import dev.pig.stockpig.chess.core.bitboard.Square;
import dev.pig.stockpig.chess.notation.Fen;

import java.util.ArrayList;
import java.util.List;

/**
 * Position stores all data required to represent a chess position.
 * Also, stores a history of moves and previous states for move un-make.
 * Wraps a Board providing material data and a MoveGenerator providing checks,
 * attacks and pin data.
 */
public final class Position {

    // State
    private final Board board;
    private boolean sideToMove;
    private byte castlingRights;
    private byte enPassantTarget;
    private int halfMoveClock;
    private int turn;

    // History
    private final List<State> history = new ArrayList<>(100);
    private record State(int move, byte castlingRights, byte enPassantTarget, int halfMoveClock) {}

    // Moves (+ check, attack and pin information)
    private final MoveGenerator moveGenerator = new MoveGenerator();
    private final MoveList moves = new MoveList();


    // ====================================================================================================
    //                                  Constructors and Builders
    // ====================================================================================================

    public Position(final Board board, final boolean sideToMove, final byte castlingRights,
                    final byte enPassantTarget, final int halfMoveClock, final int turn) {
        this.board = board;
        this.sideToMove = sideToMove;
        this.castlingRights = castlingRights;
        this.enPassantTarget = enPassantTarget;
        this.halfMoveClock = halfMoveClock;
        this.turn = turn;
        generateMoves();
    }

    /**
     * Get a standard starting position.
     * @return starting position
     */
    public static Position starting() {
        return Fen.startingPosition();
    }


    // ====================================================================================================
    //                                  Accessors
    // ====================================================================================================

    /**
     * Get the board, all material state.
     * @return board
     */
    public Board board() {
        return this.board;
    }

    /**
     * Get the current team, the side to move.
     * @return side to move
     */
    public boolean sideToMove() {
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
    public byte enPassantTarget() {
        return this.enPassantTarget;
    }

    /**
     * Get the half move clock, number of half-moves (plies) since last capture or pawn advance.
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

    /**
     * Get the list of legal moves.
     * @return legal move list
     */
    public MoveList moves() {
        return this.moves;
    }

    /**
     * Get the move generator, has check, attack and pin data.
     * @return move generator
     */
    public MoveGenerator moveGenerator() {
        return this.moveGenerator;
    }


    // ====================================================================================================
    //                                  Game States
    // ====================================================================================================

    /**
     * Returns true if the position is terminal (checkmate, stalemate, or other draw conditions).
     * @return is game over
     */
    public boolean isGameOver() {
        return this.moves.isEmpty();
    }

    /**
     * Get whether the position is checkmate.
     * @return is checkmate
     */
    public boolean isCheckmate() {
        return this.moves.isEmpty() && isCheck();
    }

    /**
     * Get whether the current side's king is in check.
     * @return is king in check
     */
    public boolean isCheck() {
        return this.moveGenerator.isCheck();
    }

    /**
     * Get whether the position is dead, insufficient material for a checkmate.
     * @return is position dead
     */
    public boolean isDeadPosition() {
        return this.board.isDeadPosition();
    }


    // ====================================================================================================
    //                                  Make / Unmake / Undo Moves
    // ====================================================================================================

    /**
     * Generate moves for the current position.
     */
    public void generateMoves() {
        this.moves.clear();
        this.moveGenerator.resetCheck();
        if (this.halfMoveClock >= 50 || this.board.isDeadPosition()) return;
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
        this.enPassantTarget = Move.isDoublePush(move) ? (byte) (Move.from(move) + Colour.forward(this.sideToMove).offset()) : Square.EMPTY;
        this.halfMoveClock = Move.isCapture(move) || Move.mover(move) == PieceType.PAWN ? 0 : this.halfMoveClock + 1;
        this.sideToMove = Colour.flip(this.sideToMove);
        if (this.sideToMove == Colour.WHITE) this.turn++;
        generateMoves();
    }

    /**
     * Undoes the last move and regenerates the legal move list.
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

        this.sideToMove = Colour.flip(this.sideToMove);
        this.board.unmakeMove(this.sideToMove, prev.move);
        this.castlingRights = prev.castlingRights;
        this.enPassantTarget = prev.enPassantTarget;
        this.halfMoveClock = prev.halfMoveClock;
        if (this.sideToMove == Colour.BLACK) this.turn--;
    }


    // ====================================================================================================
    //                                  Fen Utils
    // ====================================================================================================

    /**
     * Get a FEN string for the current position.
     * @return FEN string.
     */
    public String toFen() {
        return Fen.format(this);
    }

    /**
     * Build a position from a FEN string.
     * @param fen FEN string
     * @return position
     * @throws Fen.ParseException invalid FEN exception
     */
    public static Position fromFen(final String fen) throws Fen.ParseException {
        return Fen.parse(fen);
    }
}
