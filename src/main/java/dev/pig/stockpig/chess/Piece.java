package dev.pig.stockpig.chess;

/**
 * Piece is an enum for each chess piece and colour combination.
 */
public enum Piece {
    EMPTY       (null,  PieceType.EMPTY),
    W_KING      (Colour.WHITE, PieceType.KING),
    W_PAWN      (Colour.WHITE, PieceType.PAWN),
    W_KNIGHT    (Colour.WHITE, PieceType.KNIGHT),
    W_BISHOP    (Colour.WHITE, PieceType.BISHOP),
    W_ROOK      (Colour.WHITE, PieceType.ROOK),
    W_QUEEN     (Colour.WHITE, PieceType.QUEEN),
    B_KING      (Colour.BLACK, PieceType.KING),
    B_PAWN      (Colour.BLACK, PieceType.PAWN),
    B_KNIGHT    (Colour.BLACK, PieceType.KNIGHT),
    B_BISHOP    (Colour.BLACK, PieceType.BISHOP),
    B_ROOK      (Colour.BLACK, PieceType.ROOK),
    B_QUEEN     (Colour.BLACK, PieceType.QUEEN);

    private static final Piece[] VALUES = values();


    private final Colour colour;
    private final PieceType pieceType;

    Piece(final Colour colour, final PieceType pieceType) {
        this.colour = colour;
        this.pieceType = pieceType;
    }


    /**
     * Get the piece for the given colour and piece type.
     * @param c colour
     * @param pt piece type
     * @return piece
     */
    public static Piece of(final Colour c, final PieceType pt) {
        return pt == PieceType.EMPTY ? EMPTY : VALUES[(c == Colour.WHITE ? 0 : W_QUEEN.ordinal()) + pt.ordinal()];
    }

    /**
     * Get the colour of the piece.
     * @return colour
     */
    public Colour colour() {
        return this.colour;
    }

    /**
     * Get the piece type of the piece.
     * @return piece type
     */
    public PieceType type() {
        return this.pieceType;
    }

    /**
     * Get the piece from a piece character string.
     * @param s piece character string
     * @return piece
     */
    public static Piece fromString(final String s) {
        final PieceType pt = PieceType.fromString(s);
        return of(Colour.of(Character.isUpperCase(s.charAt(0))), pt);
    }

    /**
     * Get the piece character string.
     * @return piece character string
     */
    @Override
    public String toString() {
        return this == EMPTY ? "." :
                colour() == Colour.WHITE ?
                        type().toString().toUpperCase() :
                        type().toString();
    }
}
