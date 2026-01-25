package dev.pig.stockpig.chess;

/**
 * Castling rights are stored as a single byte bitmap data structure.
 * The 4 LSBs store whether each team/side castle is possible.
 * Provides functions and constants for encoding/decoding castling data.
 */
public final class Castling {

    // Rights
    public static final byte ALL          = 0b1111;
    public static final byte NONE         = 0;
    public static final byte W_QUEEN_SIDE = 1 << 0;
    public static final byte W_KING_SIDE  = 1 << 1;
    public static final byte B_QUEEN_SIDE = 1 << 2;
    public static final byte B_KING_SIDE  = 1 << 3;

    // Castle moves
    public static final int W_QUEEN_SIDE_MOVE = Move.castle(Square.E1, Square.C1);
    public static final int W_KING_SIDE_MOVE  = Move.castle(Square.E1, Square.G1);
    public static final int B_QUEEN_SIDE_MOVE = Move.castle(Square.E8, Square.C8);
    public static final int B_KING_SIDE_MOVE  = Move.castle(Square.E8, Square.G8);

    // Rook move bits
    public static final long W_QUEEN_SIDE_ROOK_MOVE = Bitboard.from(Square.A1, Square.D1);
    public static final long W_KING_SIDE_ROOK_MOVE  = Bitboard.from(Square.F1, Square.H1);
    public static final long B_QUEEN_SIDE_ROOK_MOVE = Bitboard.from(Square.A8, Square.D8);
    public static final long B_KING_SIDE_ROOK_MOVE  = Bitboard.from(Square.F8, Square.H8);

    // Empty squares
    public static final long W_QUEEN_SIDE_EMPTY_SQUARES = Bitboard.from(Square.B1, Square.C1, Square.D1);
    public static final long W_KING_SIDE_EMPTY_SQUARES  = Bitboard.from(Square.F1, Square.G1);
    public static final long B_QUEEN_SIDE_EMPTY_SQUARES = Bitboard.from(Square.B8, Square.C8, Square.D8);
    public static final long B_KING_SIDE_EMPTY_SQUARES  = Bitboard.from(Square.F8, Square.G8);

    // Check squares
    public static final long W_QUEEN_CHECK_SQUARES = Bitboard.from(Square.C1, Square.D1);
    public static final long W_KING_CHECK_SQUARES  = Bitboard.from(Square.F1, Square.G1);
    public static final long B_QUEEN_CHECK_SQUARES = Bitboard.from(Square.C8, Square.D8);
    public static final long B_KING_CHECK_SQUARES  = Bitboard.from(Square.F8, Square.G8);


    // ====================================================================================================
    //                                  Constant Accessors
    // ====================================================================================================

    /**
     * Get the queen side castle move for the colour.
     * @param c colour
     * @return castle move
     */
    public static int getQueenSideMove(final Colour c) {
        return c == Colour.WHITE ? W_QUEEN_SIDE_MOVE : B_QUEEN_SIDE_MOVE;
    }

    /**
     * Get the king side castle move for the colour.
     * @param c colour
     * @return castle move
     */
    public static int getKingSideMove(final Colour c) {
        return c == Colour.WHITE ? W_KING_SIDE_MOVE : B_KING_SIDE_MOVE;
    }

    /**
     * Get the rook move bits for a given castle move.
     * @param c colour
     * @param to to square
     * @return rook move bits
     */
    public static long getRookMoveBits(final Colour c, final Square to) {
        return c == Colour.WHITE ?
                to == Square.C1 ? W_QUEEN_SIDE_ROOK_MOVE : W_KING_SIDE_ROOK_MOVE :
                to == Square.C8 ? B_QUEEN_SIDE_ROOK_MOVE : B_KING_SIDE_ROOK_MOVE;
    }


    // ====================================================================================================
    //                                  Rights Modifiers
    // ====================================================================================================

    /**
     * Get the castling rights after a given move.
     * @param c colour
     * @param rights current castling rights
     * @param move move
     * @return updated castling rights
     */
    public static byte applyMove(final Colour c, byte rights, final int move) {
        if (c == Colour.WHITE) {
            if (Move.mover(move) == PieceType.KING) {
                rights &= ~(W_QUEEN_SIDE | W_KING_SIDE);
            } else if (Move.from(move) == Square.A1) {
                rights &= ~W_QUEEN_SIDE;
            } else if (Move.from(move) == Square.H1) {
                rights &= ~W_KING_SIDE;
            }

            if (Move.to(move) == Square.A8) {
                rights &= ~B_QUEEN_SIDE;
            } else if (Move.to(move) == Square.H8) {
                rights &= ~B_KING_SIDE;
            }
        } else {
            if (Move.mover(move) == PieceType.KING) {
                rights &= ~(B_QUEEN_SIDE | B_KING_SIDE);
            } else if (Move.from(move) == Square.A8) {
                rights &= ~B_QUEEN_SIDE;
            } else if (Move.from(move) == Square.H8) {
                rights &= ~B_KING_SIDE;
            }

            if (Move.to(move) == Square.A1) {
                rights &= ~W_QUEEN_SIDE;
            } else if (Move.to(move) == Square.H1) {
                rights &= ~W_KING_SIDE;
            }
        }

        return rights;
    }


    // ====================================================================================================
    //                                  Legality Checkers
    // ====================================================================================================

    /**
     * Get whether a queen side castle move is allowed given some current state.
     * @param c colour
     * @param rights castling rights
     * @param unoccupied unoccupied bitboard
     * @param attacked attacked bitboard
     * @return whether queen side castle is allowed
     */
    public static boolean isQueenSideAllowed(final Colour c, final byte rights, final long unoccupied, final long attacked) {
        return c == Colour.WHITE ?
                isCastleAllowed(rights, unoccupied, attacked, W_QUEEN_SIDE, W_QUEEN_SIDE_EMPTY_SQUARES, W_QUEEN_CHECK_SQUARES) :
                isCastleAllowed(rights, unoccupied, attacked, B_QUEEN_SIDE, B_QUEEN_SIDE_EMPTY_SQUARES, B_QUEEN_CHECK_SQUARES);
    }

    /**
     * Get whether a king side castle move is allowed given some current state.
     * @param c colour
     * @param rights castling rights
     * @param unoccupied unoccupied bitboard
     * @param attacked attacked bitboard
     * @return whether king side castle is allowed
     */
    public static boolean isKingSideAllowed(final Colour c, final byte rights, final long unoccupied, final long attacked) {
        return c == Colour.WHITE ?
                isCastleAllowed(rights, unoccupied, attacked, W_KING_SIDE, W_KING_SIDE_EMPTY_SQUARES, W_KING_CHECK_SQUARES) :
                isCastleAllowed(rights, unoccupied, attacked, B_KING_SIDE, B_KING_SIDE_EMPTY_SQUARES, B_KING_CHECK_SQUARES);
    }

    /**
     * Generic castle allowed calculator function.
     * @param rights castle rights
     * @param unoccupied unoccupied bitboard
     * @param attacked attacked bitboard
     * @param flag team side flag
     * @param emptySquares empty squares
     * @param checkSquares check squares
     * @return is castle allowed
     */
    private static boolean isCastleAllowed(final byte rights, final long unoccupied, final long attacked,
                                           final byte flag, final long emptySquares, final long checkSquares) {
        return ((rights & flag) != 0) &&
                (Bitboard.contains(unoccupied, emptySquares)) &&
                (Bitboard.disjoint(attacked, checkSquares));
    }


    // ====================================================================================================
    //                                  Utils
    // ====================================================================================================

    /**
     * Get the castling right from a string.
     * @param s castling rights string
     * @return castling rights
     */
    public static byte fromString(final String s) {
        return (byte) (
                        (s.contains("K") ? W_KING_SIDE  : 0) |
                        (s.contains("Q") ? W_QUEEN_SIDE : 0) |
                        (s.contains("k") ? B_KING_SIDE  : 0) |
                        (s.contains("q") ? B_QUEEN_SIDE : 0));
    }

    /**
     * Get a castling rights string from castling rights.
     * @param rights castling rights
     * @return castling right string
     */
    public static String toString(final byte rights) {
        return  rights == 0 ? "-" :
                ((rights & W_KING_SIDE)     == 0 ? "" : "K") +
                ((rights & W_QUEEN_SIDE)    == 0 ? "" : "Q") +
                ((rights & B_KING_SIDE)     == 0 ? "" : "k") +
                ((rights & B_QUEEN_SIDE)    == 0 ? "" : "q");
    }

    private Castling() {}
}
