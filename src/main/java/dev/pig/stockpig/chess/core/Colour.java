package dev.pig.stockpig.chess.core;

import dev.pig.stockpig.chess.core.bitboard.Bitboard;
import dev.pig.stockpig.chess.core.bitboard.Direction;

/**
 * Colour provides constants and functions for working with team colour booleans.
 */
public final class Colour {

    public static final boolean
            WHITE = true, BLACK = false;


    /**
     * Get the other colour.
     * @param c colour
     * @return other colour
     */
    public static boolean flip(final boolean c) {
        return !c;
    }

    /**
     * Get the forward direction of the colour.
     * @param c colour
     * @return forward direction
     */
    public static Direction forward(final boolean c) {
        return c ? Direction.N : Direction.S;
    }

    /**
     * Get the backward direction of the colour.
     * @param c colour
     * @return backward direction
     */
    public static Direction backward(final boolean c) {
        return c ? Direction.S : Direction.N;
    }

    /**
     * Get the diagonal pawn attacking direction of the colour.
     * @param c colour
     * @return diagonal attack direction
     */
    public static Direction pawnAttackDirection1(final boolean c) {
        return c ? Direction.NE : Direction.SW;
    }

    /**
     * Get the anti-diagonal pawn attacking direction of the colour.
     * @param c colour
     * @return anti-diagonal attack direction
     */
    public static Direction pawnAttackDirection2(final boolean c) {
        return c ? Direction.NW : Direction.SE;
    }

    /**
     * Get the third rank forward bitboard for the colour.
     * @param c colour
     * @return rank 3 bitboard
     */
    public static long rank3(final boolean c) {
        return c ? Bitboard.RANK_3 : Bitboard.RANK_6;
    }

    /**
     * Get the eighth rank forward bitboard for the colour.
     * @param c colour
     * @return rank 8 bitboard
     */
    public static long rank8(final boolean c) {
        return c ? Bitboard.RANK_8 : Bitboard.RANK_1;
    }


    private Colour() {}
}
