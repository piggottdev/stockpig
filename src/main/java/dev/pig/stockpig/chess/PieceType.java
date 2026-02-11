package dev.pig.stockpig.chess;

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

    /**
     * Get the piece type from a piece type character string.
     * @param s piece type character string
     * @return piece type
     */
    public static PieceType fromString(final String s) {
        return switch (s.toLowerCase())
        {
            case "k" -> KING;
            case "p" -> PAWN;
            case "n" -> KNIGHT;
            case "b" -> BISHOP;
            case "r" -> ROOK;
            case "q" -> QUEEN;
            default -> throw new IllegalArgumentException("unknown piece type: " + s);
        };
    }

    /**
     * Get the piece type character string.
     * @return piece type character string
     */
    @Override
    public String toString() {
        return switch (this)
        {
            case EMPTY  -> "";
            case KING   -> "k";
            case PAWN   -> "p";
            case KNIGHT -> "n";
            case BISHOP -> "b";
            case ROOK   -> "r";
            case QUEEN  -> "q";
        };
    }
}
