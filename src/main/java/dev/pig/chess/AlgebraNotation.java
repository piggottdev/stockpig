package dev.pig.chess;

/**
 * Provides methods for converting to and from algebra notation (a1 ... h8).
 *
 * @see Bitboard
 */
public class AlgebraNotation {

    private AlgebraNotation() {}

    /**
     * Parse algebra notation into 0-63 square index.
     *
     * @param algebra algebra notation
     * @return square index, -1 if parse error
     */
    public static int toIndex(final String algebra) {
        try {
            final int file = ((int) algebra.charAt(0)) - 97;
            final int rank = Character.digit(algebra.charAt(1), 10) - 1;
            return rank * 8 + file;
        } catch (StringIndexOutOfBoundsException | NullPointerException e) {
            return -1;
        }
    }

    /**
     * Get the algebra notation for a 0-63 square index.
     *
     * @param index square index
     * @return algebra notation
     */
    public static String fromIndex(final int index) {
        if (index == -1) return "-";

        final char file = ((char) (index % 8 + 97)) ;
        final int rank = index / 8 + 1;
        return file + "" + rank;
    }

    /**
     * Parse algebra notation into a bitboard.
     *
     * @param algebra algebra notation
     * @return bitboard
     */
    public static long toBitboard(final String algebra) {
        final int index = toIndex(algebra);
        return index == -1 ? Bitboard.EMPTY : Bitboard.INDEX[index];
    }

    /**
     * Get the algebra notation for a bitboard.
     * If a bitboard has more than one bit flipped, the least significant 1 bit will be converted.
     *
     * @param bit bitboard
     * @return algebra notation
     */
    public static String fromBitboard(final long bit) {
        if (bit == Bitboard.EMPTY) return "-";

        final int index = Long.numberOfTrailingZeros(bit);
        return fromIndex(index);
    }

}
