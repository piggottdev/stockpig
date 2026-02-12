package dev.pig.stockpig.chess.core.bitboard;

import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

/**
 * Bitboard is a single long bitmap data structure encoding occupancy of a chess board.
 * Each bit in the long is a bit flag for whether a chess square is occupied.
 * We can use several of these to represent the whole state of a chess board.
 * Bitwise operators also provide an efficient way to carry out certain operations.
 * <p>
 * The least significant bit is indexed 0 and the most significant bit indexed 63,
 * providing the following mapping (bottom left square being A1).
 * <p>
 *          8       56 . . . . 63
 *                  . . . . . . .
 *          Ranks   . . . . . . .
 *                  . . . . . . .
 *          1       0 . . . . . 7
 * <p>
 *                  A   Files   H
 *
 */
public final class Bitboard {


    // ====================================================================================================
    //                                  Common Constants
    // ====================================================================================================

    // Constants
    public static final long EMPTY          = 0L;
    public static final long ALL            = -1L;
    public static final long BLACK_SQUARES  = 0xAA55AA55AA55AA55L;
    public static final long WHITE_SQUARES  = 0x55AA55AA55AA55AAL;

    // Files
    public static final long FILE_A = 0x101010101010101L << 0;
    public static final long FILE_B = 0x101010101010101L << 1;
    public static final long FILE_C = 0x101010101010101L << 2;
    public static final long FILE_D = 0x101010101010101L << 3;
    public static final long FILE_E = 0x101010101010101L << 4;
    public static final long FILE_F = 0x101010101010101L << 5;
    public static final long FILE_G = 0x101010101010101L << 6;
    public static final long FILE_H = 0x101010101010101L << 7;

    // Ranks
    public static final long RANK_1 = 0xFFL << (8 * 0);
    public static final long RANK_2 = 0xFFL << (8 * 1);
    public static final long RANK_3 = 0xFFL << (8 * 2);
    public static final long RANK_4 = 0xFFL << (8 * 3);
    public static final long RANK_5 = 0xFFL << (8 * 4);
    public static final long RANK_6 = 0xFFL << (8 * 5);
    public static final long RANK_7 = 0xFFL << (8 * 6);
    public static final long RANK_8 = 0xFFL << (8 * 7);

    // Wrap Bounds
    public static final long NOT_FILE_A     = ~(FILE_A);
    public static final long NOT_FILE_H     = ~(FILE_H);
    public static final long NOT_FILE_AB    = ~(FILE_A | FILE_B);
    public static final long NOT_FILE_GH    = ~(FILE_G | FILE_H);


    // ====================================================================================================
    //                                  Constructors and Builders
    // ====================================================================================================

    /**
     * Get a bitboard for a given square, should be a valid square.
     * @param square square
     * @return single occupancy bitboard
     */
    public static long ofSquare(final int square) {
        return 1L << square;
    }

    /**
     * Get a bitboard for a collection of squares, should be a valid squares.
     * @param squares squares
     * @return bitboard
     */
    public static long ofSquares(final int... squares) {
        long bb = 0L;
        for (final int square : squares) {
            bb |= ofSquare(square);
        }
        return bb;
    }


    // ====================================================================================================
    //                                  Set Operations
    // ====================================================================================================

    /**
     * Checks if a bitboard is empty.
     * @param bb bitboard
     * @return is bitboard empty
     */
    public static boolean isEmpty(final long bb) {
        return bb == EMPTY;
    }

    /**
     * Checks if two bitboards intersect, that is they share at least a single one bit.
     * @param bb1 bitboard
     * @param bb2 bitboard
     * @return do bitboards intersect
     */
    public static boolean intersects(final long bb1, final long bb2) {
        return (bb1 & bb2) != EMPTY;
    }

    /**
     * Checks if two bitboards are disjoint, that is they have no common one bits.
     * @param bb1 bitboard
     * @param bb2 bitboard
     * @return are bitboards disjoint
     */
    public static boolean disjoint(final long bb1, final long bb2) {
        return isEmpty(bb1 & bb2);
    }

    /**
     * Checks if a bitboard contains another bitboard, that is every one bit in bitboard is also
     * set in the area bitboard.
     * @param area area bitboard
     * @param bb bitboard
     * @return does area fully contain the bitboard
     */
    public static boolean contains(final long area, final long bb) {
        return (area & bb) == bb;
    }


    // ====================================================================================================
    //                                  Population / Bit Count
    // ====================================================================================================

    /**
     * Get whether the bitboard contains only a single one bit.
     * @param bb bitboard
     * @return is single occupancy bitboard
     */
    public static boolean isSingle(final long bb) {
        return Bitboard.isEmpty(bb ^ pop(bb));
    }

    /**
     * Get the population/bit count of the bitboard, that is the number of one bits.
     * @param bb bitboard
     * @return bit count
     */
    public static int count(final long bb) {
        return Long.bitCount(bb);
    }


    // ====================================================================================================
    //                                  Shifts
    // ====================================================================================================

    /**
     * Generalised bit shift allowing negative shifts. Positive shifts are left shifted and
     * negative shifts are right shifted.
     * @param bb bitboard
     * @param shift shift amount
     * @return shifted bitboard
     */
    public static long shift(final long bb, final int shift) {
        return shift >= 0 ? bb << shift : bb >>> -shift;
    }

    /**
     * Shift a bitboard in a direction, applies mask to prevent file wrap around.
     * @param bb bitboard
     * @param d shift direction
     * @return shifted bitboard
     */
    public static long shift(final long bb, final Direction d) {
        return shift(bb, d.offset()) & d.mask();
    }

    /**
     * Shift a bitboard in a direction into an area, applies mask to prevent file wrap around.
     * @param bb bitboard
     * @param d shift direction
     * @param area post shift mask
     * @return shifted bitboard
     */
    public static long shiftInto(final long bb, final Direction d, final long area) {
        return shift(bb, d) & area;
    }

    /**
     * Shift a bitboard in a reverse direction, does not apply a mask to prevent file wrap around (assumes this is undoing a shift).
     * @param bb bitboard
     * @param d shift direction
     * @return reverse shifted bitboard
     */
    public static long shiftRev(final long bb, final Direction d) {
        return shift(bb, -d.offset());
    }


    // ====================================================================================================
    //                                  Fills and Slides
    // ====================================================================================================

    /**
     * Fill a bitboard in a given direction, this duplicates one bits in the given direction up to the edge of the board.
     * @param bb bitboard
     * @param d fill direction
     * @return filled bitboard
     */
    public static long fill(long bb, final Direction d) {
        return fillInto(bb, d, ALL);
    }

    /**
     * Fill a bitboard in a given direction into an area, this duplicates one bits in the given direction within the area.
     * @param bb bitboard
     * @param d fill direction
     * @param area area bitboard
     * @return filled bitboard
     */
    public static long fillInto(long bb, final Direction d, long area) {
        area &= d.mask();

        bb |= area & shift(bb, d.offset());
        area &= shift(area, d.offset());

        bb |= area & shift(bb, d.offset() * 2);
        area &= shift(area, d.offset() * 2);

        bb |= area & shift(bb, d.offset() * 4);
        return bb;
    }

    /**
     * Fill a bitboard in a given direction into unoccupied spaces, then shift the result in the direction.
     * The result is a slide attack map for the pieces, including the first blocker encountered.
     * @param pieces pieces bitboard
     * @param unoccupied unoccupied bitboard
     * @param d direction
     * @return pieces attack map
     */
    public static long slide(final long pieces, final long unoccupied, final Direction d) {
        return shift(fillInto(pieces, d, unoccupied), d);
    }

    /**
     * Slide a bitboard in all orthogonal directions (like a rook).
     * @param pieces pieces bitboard
     * @param unoccupied unoccupied bitboard
     * @return pieces attack map
     */
    public static long slideOrthogonal(final long pieces, final long unoccupied) {
        return  slide(pieces, unoccupied, Direction.N) |
                slide(pieces, unoccupied, Direction.S) |
                slide(pieces, unoccupied, Direction.E) |
                slide(pieces, unoccupied, Direction.W);
    }

    /**
     * Slide a bitboard in all diagonal directions (like a bishop).
     * @param pieces pieces bitboard
     * @param unoccupied unoccupied bitboard
     * @return pieces attack map
     */
    public static long slideDiagonal(final long pieces, final long unoccupied) {
        return  slide(pieces, unoccupied, Direction.NE) |
                slide(pieces, unoccupied, Direction.SW) |
                slide(pieces, unoccupied, Direction.SE) |
                slide(pieces, unoccupied, Direction.NW);
    }

    /**
     * Slide a bitboard in all directions (like a queen).
     * @param pieces pieces bitboard
     * @param unoccupied unoccupied bitboard
     * @return pieces attack map
     */
    public static long slideAll(final long pieces, final long unoccupied) {
        return slideOrthogonal(pieces, unoccupied) | slideDiagonal(pieces, unoccupied);
    }


    // ====================================================================================================
    //                                  Mirrors and Flips
    // ====================================================================================================

    /**
     * Flip the bitboard vertically, that is mirror horizontally in the x-axis.
     * @param bb bitboard
     * @return flipped bitboard
     */
    public static long mirrorx(final long bb) {
        return Long.reverseBytes(bb);
    }


    // ====================================================================================================
    //                                  LSB Loops / Consumers
    // ====================================================================================================

    /**
     * Return the lowest bit of a bitboard as a single occupancy bitboard.
     * Can be used to iterate through individual bits of a bitboard.
     * @param bb bitboard
     * @return single occupancy bitboard
     */
    public static long pop(final long bb) {
        return Long.lowestOneBit(bb);
    }

    /**
     * Call the consumer with each single bit within a bitboard (LSB...MSB).
     * @param bb bitboard
     * @param c single occupancy bitboard consumer
     */
    public static void forEach(long bb, final LongConsumer c) {
        while (bb != 0L) {
            final long lsb = pop(bb);
            c.accept(lsb);
            bb ^= lsb;
        }
    }

    /**
     * Call the consumer with each square within a bitboard (LSB...MSB).
     * @param bb bitboard
     * @param c square consumer
     */
    public static void forEachSquare(final long bb, final IntConsumer c) {
        forEach(bb, bit -> c.accept(Square.ofBitboard(bit)));
    }


    // ====================================================================================================
    //                                  String Utils
    // ====================================================================================================

    /**
     * Create a pretty 0/1 debug string of a bitboard.
     * @param bb bitboard
     * @return pretty 0/1 debug string
     */
    public static String toString(final long bb) {
        final StringBuilder sb = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                final long bit = ofSquare(rank*8+file);
                sb.append(isEmpty(bit & bb) ? "0 " : "1 ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    private Bitboard() {}
}
