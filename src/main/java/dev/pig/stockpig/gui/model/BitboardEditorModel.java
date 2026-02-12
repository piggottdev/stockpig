package dev.pig.stockpig.gui.model;

import dev.pig.stockpig.chess.core.Position;
import dev.pig.stockpig.chess.core.bitboard.Bitboard;

/**
 * Bitboard editor model for tracking and editing selected bitboard.
 */
public final class BitboardEditorModel {

    /**
     * Bitboards available from the position.
     */
    public enum BitboardSelector {
        Attacked,
        Target,
        Pinned,
        Pins,
        Checkers,
        Checks,
        Unoccupied,
        Occupied
    }


    private long bitboard = Bitboard.EMPTY;

    /**
     * Get the bitboard.
     * @return bitboard
     */
    public long bitboard() {
        return this.bitboard;
    }

    /**
     * Reset the bitboard.
     */
    public void reset() {
        this.bitboard = Bitboard.EMPTY;
    }

    /**
     * Toggle the selected square index bit in the bitboard.
     * @param i square index
     */
    public void toggleBit(final int i) {
        this.bitboard ^= Bitboard.ofSquare(i);
    }

    /**
     * Set the bitboard to the selected bitboard from the position.
     * @param selector bitboard selector
     * @param pos position
     */
    public void set(final BitboardSelector selector, final Position pos) {
        this.bitboard = getBitboard(selector, pos);
    }

    /**
     * Add or union the selected bitboard from the position with the current bitboard.
     * @param selector bitboard selector
     * @param pos position
     */
    public void add(final BitboardSelector selector, final Position pos) {
        this.bitboard |= getBitboard(selector, pos);
    }

    /**
     * Get the selected bitboard from the position.
     * @param selector bitboard selector
     * @param pos position
     * @return bitboard
     */
    private long getBitboard(final BitboardSelector selector, final Position pos) {
        return switch (selector)
        {
            case Attacked -> pos.moveGenerator().attacked();
            case Target -> pos.moveGenerator().target();
            case Pinned -> pos.moveGenerator().pinned();
            case Pins -> pos.moveGenerator().pins();
            case Checkers -> pos.moveGenerator().checkers();
            case Checks -> pos.moveGenerator().checkray();
            case Unoccupied -> pos.board().unoccupied();
            case Occupied -> pos.board().occupied();
        };
    }
}
