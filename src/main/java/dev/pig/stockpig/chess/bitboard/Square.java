package dev.pig.stockpig.chess.bitboard;

/**
 * Square is an enum for all co-ordinates on an 8x8 chess board,
 * using algebraic notation, file letter, rank number.
 */
public enum Square {
    // Vertically flipped, A1 (0) would appear in the bottom left visually.
    A1, B1, C1, D1, E1, F1, G1, H1,
    A2, B2, C2, D2, E2, F2, G2, H2,
    A3, B3, C3, D3, E3, F3, G3, H3,
    A4, B4, C4, D4, E4, F4, G4, H4,
    A5, B5, C5, D5, E5, F5, G5, H5,
    A6, B6, C6, D6, E6, F6, G6, H6,
    A7, B7, C7, D7, E7, F7, G7, H7,
    A8, B8, C8, D8, E8, F8, G8, H8,
    EMPTY;


    /**
     * Get the square for the given square index/ordinal (0...63).
     * @param i index
     * @return square
     */
    // TODO: Candidate optimisation: Test caching values() or removing Square enum in favour of an int/byte
    // TODO: Candidate optimisation: Remove bounds check
    public static Square of(final int i) {
        return i < 0 || i >= values().length ? EMPTY : values()[i];
    }

    /**
     * Get the square for the given file, rank.
     * @param f file
     * @param r rank
     * @return square at the intersection of file and rank
     */
    public static Square of(final File f, final Rank r) {
        return fromBitboard(f.bitboard() & r.bitboard());
    }

    /**
     * Get the square for the given single occupancy bitboard.
     * @param bb bitboard
     * @return square
     */
    // TODO: Candidate optimisation: Test caching values() or removing Square enum in favour of an int/byte
    public static Square fromBitboard(final long bb) {
        return values()[Long.numberOfTrailingZeros(bb)];
    }

    /**
     * Get the single occupancy bitboard of the square.
     * @return bitboard
     */
    public long bitboard() {
        return this == EMPTY ? 0L : 1L << ordinal();
    }

    /**
     * Get the square moved once in a given direction.
     * Does not prevent file wrap around.
     * @param d direction
     * @return moved square
     */
    // TODO: Candidate optimisation: Test caching values() or removing Square enum in favour of an int/byte
    // TODO: Candidate optimisation: Test removing bounds check
    public Square move(final Direction d) {
        final int o = ordinal() + d.offset();
        return (0 <= o && o <= 63) ? values()[o] : EMPTY;
    }

    /**
     * Get the square from an algebraic notation string.
     * @return square
     */
    public static Square fromString(final String s) {
        return "-".equals(s) ? EMPTY : valueOf(s.toUpperCase());
    }

    /**
     * Get the algebraic notation string of the square.
     * @return algebraic notation string
     */
    @Override
    public String toString() {
        return this == EMPTY ? "-" : super.toString().toLowerCase();
    }
}