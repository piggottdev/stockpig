package dev.pig.chess;

import java.util.function.Consumer;

/**
 * A bitboard is a bitmap data structure in the form of a single long.
 * Each bit in the long represents the presence/absence on a given chess square.
 * We can use several of these to represent the whole state of a chess board.
 * <p>
 * The 64 bits are set out in an 8x8 square.
 * The least significant bit is indexed 0 and the most significant bit indexed 63:
 * .....63
 * .......
 * .......
 * 0 ... 7
 */
public class Bitboard {

    // Bitboard constants
    public static final long EMPTY = 0L;
    public static final long ALL = 0xFFFFFFFFFFFFFFFFL;
    public static final long[] RANKS = initRanks();
    public static final long[] FILES = initFiles();
    public static final long BLACK_SQUARES = initBlackSquares();
    public static final long[] INDEX = initPositions();

    // Direction indexes
    public static final int NORTH = 0;
    public static final int NORTH_EAST = 1;
    public static final int EAST = 2;
    public static final int SOUTH_EAST = 3;
    public static final int SOUTH = 4;
    public static final int SOUTH_WEST = 5;
    public static final int WEST = 6;
    public static final int NORTH_WEST = 7;
    public static final int NORTH_NORTH_EAST = 8;
    public static final int NORTH_EAST_EAST = 9;
    public static final int SOUTH_EAST_EAST = 10;
    public static final int SOUTH_SOUTH_EAST = 11;
    public static final int SOUTH_SOUTH_WEST = 12;
    public static final int SOUTH_WEST_WEST = 13;
    public static final int NORTH_WEST_WEST = 14;
    public static final int NORTH_NORTH_WEST = 15;

    // Direction index groups
    public static final int[] CARDINAL = {NORTH, SOUTH, EAST, WEST};
    public static final int[] HORIZONTAL = {EAST, WEST};
    public static final int[] VERTICAL = {NORTH, SOUTH};
    public static final int[] DIAGONAL = {NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST};
    public static final int[] DIAGONAL_POSITIVE = {NORTH_EAST, SOUTH_WEST};
    public static final int[] DIAGONAL_NEGATIVE = {NORTH_WEST, SOUTH_EAST};
    public static final int[] EVERY_DIRECTION = {NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
    public static final int[] L_SHAPES = {NORTH_NORTH_EAST, NORTH_EAST_EAST, SOUTH_EAST_EAST, SOUTH_SOUTH_EAST, SOUTH_SOUTH_WEST, SOUTH_WEST_WEST, NORTH_WEST_WEST, NORTH_NORTH_WEST};
    public static final int[] WHITE_PAWN_ATTACK_DIRECTIONS = {NORTH_EAST, NORTH_WEST};
    public static final int[] BLACK_PAWN_ATTACK_DIRECTIONS = {SOUTH_WEST, SOUTH_EAST};

    // Constants for directions, index of the array being the direction index above
    private static final int[] DIRECTIONS = new int[] {8, 9, 1, -7, -8, -9, -1, 7, 17, 10, -6, -15, -17, -10, 6, 15};
    private static final long[] BOUNDS = initBounds();
    private static final long[] INVERSE_BOUNDS = initInverseBounds();
    private static final int[][] FILL_SHIFTS = initFillShifts();
    private static final long[] FILL_AREAS = initFillAreas();

    private Bitboard() {}

    /**
     * Get the square index of the lowest one bit of a bitboard.
     *
     * @param bitboard bitboard
     * @return square index
     */
    public static int toIndex(final long bitboard) {
        return Long.numberOfTrailingZeros(bitboard);
    }

    /**
     * Call the consumer with each single bit in the bitboard.
     *
     * @param bitboard bitboard
     * @param consumer bitboard consumer
     */
    public static void forEachBit(long bitboard, final Consumer<Long> consumer) {
        while (bitboard != 0L) {
            final long bit = Long.lowestOneBit(bitboard);
            consumer.accept(bit);
            bitboard ^= bit;
        }
    }

    /**
     * Check to see if two bitboards have at least one common space.
     * 
     * @param bitboard1 bitboard
     * @param bitboard2 bitboard
     * @return whether the bitboards intersect
     */
    public static boolean intersects(final long bitboard1, final long bitboard2) {
        return ((bitboard1 & bitboard2) != 0);
    }

    /**
     * Check to see if an area fully contains a bitboard.
     * Could also be named 'isSubset'.
     *
     * @param area area bitboard
     * @param bits a bitboard to check if contained in the area
     * @return whether the area fully contains the given bitboard
     */
    public static boolean contains(final long area, final long bits) {
        return (area & bits) == bits;
    }

    /**
     * Perform fill algorithm in given direction within given area.
     *
     * @param bits bitboard to fill
     * @param direction direction to fill
     * @param area area that is allowed to be filled
     * @return filled bitboard
     */
    public static long fill(long bits, final int direction, long area) {

        area &= FILL_AREAS[direction];

        bits |= area & (shift(bits, FILL_SHIFTS[direction][0]));
        area &= (shift(area, FILL_SHIFTS[direction][0]));

        bits |= area & (shift(bits, FILL_SHIFTS[direction][1]));
        area &= (shift(area, FILL_SHIFTS[direction][1]));

        bits |= area & (shift(bits, FILL_SHIFTS[direction][2]));

        return bits;
    }

    /**
     * Bit shift by a bitboard direction constant.
     *
     * @param bitboard bitboard
     * @param direction direction index
     * @return shifted bitboard
     */
    public static long directionalShift(final long bitboard, final int direction) {
        return shift(bitboard, DIRECTIONS[direction]);
    }

    /**
     * Bit shift by the opposite of a bitboard direction constant.
     *
     * @param bitboard bitboard
     * @param direction direction index
     * @return shifted bitboard
     */
    public static long oppositeDirectionalShift(final long bitboard, final int direction) {
        return shift(bitboard, -DIRECTIONS[direction]);
    }

    /**
     * Bit shift by a bitboard direction constant.
     * Then AND the result with a given bitboard mask.
     *
     * @param bitboard bitboard
     * @param direction direction index
     * @param spaceMask mask to AND with the shifted bitboard
     * @return shifted bitboard
     */
    public static long directionalShiftWithinArea(final long bitboard, final int direction, final long spaceMask) {
        return directionalShift(bitboard, direction) & spaceMask;
    }

    /**
     * Bit shift by a bitboard direction constant.
     * AND the bitboard with a given inverse bound for the given direction.
     * Then shift the bitboard in that direction.
     * This prevents chess pieces wrapping around the board.
     * (e.g. bishop moving diagonally through the side of the board).
     *
     * @param bit bitboard
     * @param direction direction index
     * @return shifted bitboard
     */
    public static long directionalShiftBounded(final long bit, final int direction) {
        return directionalShift(bit & INVERSE_BOUNDS[direction], direction);
    }

    /**
     * Bit shift by a bitboard direction constant.
     * AND the bitboard with a given inverse bound for the given direction.
     * Then shift the bits in that direction.
     * Then AND the result with a given bitboard mask.
     *
     * @param bit bitboard
     * @param direction direction index
     * @param spaceMask mask to AND with shifted bitboard
     * @return shifted bitboard
     */
    public static long directionalShiftBoundedWithinArea(final long bit, final int direction, final long spaceMask) {
        return directionalShiftBounded(bit, direction) & spaceMask;
    }

    /**
     * Generalised bit shift, allows negatives.
     *
     * @param bitboard bitboard
     * @param shift amount to shift by
     * @return shifted bitboard
     */
    public static long shift(final long bitboard, final int shift) {
        return shift < 0 ? bitboard >>> -shift : bitboard << shift;
    }

    /**
     * Flip a bitboard vertically.
     *
     * @param bitboard bitboard
     * @return flipped bitboard
     */
    public static long flipVertical(final long bitboard) {
        return Long.reverseBytes(bitboard);
    }

    /**
     * Returns a debug string.
     *
     * @param bitboard bitboard
     * @return debug string
     */
    public static String debugString(final long bitboard) {
        return buildBoardString(bitboard);
    }

    // -- Constant Initialisation --

    private static long[] initRanks() {
        final long[] ranks = new long[8];
        long bit = 255L;
        for (int i = 0; i < 8; i++) {
            ranks[i] = bit;
            bit <<= 8;
        }
        return ranks;
    }

    private static long[] initFiles() {
        final long[] files = new long[8];
        long bit = 1L;
        for (int i = 0; i < 7; i ++) {
            bit <<= 8;
            bit |= 1L;
        }
        for (int i = 0; i < 8; i++) {
            files[i] = bit;
            bit <<= 1;
        }
        return files;
    }

    private static long initBlackSquares() {
        long squares = 0b1010101001010101;
        for (int i = 0 ; i < 4; i++) squares |= (squares << 16);
        return squares;
    }

    private static long[] initPositions() {
        final long[] positions = new long[64];
        long bit = 1L;
        for (int i = 0; i < 64; i ++) {
            positions[i] = bit;
            bit <<= 1;
        }
        return positions;
    }

    private static long[] initBounds() {
        final long[] bounds = new long[16];
        bounds[NORTH] = RANKS[7];
        bounds[NORTH_EAST] = RANKS[7] | FILES[7];
        bounds[EAST] = FILES[7];
        bounds[SOUTH_EAST] = RANKS[0] | FILES[7];
        bounds[SOUTH] = RANKS[0];
        bounds[SOUTH_WEST] = RANKS[0] | FILES[0];
        bounds[WEST] = FILES[0];
        bounds[NORTH_WEST] = RANKS[7] | FILES[0];
        bounds[NORTH_NORTH_EAST] = RANKS[7] | RANKS[6] | FILES[7];
        bounds[NORTH_EAST_EAST] = RANKS[7] | FILES[7] | FILES[6];
        bounds[SOUTH_EAST_EAST] = RANKS[0] | FILES[7] | FILES[6];
        bounds[SOUTH_SOUTH_EAST] = RANKS[0] | RANKS[1] | FILES[7];
        bounds[SOUTH_SOUTH_WEST] = RANKS[0] | RANKS[1] | FILES[0];
        bounds[SOUTH_WEST_WEST] = RANKS[0] | FILES[0] | FILES[1];
        bounds[NORTH_WEST_WEST] = RANKS[7] | FILES[0] | FILES[1];
        bounds[NORTH_NORTH_WEST] = RANKS[7] | RANKS[6] | FILES[0];
        return bounds;
    }

    private static long[] initInverseBounds() {
        final long[] inverseBounds = new long[16];
        for (int i = 0; i < 16; i++) {
            inverseBounds[i] = ~BOUNDS[i];
        }
        return inverseBounds;
    }

    private static int[][] initFillShifts() {
        final int[][] fillShifts = new int[8][3];

        for (int direction = NORTH; direction < NORTH_NORTH_EAST; direction++) {
            fillShifts[direction][0] = DIRECTIONS[direction];
            fillShifts[direction][1] = DIRECTIONS[direction] * 2;
            fillShifts[direction][2] = DIRECTIONS[direction] * 4;
        }

        return fillShifts;
    }

    private static long[] initFillAreas() {
        final long[] fillAreas = new long[8];

        fillAreas[NORTH] = ALL;
        fillAreas[NORTH_EAST] = INVERSE_BOUNDS[WEST];
        fillAreas[EAST] = INVERSE_BOUNDS[WEST];
        fillAreas[SOUTH_EAST] = INVERSE_BOUNDS[WEST];
        fillAreas[SOUTH] = ALL;
        fillAreas[SOUTH_WEST] = INVERSE_BOUNDS[EAST];
        fillAreas[WEST] = INVERSE_BOUNDS[EAST];
        fillAreas[NORTH_WEST] = INVERSE_BOUNDS[EAST];

        return fillAreas;
    }

    // -- Debug String --

    private static final String LINE_ROW = "+---+---+---+---+---+---+---+---+\n";
    private static final String PADDING_ROW = "|   |   |   |   |   |   |   |   |\n";
    private static final String ROW_START = "| ";
    private static final String ROW_END = " |\n";
    private static final String CELL_BORDER = " | ";

    private static String buildBoardString(final long bitboard) {
        final StringBuilder boardString = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            boardString.append(LINE_ROW);
            boardString.append(PADDING_ROW);
            boardString.append(ROW_START);

            long bit = 1L;
            bit <<= ((i * 8));

            for (int j = 0; j < 7; j++) {
                boardString.append(((bit & bitboard) != 0) ? "x" : " ");
                boardString.append(CELL_BORDER);
                bit <<= 1;
            }
            boardString.append(((bit & bitboard) != 0) ? "x" : " ");

            boardString.append(ROW_END);
            boardString.append(PADDING_ROW);
        }
        boardString.append(LINE_ROW);

        return boardString.toString();
    }
}
