package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.Move;
import dev.pig.stockpig.chess.MoveList;
import dev.pig.stockpig.chess.Piece;
import dev.pig.stockpig.chess.Position;
import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Chess model is an adapter between a chess position and the GUI.
 * Manages interactions on a chess board such as selecting a piece.
 */
public final class StockpigModel {

    private Position position         = Position.starting();
    private Square selected           = Square.EMPTY;
    private final MoveList legalMoves = new MoveList();


    // ====================================================================================================
    //                                  Helpers
    // ====================================================================================================

    /**
     * Clear the selected square and legal moves, state that must be reset on most
     * operations.
     */
    public void clear() {
        this.selected = Square.EMPTY;
        this.legalMoves.clear();
    }

    /**
     * Create a list of square index integers from a bitboard.
     * @param bb bitboard
     * @return list of square indexes
     */
    private static List<Integer> bitboardToSquareIndexList(final long bb) {
        final List<Integer> squares = new ArrayList<>(Bitboard.count(bb));
        Bitboard.forEach(bb, bit -> squares.add(Square.ofBitboard(bit).ordinal()));
        return squares;
    }


    // ====================================================================================================
    //                                  Accessors
    // ====================================================================================================

    /**
     * Get the currently selected square a list. Empty list if not selected.
     * @return selected square list
     */
    public List<Integer> selected() {
        return this.selected == Square.EMPTY ? List.of() : List.of(this.selected.ordinal());
    }

    /**
     * Get the pieces on the board in square index order.
     * @return pieces in square order
     */
    public List<Piece> pieces() {
        final List<Piece> pieces = new ArrayList<>(64);
        for (int i = 0; i < 64; i++) {
            pieces.add(this.position.pieceAt(Square.of(i)));
        }
        return pieces;
    }

    /**
     * Get the currently selected pieces possible destination squares.
     * @return destination squares
     */
    public List<Integer> destinations() {
        final List<Integer> squares = new ArrayList<>();
        for (int i = 0; i < this.legalMoves.size(); i++) {
            final int move = this.legalMoves.get(i);
            squares.add(Move.to(move).ordinal());
        }
        return squares;
    }

    /**
     * Get the currently attacked squares.
     * @return attacked squares
     */
    public List<Integer> attacked() {
        return bitboardToSquareIndexList(this.position.attacked());
    }

    /**
     * Get the target squares for the current team.
     * @return target squares
     */
    public List<Integer> target() {
        return bitboardToSquareIndexList(this.position.target());
    }

    /**
     * Get the pin line squares.
     * @return pin squares
     */
    public List<Integer> pins() {
        return bitboardToSquareIndexList(this.position.pins());
    }


    // ====================================================================================================
    //                                  Operations
    // ====================================================================================================

    /**
     * Start a new game from the starting position.
     */
    public void newGame() {
        this.position = Position.starting();
        clear();
    }

    /**
     * Load a position from a fen string.
     * @param fen fen string
     */
    public void loadFen(final String fen) {
        this.position = Position.fromFen(fen);
        clear();
    }

    /**
     * Undo the last move.
     */
    public void undo() {
        this.position.undo();
        clear();
    }


    // ====================================================================================================
    //                                  Square Selection
    // ====================================================================================================

    /**
     * Attempt to select a square. This will select a piece if is able to move.
     * If a piece is already selected it will attempt to make the move if legal.
     * @param sqi selected square index
     * @return whether a move has been made (pieces must be redrawn)
     */
    public boolean select(final int sqi) {
        final Square sq = Square.of(sqi);

        // Piece already selected, attempt to make move
        if (this.selected != Square.EMPTY) {
            for (int i = 0; i < this.legalMoves.size(); i++) {
                if (Move.to(this.legalMoves.get(i)) == sq) {
                    this.position.makeMove(this.legalMoves.get(i));
                    clear();
                    return true;
                }
            }
        }
        clear();

        // This is a new selection
        for (int i = 0; i < this.position.moves().size(); i++) {
            if (Move.from(this.position.moves().get(i)) == sq) {
                this.legalMoves.add(this.position.moves().get(i));
            }
        }

        if (!this.legalMoves.isEmpty()) this.selected = sq;
        return false;
    }
}
