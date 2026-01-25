package dev.pig.stockpig.chess;

/**
 * Piece type is an enum for each type of chess piece, including the empty piece.
 */
public enum PieceType {
    EMPTY, KING, PAWN, KNIGHT, BISHOP, ROOK, QUEEN;


    /**
     * Get the piece type for the given index/ordinal (0...6).
     * @param i index
     * @return piece type
     */
    // TODO: Candidate optimisation: Test caching values() or removing PieceType enum in favour of an int/byte
    // TODO: Candidate optimisation: Remove bounds check
    public static PieceType of(final int i) {
        return i < 0 || i >= values().length ? EMPTY : values()[i];
    }

    /**
     * Get a piece type from a character.
     * @param c piece type character
     * @return piece type
     */
    public static PieceType fromChar(final char c) {
        return switch (Character.toLowerCase(c))
        {
            case 'k' -> KING;
            case 'p' -> PAWN;
            case 'n' -> KNIGHT;
            case 'b' -> BISHOP;
            case 'r' -> ROOK;
            case 'q' -> QUEEN;
            default  -> EMPTY;
        };
    }

    /**
     * Get the piece type character as a string.
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
