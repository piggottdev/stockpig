package dev.pig.stockpig.chess.bitboard;

/**
 * Attacks provides static functions for obtaining attack map
 * bitboards for single pieces.
 */
public final class Attack {

    /**
     * Get an attack bitboard for a king on a given square.
     * @param sq king square
     * @return king attack map
     */
    public static long king(final byte sq) {
        return KING_ATTACKS[sq];
    }

    /**
     * Get an attack bitboard for a knight on a given square.
     * @param sq knight square
     * @return knight attack map
     */
    public static long knight(final byte sq) {
        return KNIGHT_ATTACKS[sq];
    }

    /**
     * Get an attack bitboard for a bishop on a given square.
     * Occupied squares are required to determine slide stop.
     * @param sq bishop square
     * @param occupied occupied bitboard
     * @return bishop attack map
     */
    public static long bishop(final byte sq, final long occupied) {
        return Magics.bAttack(sq, occupied);
    }

    /**
     * Get an attack bitboard for a rook on a given square.
     * Occupied squares are required to determine slide stop.
     * @param sq rook square
     * @param occupied occupied bitboard
     * @return rook attack map
     */
    public static long rook(final byte sq, final long occupied) {
        return Magics.rAttack(sq, occupied);
    }

    /**
     * Get an attack bitboard for a queen on a given square.
     * Occupied squares are required to determine slide stop.
     * @param sq queen square
     * @param occupied occupied bitboard
     * @return queen attack map
     */
    public static long queen(final byte sq, final long occupied) {
        return rook(sq, occupied) | bishop(sq, occupied);
    }


    // ====================================================================================================
    //                                  Pre-computed Lookups
    // ====================================================================================================

    private static final long[] KING_ATTACKS = new long[64];
    static {
        for (byte i = 0; i < KING_ATTACKS.length; i++) {
            final long king = Bitboard.ofSquare(i);
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
        for (byte i = 0; i < KNIGHT_ATTACKS.length; i++) {
            final long knight = Bitboard.ofSquare(i);
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
