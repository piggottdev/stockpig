package dev.pig.stockpig.chess.bitboard;

/**
 * Attacks provides static functions for obtaining attack map bitboards.
 */
public final class Attacks {

    // TODO: Candidate optimisation: Magics

    /**
     * Get an attack bitboard for a single occupancy king bitboard.
     * @param king single king bitboard
     * @return king attack map
     */
    public static long king(final long king) {
        return KING_ATTACKS[Square.ofBitboard(king).ordinal()];
    }

    /**
     * Get an attack bitboard for a single occupancy knight bitboard.
     * @param knight single knight bitboard
     * @return knight attack map
     */
    public static long knight(final long knight) {
        return KNIGHT_ATTACKS[Square.ofBitboard(knight).ordinal()];
    }

    /**
     * Get an attack bitboard for a single occupancy bishop bitboard.
     * Unoccupied squares are required to determine slide stop.
     * @param bishop single bishop bitboard
     * @param unoccupied unoccupied bitboard
     * @return bishop attack map
     */
    public static long bishop(final long bishop, final long unoccupied) {
        return  slide(bishop, unoccupied, Direction.NE) |
                slide(bishop, unoccupied, Direction.SW) |
                slide(bishop, unoccupied, Direction.SE) |
                slide(bishop, unoccupied, Direction.NW);
    }

    /**
     * Get an attack bitboard for a single occupancy rook bitboard.
     * Unoccupied squares are required to determine slide stop.
     * @param rook single rook bitboard
     * @param unoccupied unoccupied bitboard
     * @return rook attack map
     */
    public static long rook(final long rook, final long unoccupied) {
        return  slide(rook, unoccupied, Direction.N) |
                slide(rook, unoccupied, Direction.S) |
                slide(rook, unoccupied, Direction.E) |
                slide(rook, unoccupied, Direction.W);
    }

    /**
     * Get an attack bitboard for a single occupancy queen bitboard.
     * Unoccupied squares are required to determine slide stop.
     * @param queen single queen bitboard
     * @param unoccupied unoccupied bitboard
     * @return queen attack map
     */
    public static long queen(final long queen, final long unoccupied) {
        return rook(queen, unoccupied) | bishop(queen, unoccupied);
    }

    /**
     * Get an attack bitboard for sliding pieces.
     * Usable with multiple occupancy piece bitboards.
     * @param pieces pieces bitboard
     * @param unoccupied unoccupied bitboard
     * @param d direction
     * @return piece attack map
     */
    public static long slide(final long pieces, final long unoccupied, final Direction d) {
        return Bitboard.shift(Bitboard.fillInto(pieces, d, unoccupied), d);
    }


    // ====================================================================================================
    //                                  Pre-computed Lookups
    // ====================================================================================================

    private static final long[] KING_ATTACKS = new long[64];
    static {
        for (int i = 0; i < KING_ATTACKS.length; i++) {
            final long king = Square.of(i).bitboard();
            KING_ATTACKS[i] =
                    Bitboard.shift(king, Direction.N)  |
                    Bitboard.shift(king, Direction.NE) |
                    Bitboard.shift(king, Direction.E)  |
                    Bitboard.shift(king, Direction.SE) |
                    Bitboard.shift(king, Direction.S)  |
                    Bitboard.shift(king, Direction.SW) |
                    Bitboard.shift(king, Direction.W)  |
                    Bitboard.shift(king, Direction.NW);
        }
    }

    private static final long[] KNIGHT_ATTACKS = new long[64];
    static {
        for (int i = 0; i < KNIGHT_ATTACKS.length; i++) {
            final long knight = Square.of(i).bitboard();
            KNIGHT_ATTACKS[i] =
                    Bitboard.shift(knight, Direction.NNE) |
                    Bitboard.shift(knight, Direction.NEE) |
                    Bitboard.shift(knight, Direction.SEE) |
                    Bitboard.shift(knight, Direction.SSE) |
                    Bitboard.shift(knight, Direction.SSW) |
                    Bitboard.shift(knight, Direction.SWW) |
                    Bitboard.shift(knight, Direction.NWW) |
                    Bitboard.shift(knight, Direction.NNW);
        }
    }


    private Attacks() {}
}
