package dev.pig.stockpig.chess.bitboard;

import java.util.function.Consumer;

/**
 * File is an enum for all files on an 8x8 chess board, standard file letter notation.
 */
public enum File {
    A, B, C, D, E, F, G, H;


    private final long bitboard;

    File() {
        this.bitboard = 0x101010101010101L << ordinal();
    }


    /**
     * Get the bitboard of the file.
     * @return bitboard
     */
    public long bitboard() {
        return this.bitboard;
    }

    /**
     * Call the consumer with each file (a...h).
     * @param c file callback
     */
    public static void forEach(final Consumer<File> c) {
        for (final File f : values())
            c.accept(f);
    }

    /**
     * Get the lowercase letter of the file.
     * @return lowercase file letter
     */
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
