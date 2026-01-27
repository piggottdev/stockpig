package dev.pig.stockpig.chess.bitboard;

import java.util.function.Consumer;

/**
 * Rank is an enum for all ranks on an 8x8 chess board, standard rank number notation.
 */
public enum Rank {
    // r prefix as can't start with number.
    r1, r2, r3, r4, r5, r6, r7, r8;


    private final long bitboard;

    Rank() {
        this.bitboard = 0xFFL << (8 * ordinal());
    }


    /**
     * Get the bitboard of the rank.
     * @return bitboard
     */
    public long bitboard() {
        return this.bitboard;
    }

    /**
     * Call the consumer with each rank in reverse order (8...1), top to bottom.
     * @param c file callback
     */
    public static void forEach(final Consumer<Rank> c) {
        for (int i = values().length - 1; i >= 0; i--)
            c.accept(values()[i]);
    }

    /**
     * Get the number of the rank as a string.
     * @return rank number string
     */
    @Override
    public String toString() {
        return Integer.toString(ordinal() + 1);
    }
}
