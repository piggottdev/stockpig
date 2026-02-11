package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Direction;

/**
 * Colour is an enum for each chess team colour.
 */
public enum Colour {
    WHITE, BLACK;


    /**
     * Get the colour for the boolean (true is WHITE, false is BLACK).
     * @param isWhite is white
     * @return colour
     */
    public static Colour of(final boolean isWhite) {
        return isWhite ? WHITE : BLACK;
    }

    /**
     * Get the other colour.
     * @return other colour
     */
    public Colour flip() {
        return this == WHITE ? BLACK : WHITE;
    }

    /**
     * Get the forward direction of the colour.
     * @return forward direction
     */
    public Direction forward() {
        return this == WHITE ? Direction.N : Direction.S;
    }

    /**
     * Get the backward direction of the colour.
     * @return backward direction
     */
    public Direction backward() {
        return this == WHITE ? Direction.S : Direction.N;
    }

    /**
     * Get the first pawn attack direction of the colour, in the diagonal axis.
     * @return diagonal pawn attack direction
     */
    public Direction pawnAttackDirection1() {
        return this == WHITE ? Direction.NE : Direction.SW;
    }

    /**
     * Get the second pawn attack direction of the colour, in the anti-diagonal axis.
     * @return anti-diagonal pawn attack direction
     */
    public Direction pawnAttackDirection2() {
        return this == WHITE ? Direction.NW : Direction.SE;
    }

    /**
     * Get the third forward rank of the colour, this is the rank one forward from the pawn
     * starting rank.
     * @return third rank
     */
    public long rank3() {
        return this == WHITE ? Bitboard.RANK_3 : Bitboard.RANK_6;
    }

    /**
     * Get the eighth forward rank of the colour, this is the pawn promotion rank.
     * @return eighth rank
     */
    public long rank8() {
        return this == WHITE ? Bitboard.RANK_8 : Bitboard.RANK_1;
    }
}
