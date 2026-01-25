package dev.pig.stockpig.chess;

/**
 * Direction is an enum for all possible directions a piece can move on a chess board,
 * including knight L-shapes. The enum uses compass directions as looking at the board with
 * A1 in the bottom left.
 * The offset and mask are stored with the enum to be used when bitboard shifting.
 */
public enum Direction {
    N   (8,     Bitboard.ALL),
    S   (-8,    Bitboard.ALL),
    E   (1,     Bitboard.NOT_FILE_A),
    W   (-1,    Bitboard.NOT_FILE_H),
    NE  (9,     Bitboard.NOT_FILE_A),
    SW  (-9,    Bitboard.NOT_FILE_H),
    SE  (-7,    Bitboard.NOT_FILE_A),
    NW  (7,     Bitboard.NOT_FILE_H),
    NNE (17,    Bitboard.NOT_FILE_A),
    NEE (10,    Bitboard.NOT_FILE_AB),
    NNW (15,    Bitboard.NOT_FILE_H),
    NWW (6,     Bitboard.NOT_FILE_GH),
    SSE (-15,   Bitboard.NOT_FILE_A),
    SEE (-6,    Bitboard.NOT_FILE_AB),
    SSW (-17,   Bitboard.NOT_FILE_H),
    SWW (-10,   Bitboard.NOT_FILE_GH);


    private final int offset;
    private final long mask;

    Direction(final int offset, final long mask) {
        this.offset = offset;
        this.mask = mask;
    }


    /**
     * Return the offset (or shift) of the direction.
     * @return offset/shift
     */
    public int offset() {
        return this.offset;
    }

    /**
     * Return the post-shift mask that prevents file wrap around of the direction.
     * @return mask
     */
    public long mask() {
        return this.mask;
    }
}
