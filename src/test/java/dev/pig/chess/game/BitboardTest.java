package dev.pig.chess.game;

import dev.pig.chess.Bitboard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitboardTest {

    @Test
    void intersects() {
        assertTrue(Bitboard.intersects(1L, 1L));
        assertTrue(Bitboard.intersects(2L, 2L));
        assertTrue(Bitboard.intersects(1L, 3L));
        assertTrue(Bitboard.intersects(8L, 10L));
        assertTrue(Bitboard.intersects(Bitboard.RANKS[0], Bitboard.FILES[0]));

        assertFalse(Bitboard.intersects(1L, 2L));
        assertFalse(Bitboard.intersects(3L, 4L));
        assertFalse(Bitboard.intersects(Bitboard.RANKS[0], Bitboard.RANKS[1]));
    }

    @Test
    void contains() {
        assertTrue(Bitboard.contains(Bitboard.RANKS[0], Bitboard.RANKS[0]));
        assertTrue(Bitboard.contains(Bitboard.RANKS[0], Bitboard.INDEX[1]));
        assertTrue(Bitboard.contains(Bitboard.RANKS[0], Bitboard.INDEX[5]));
        assertTrue(Bitboard.contains(Bitboard.RANKS[0], Bitboard.INDEX[5] | Bitboard.INDEX[6]));
        assertTrue(Bitboard.contains(Bitboard.ALL, Bitboard.BLACK_SQUARES));
        assertTrue(Bitboard.contains(Bitboard.ALL, ~Bitboard.BLACK_SQUARES));
        assertTrue(Bitboard.contains(Bitboard.ALL, Bitboard.EMPTY));
        assertTrue(Bitboard.contains(Bitboard.INDEX[1], Bitboard.INDEX[1]));

        assertFalse(Bitboard.contains(Bitboard.INDEX[1], Bitboard.INDEX[1] | Bitboard.INDEX[2]));
        assertFalse(Bitboard.contains(Bitboard.RANKS[2], Bitboard.FILES[2]));
        assertFalse(Bitboard.contains(Bitboard.BLACK_SQUARES, Bitboard.ALL));
        assertFalse(Bitboard.contains(Bitboard.BLACK_SQUARES, Bitboard.ALL));
    }

    @Test
    void fill() {
        assertEquals(Bitboard.FILES[0], Bitboard.fill(Bitboard.INDEX[0], Bitboard.NORTH, Bitboard.ALL));
        assertEquals(Bitboard.RANKS[0], Bitboard.fill(Bitboard.INDEX[0], Bitboard.EAST, Bitboard.ALL));
        assertEquals(Bitboard.FILES[7], Bitboard.fill(Bitboard.INDEX[63], Bitboard.SOUTH, Bitboard.ALL));
        assertEquals(Bitboard.RANKS[7], Bitboard.fill(Bitboard.INDEX[63], Bitboard.WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[14] | Bitboard.INDEX[23], Bitboard.fill(Bitboard.INDEX[14], Bitboard.NORTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[7] | Bitboard.INDEX[14], Bitboard.fill(Bitboard.INDEX[14], Bitboard.SOUTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[6] | Bitboard.INDEX[13] | Bitboard.INDEX[20] | Bitboard.INDEX[27], Bitboard.fill(Bitboard.INDEX[6], Bitboard.NORTH_WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2] | Bitboard.RANKS[3]));
        assertEquals(Bitboard.INDEX[63] | Bitboard.INDEX[54], Bitboard.fill(Bitboard.INDEX[63], Bitboard.SOUTH_WEST, Bitboard.INDEX[54]));
        assertEquals(Bitboard.ALL, Bitboard.fill(Bitboard.FILES[0], Bitboard.EAST, Bitboard.ALL));
        assertEquals(Bitboard.FILES[0], Bitboard.fill(Bitboard.FILES[0], Bitboard.WEST, Bitboard.ALL));
    }

    @Test
    void oppositeDirectionalShift() {
        assertEquals(Bitboard.INDEX[11], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.NORTH));
        assertEquals(Bitboard.INDEX[18], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.EAST));
        assertEquals(Bitboard.INDEX[27], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.SOUTH));
        assertEquals(Bitboard.INDEX[20], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.WEST));
        assertEquals(Bitboard.INDEX[10], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.NORTH_EAST));
        assertEquals(Bitboard.INDEX[26], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.SOUTH_EAST));
        assertEquals(Bitboard.INDEX[28], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.SOUTH_WEST));
        assertEquals(Bitboard.INDEX[12], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.NORTH_WEST));
        assertEquals(Bitboard.INDEX[2], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.NORTH_NORTH_EAST));
        assertEquals(Bitboard.INDEX[9], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.NORTH_EAST_EAST));
        assertEquals(Bitboard.INDEX[25], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.SOUTH_EAST_EAST));
        assertEquals(Bitboard.INDEX[34], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.SOUTH_SOUTH_EAST));
        assertEquals(Bitboard.INDEX[36], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.SOUTH_SOUTH_WEST));
        assertEquals(Bitboard.INDEX[29], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.SOUTH_WEST_WEST));
        assertEquals(Bitboard.INDEX[13], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.NORTH_WEST_WEST));
        assertEquals(Bitboard.INDEX[4], Bitboard.oppositeDirectionalShift(Bitboard.INDEX[19], Bitboard.NORTH_NORTH_WEST));
    }

    @Test
    void directionalShiftWithinArea() {
        assertEquals(Bitboard.INDEX[27], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[20], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[11], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[18], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[28], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[12], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[10], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[26], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[36], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_NORTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[29], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_EAST_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[13], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_EAST_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[4], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_SOUTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[2], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_SOUTH_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[9], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_WEST_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[25], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_WEST_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[34], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_NORTH_WEST, Bitboard.ALL));

        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[20], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.EAST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[11], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[18], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_EAST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[12], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_EAST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[10], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_NORTH_EAST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_EAST_EAST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[13], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_EAST_EAST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[4], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_SOUTH_EAST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[2], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_SOUTH_WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.INDEX[9], Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.SOUTH_WEST_WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_WEST_WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftWithinArea(Bitboard.INDEX[19], Bitboard.NORTH_NORTH_WEST, Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[2]));
    }

    @Test
    void directionalShiftBounded() {
        assertEquals(Bitboard.INDEX[23], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.NORTH));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.EAST));
        assertEquals(Bitboard.INDEX[7], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.SOUTH));
        assertEquals(Bitboard.INDEX[14], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.WEST));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.NORTH_EAST));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.SOUTH_EAST));
        assertEquals(Bitboard.INDEX[6], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.SOUTH_WEST));
        assertEquals(Bitboard.INDEX[22], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.NORTH_WEST));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.NORTH_NORTH_EAST));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.NORTH_EAST_EAST));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.SOUTH_EAST_EAST));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.SOUTH_SOUTH_EAST));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.SOUTH_SOUTH_WEST));
        assertEquals(Bitboard.INDEX[5], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.SOUTH_WEST_WEST));
        assertEquals(Bitboard.INDEX[21], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.NORTH_WEST_WEST));
        assertEquals(Bitboard.INDEX[30], Bitboard.directionalShiftBounded(Bitboard.INDEX[15], Bitboard.NORTH_NORTH_WEST));
    }

    @Test
    void directionalShiftBoundedWithinArea() {
        assertEquals(Bitboard.INDEX[23], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[7], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[14], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.WEST, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[6], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[22], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_WEST, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_NORTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_EAST_EAST, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_EAST_EAST, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_SOUTH_EAST, Bitboard.ALL));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_SOUTH_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[5], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_WEST_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[21], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_WEST_WEST, Bitboard.ALL));
        assertEquals(Bitboard.INDEX[30], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_NORTH_WEST, Bitboard.ALL));

        assertEquals(Bitboard.INDEX[23], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.EAST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.INDEX[7], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.INDEX[14], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.WEST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_EAST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_EAST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.INDEX[6], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_WEST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.INDEX[22], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_WEST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_NORTH_EAST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_EAST_EAST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_EAST_EAST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_SOUTH_EAST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_SOUTH_WEST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.SOUTH_WEST_WEST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.EMPTY, Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_WEST_WEST, Bitboard.FILES[6] | Bitboard.FILES[7]));
        assertEquals(Bitboard.INDEX[30], Bitboard.directionalShiftBoundedWithinArea(Bitboard.INDEX[15], Bitboard.NORTH_NORTH_WEST, Bitboard.FILES[6] | Bitboard.FILES[7]));
    }

    @Test
    void debugString() {
        assertEquals(
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   | x |   | x |   | x |   | x |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| x |   | x |   | x |   | x |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   | x |   | x |   | x |   | x |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| x |   | x |   | x |   | x |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   | x |   | x |   | x |   | x |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| x |   | x |   | x |   | x |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   | x |   | x |   | x |   | x |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| x |   | x |   | x |   | x |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n", Bitboard.debugString(Bitboard.BLACK_SQUARES));
    }

}