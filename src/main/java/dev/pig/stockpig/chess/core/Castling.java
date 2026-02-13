package dev.pig.stockpig.chess.core;

import dev.pig.stockpig.chess.core.bitboard.Bitboard;
import dev.pig.stockpig.chess.core.bitboard.Square;

import java.util.Arrays;

/**
 * Castling rights are encoded as a single 8-bit byte (using only the 4 LSBs).
 * The 4 least-significant bits encode whether each colour/side may castle.
 * Provides functions and constants for encoding/decoding and querying castling rights.
 */
public final class Castling {

    // Rights
    public static final byte ALL          = 0b1111;
    public static final byte NONE         = 0;
    public static final byte W_QUEEN_SIDE = 1 << 0;
    public static final byte W_KING_SIDE  = 1 << 1;
    public static final byte B_QUEEN_SIDE = 1 << 2;
    public static final byte B_KING_SIDE  = 1 << 3;

    // Castle Moves
    public static final int W_QUEEN_SIDE_MOVE = Move.castle(Square.E1, Square.C1);
    public static final int W_KING_SIDE_MOVE  = Move.castle(Square.E1, Square.G1);
    public static final int B_QUEEN_SIDE_MOVE = Move.castle(Square.E8, Square.C8);
    public static final int B_KING_SIDE_MOVE  = Move.castle(Square.E8, Square.G8);

    // Rook Move Squares
    public static final byte W_QUEEN_SIDE_ROOK_FROM = Square.A1;
    public static final byte W_KING_SIDE_ROOK_FROM  = Square.H1;
    public static final byte B_QUEEN_SIDE_ROOK_FROM = Square.A8;
    public static final byte B_KING_SIDE_ROOK_FROM  = Square.H8;
    public static final byte W_QUEEN_SIDE_ROOK_TO   = Square.D1;
    public static final byte W_KING_SIDE_ROOK_TO    = Square.F1;
    public static final byte B_QUEEN_SIDE_ROOK_TO   = Square.D8;
    public static final byte B_KING_SIDE_ROOK_TO    = Square.F8;

    // Empty Squares
    public static final long W_QUEEN_SIDE_EMPTY_SQUARES = Bitboard.ofSquares(Square.B1, Square.C1, Square.D1);
    public static final long W_KING_SIDE_EMPTY_SQUARES  = Bitboard.ofSquares(Square.F1, Square.G1);
    public static final long B_QUEEN_SIDE_EMPTY_SQUARES = Bitboard.ofSquares(Square.B8, Square.C8, Square.D8);
    public static final long B_KING_SIDE_EMPTY_SQUARES  = Bitboard.ofSquares(Square.F8, Square.G8);

    // Check Squares
    public static final long W_QUEEN_CHECK_SQUARES = Bitboard.ofSquares(Square.C1, Square.D1);
    public static final long W_KING_CHECK_SQUARES  = Bitboard.ofSquares(Square.F1, Square.G1);
    public static final long B_QUEEN_CHECK_SQUARES = Bitboard.ofSquares(Square.C8, Square.D8);
    public static final long B_KING_CHECK_SQUARES  = Bitboard.ofSquares(Square.F8, Square.G8);

    private static final byte[] SQUARE_MASKS = new byte[64];
    static {
        Arrays.fill(SQUARE_MASKS, ALL);

        SQUARE_MASKS[Square.A1] = ~(W_QUEEN_SIDE);
        SQUARE_MASKS[Square.H1] = ~(W_KING_SIDE);
        SQUARE_MASKS[Square.E1] = ~(W_QUEEN_SIDE | W_KING_SIDE);

        SQUARE_MASKS[Square.A8] = ~(B_QUEEN_SIDE);
        SQUARE_MASKS[Square.H8] = ~(B_KING_SIDE);
        SQUARE_MASKS[Square.E8] = ~(B_QUEEN_SIDE | B_KING_SIDE);
    }


    // ====================================================================================================
    //                                  Constant Accessors
    // ====================================================================================================

    /**
     * Get the queen side castle move for the colour.
     * @param c colour
     * @return queen side castle move
     */
    public static int getQueenSideMove(final boolean c) {
        return c ? W_QUEEN_SIDE_MOVE : B_QUEEN_SIDE_MOVE;
    }

    /**
     * Get the king side castle move for the colour.
     * @param c colour
     * @return king side castle move
     */
    public static int getKingSideMove(final boolean c) {
        return c ? W_KING_SIDE_MOVE : B_KING_SIDE_MOVE;
    }

    /**
     * Get the rook from square for the given castle move.
     * @param c colour
     * @param to king to square
     * @return rook from square
     */
    static byte getRookFrom(final boolean c, final byte to) {
        return c ?
                to == Square.C1 ? W_QUEEN_SIDE_ROOK_FROM : W_KING_SIDE_ROOK_FROM :
                to == Square.C8 ? B_QUEEN_SIDE_ROOK_FROM : B_KING_SIDE_ROOK_FROM;
    }

    /**
     * Get the rook to square for the given castle move.
     * @param c colour
     * @param to king to square
     * @return rook to square
     */
    static byte getRookTo(final boolean c, final byte to) {
        return c ?
                to == Square.C1 ? W_QUEEN_SIDE_ROOK_TO : W_KING_SIDE_ROOK_TO :
                to == Square.C8 ? B_QUEEN_SIDE_ROOK_TO : B_KING_SIDE_ROOK_TO;
    }


    // ====================================================================================================
    //                                  Rights Modifiers
    // ====================================================================================================

    /**
     * Returns updated castling rights after applying the given move.
     * @param rights current castling rights
     * @param move move
     * @return updated castling rights
     */
    static byte update(byte rights, final int move) {
        return (byte) (rights & SQUARE_MASKS[Move.from(move)] & SQUARE_MASKS[Move.to(move)]);
    }


    // ====================================================================================================
    //                                  Legality Checkers
    // ====================================================================================================

    /**
     * Returns whether queen side castling is allowed under the given position state.
     * @param c colour
     * @param rights castling rights
     * @param unoccupied unoccupied bitboard
     * @param attacked attacked bitboard
     * @return whether queen side castle is allowed
     */
    static boolean isQueenSideAllowed(final boolean c, final byte rights, final long unoccupied, final long attacked) {
        return c ?
                isCastleAllowed(rights, unoccupied, attacked, W_QUEEN_SIDE, W_QUEEN_SIDE_EMPTY_SQUARES, W_QUEEN_CHECK_SQUARES) :
                isCastleAllowed(rights, unoccupied, attacked, B_QUEEN_SIDE, B_QUEEN_SIDE_EMPTY_SQUARES, B_QUEEN_CHECK_SQUARES);
    }

    /**
     * Returns whether king side castling is allowed under the given position state.
     * @param c colour
     * @param rights castling rights
     * @param unoccupied unoccupied bitboard
     * @param attacked attacked bitboard
     * @return whether king side castle is allowed
     */
    static boolean isKingSideAllowed(final boolean c, final byte rights, final long unoccupied, final long attacked) {
        return c ?
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


    private Castling() {}
}
