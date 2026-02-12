package dev.pig.stockpig.chess.core;

/**
 * Piece type is an enum for each type of chess piece, including the empty piece.
 */
// TODO: Candidate optimisation: Test removing enum in favour of int/byte
public enum PieceType {
    EMPTY, KING, PAWN, KNIGHT, BISHOP, ROOK, QUEEN;

    private static final PieceType[] VALUES = values();


    /**
     * Get the piece type for the given piece index/ordinal (0...6).
     * @param i index
     * @return piece type
     */
    public static PieceType of(final int i) {
        return VALUES[i];
    }
}
