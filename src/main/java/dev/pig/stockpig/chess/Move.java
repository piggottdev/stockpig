package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Square;

/**
 * Moves are encoded as single integer bitmaps. Set bit ranges encode properties
 * of the move, including some redundant move information that could be inferred
 * from the board state.
 * <p>
 * Bit ranges:
 * - Bits 0-5:      From square (0...63)
 * - Bits 6-11:     To square (0...63)
 * - Bits 12-14:    Moving piece type (0...6)
 * - Bits 15-17:    Captured piece type (0...6)
 * - Bits 18-20:    Promoted piece type (0...6)
 * - Bit 21:        Double pawn push flag
 * - Bit 22:        Castle flag
 * - Bit 23:        En passant flag
 */
public final class Move {

    // Masks
    private static final int SQUARE_MASK  = 0b111111;
    private static final int PIECE_MASK   = 0b111;

    // Flag masks
    private static final int DOUBLE_PUSH_MASK   = 1 << 21;
    private static final int CASTLE_MASK        = 1 << 22;
    private static final int EN_PASSANT_MASK    = 1 << 23;

    // Shifts
    private static final int FROM_SHIFT       = 0;
    private static final int TO_SHIFT         = 6;
    private static final int MOVER_SHIFT      = 12;
    private static final int CAPTURE_SHIFT    = 15;
    private static final int PROMOTE_SHIFT    = 18;

    // Helper mask
    private static final int CAPTURE_MASK = SQUARE_MASK << CAPTURE_SHIFT;


    // ====================================================================================================
    //                                  Constructors and Builders
    // ====================================================================================================

    // TODO: Candidate optimisation: Test pre-computed and cached common moves

    /**
     * Create a basic move.
     * @param from from square
     * @param to to square
     * @param mover moving piece type
     * @return move
     */
    public static int basic(final Square from, final Square to, final PieceType mover) {
        return from.ordinal() | (to.ordinal() << TO_SHIFT) | (mover.ordinal() << MOVER_SHIFT);
    }

    /**
     * Add a captured piece to the move.
     * @param move move
     * @param capture captured piece type
     * @return capture move
     */
    public static int addCapture(final int move, final PieceType capture) {
        return move | (capture.ordinal() << CAPTURE_SHIFT);
    }

    /**
     * Add a promoted piece to the move.
     * @param move move
     * @param promote promoted piece type
     * @return promotion move
     */
    public static int addPromotion(final int move, final PieceType promote) {
        return move | (promote.ordinal() << PROMOTE_SHIFT);
    }

    /**
     * Create a capture move.
     * @param from from square
     * @param to to square
     * @param mover moving piece type
     * @param capture captured piece type
     * @return move
     */
    public static int capture(final Square from, final Square to, final PieceType mover, final PieceType capture) {
        return addCapture(basic(from, to, mover), capture);
    }

    /**
     * Create a double pawn push move.
     * @param from from square
     * @param to to square
     * @return move
     */
    public static int doublePush(final Square from, final Square to) {
        return basic(from, to, PieceType.PAWN) | DOUBLE_PUSH_MASK;
    }

    /**
     * Create a castle move.
     * @param from from square
     * @param to to square
     * @return move
     */
    public static int castle(final Square from, final Square to) {
        return basic(from, to, PieceType.KING) | CASTLE_MASK;
    }

    /**
     * Create an en passant move.
     * @param from from square
     * @param to to square.
     * @return move
     */
    public static int enPassant(final Square from, final Square to) {
        return capture(from, to, PieceType.PAWN, PieceType.PAWN) | EN_PASSANT_MASK;
    }


    // ====================================================================================================
    //                                  Accessors
    // ====================================================================================================

    /**
     * Get the from square of the move.
     * @param move move
     * @return from square
     */
    public static Square from(final int move) {
        return Square.of(move & SQUARE_MASK);
    }

    /**
     * Get the to square of the move.
     * @param move move
     * @return to square
     */
    public static Square to(final int move) {
        return Square.of((move >>> TO_SHIFT) & SQUARE_MASK);
    }

    /**
     * Get the moving piece type of the move.
     * @param move move
     * @return moving piece type
     */
    public static PieceType mover(final int move) {
        return PieceType.of((move >>> MOVER_SHIFT) & PIECE_MASK);
    }

    /**
     * Get the captured piece type of the move.
     * @param move move
     * @return captured piece type
     */
    public static PieceType capture(final int move) {
        return PieceType.of((move >>> CAPTURE_SHIFT) & PIECE_MASK);
    }

    /**
     * Get the promoted piece type of the move.
     * @param move move
     * @return promoted piece type
     */
    public static PieceType promote(final int move) {
        return PieceType.of((move >>> PROMOTE_SHIFT) & PIECE_MASK);
    }

    /**
     * Get whether the move is a double pawn push move.
     * @param move move
     * @return is double pawn push move
     */
    public static boolean isDoublePush(final int move) {
        return (move & DOUBLE_PUSH_MASK) != 0;
    }

    /**
     * Get whether the move is a castle move.
     * @param move move
     * @return is castle move
     */
    public static boolean isCastle(final int move) {
        return (move & CASTLE_MASK) != 0;
    }

    /**
     * Get whether the move is an en passant move.
     * @param move move
     * @return is en passant move
     */
    public static boolean isEnPassant(final int move) {
        return (move & EN_PASSANT_MASK) != 0;
    }

    /**
     * Get whether the move is a capture move.
     * @param move move
     * @return is capture move
     */
    public static boolean isCapture(final int move) {
        return (move & CAPTURE_MASK) != 0;
    }


    // ====================================================================================================
    //                                  Utils
    // ====================================================================================================

    /**
     * Create a string representation of the move, this is the algebra notation of the origin
     * square followed be the destination square, then the promoted piece type if any.
     * @param move move
     * @return algebra notation move
     */
    public static String toString(final int move) {
        return from(move).toString() + to(move).toString() + promote(move).toString();
    }

    private Move() {}
}
