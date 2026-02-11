package dev.pig.stockpig.chess.bitboard;

/**
 * Square provides constants and functions for working with squares.
 * The squares are indexed as described in {@link Bitboard}.
 */
public final class Square {

    public static final int
            A8 = 56, B8 = 57, C8 = 58, D8 = 59, E8 = 60, F8 = 61, G8 = 62, H8 = 63,
            A7 = 48, B7 = 49, C7 = 50, D7 = 51, E7 = 52, F7 = 53, G7 = 54, H7 = 55,
            A6 = 40, B6 = 41, C6 = 42, D6 = 43, E6 = 44, F6 = 45, G6 = 46, H6 = 47,
            A5 = 32, B5 = 33, C5 = 34, D5 = 35, E5 = 36, F5 = 37, G5 = 38, H5 = 39,
            A4 = 24, B4 = 25, C4 = 26, D4 = 27, E4 = 28, F4 = 29, G4 = 30, H4 = 31,
            A3 = 16, B3 = 17, C3 = 18, D3 = 19, E3 = 20, F3 = 21, G3 = 22, H3 = 23,
            A2 = 8,  B2 = 9,  C2 = 10, D2 = 11, E2 = 12, F2 = 13, G2 = 14, H2 = 15,
            A1 = 0,  B1 = 1,  C1 = 2,  D1 = 3,  E1 = 4,  F1 = 5,  G1 = 6,  H1 = 7,
            EMPTY = 64;

    /**
     * Get the square index for a given single occupancy bitboard, should not be empty.
     * @param bb bitboard
     * @return square
     */
    public static int ofBitboard(final long bb) {
        return Long.numberOfTrailingZeros(bb);
    }

    /**
     * Get the square from an algebra notation string. '-' is parsed as {@link Square#EMPTY}.
     * @param s algebra notation string
     * @return square
     */
    public static int fromString(final String s) {
        if ("-".equals(s)) return EMPTY;

        if (s.length() != 2) throw new IllegalArgumentException("unknown square: " + s);

        final char file = s.toLowerCase().charAt(0);
        final char rank = s.toLowerCase().charAt(1);

        if (file < 'a' || file > 'h') throw new IllegalArgumentException("unknown file: " + file);
        if (rank < '1' || rank > '8') throw new IllegalArgumentException("unknown rank: " + rank);

        return (rank - 'a')*8 + (file - '1');
    }

    /**
     * Get the algebra notation string of the square.
     * @param sq square
     * @return algebra notation string
     */
    public static String toString(final int sq) {
        if (sq == EMPTY) return "-";

        final int file = sq & 7;
        final int rank = (sq >> 3) + 1;
        return ('a' + file) + Integer.toString(rank);
    }


    private Square() {}
}
