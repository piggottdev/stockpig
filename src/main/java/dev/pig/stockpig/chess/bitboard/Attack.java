package dev.pig.stockpig.chess.bitboard;

/**
 * Attacks provides static functions for obtaining attack map bitboards.
 */
public final class Attack {

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
     * Occupied squares are required to determine slide stop.
     * @param bishop single bishop bitboard
     * @param occupied occupied bitboard
     * @return bishop attack map
     */
    public static long bishop(final long bishop, final long occupied) {
        return Magics.bAttack(Square.ofBitboard(bishop).ordinal(), occupied);
    }

    /**
     * Get an attack bitboard for a single occupancy rook bitboard.
     * Occupied squares are required to determine slide stop.
     * @param rook single rook bitboard
     * @param occupied occupied bitboard
     * @return rook attack map
     */
    public static long rook(final long rook, final long occupied) {
        return Magics.rAttack(Square.ofBitboard(rook).ordinal(), occupied);
    }

    /**
     * Get an attack bitboard for a single occupancy queen bitboard.
     * Occupied squares are required to determine slide stop.
     * @param queen single queen bitboard
     * @param occupied occupied bitboard
     * @return queen attack map
     */
    public static long queen(final long queen, final long occupied) {
        return rook(queen, occupied) | bishop(queen, occupied);
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


    private Attack() {}
}
