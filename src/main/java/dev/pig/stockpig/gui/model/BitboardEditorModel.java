package dev.pig.stockpig.gui.model;

import dev.pig.stockpig.chess.Position;
import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Square;

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
        this.bitboard ^= Square.of(i).bitboard();
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
            case Attacked -> pos.attacked();
            case Target -> pos.target();
            case Pinned -> pos.pinned();
            case Pins -> pos.pins();
            case Checkers -> pos.checkers();
            case Checks -> pos.checkray();
            case Unoccupied -> pos.unoccupied();
            case Occupied -> pos.occupied();
        };
    }
}
