package dev.pig.stockpig.chess.bitboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bitboard tests the majority of bitboard functions used within the engine.
 * These tests inherently test the enums within the bitboard package.
 */
public final class BitboardTest {

    @Test
    public void isEmpty() {
        assertTrue(Bitboard.isEmpty(Bitboard.EMPTY));

        assertFalse(Bitboard.isEmpty(Bitboard.ofSquare(Square.H6)));
        assertFalse(Bitboard.isEmpty(Bitboard.BLACK_SQUARES));
        assertFalse(Bitboard.isEmpty(Bitboard.ALL));
    }

    @Test
    public void intersects() {
        assertFalse(Bitboard.intersects(Bitboard.EMPTY,       Bitboard.EMPTY));
        assertFalse(Bitboard.intersects(Bitboard.EMPTY,       Bitboard.ofSquare(Square.B2)));
        assertFalse(Bitboard.intersects(Bitboard.EMPTY,       Bitboard.ALL));
        assertFalse(Bitboard.intersects(Bitboard.ofSquare(Square.B1), Bitboard.ofSquare(Square.B2)));
        assertFalse(Bitboard.intersects(Bitboard.RANK_1,   Bitboard.RANK_2));

        assertTrue(Bitboard.intersects(Bitboard.ofSquare(Square.A1), Bitboard.ofSquare(Square.A1)));
        assertTrue(Bitboard.intersects(Bitboard.ofSquare(Square.B2), Bitboard.FILE_B));
        assertTrue(Bitboard.intersects(Bitboard.ofSquare(Square.C3), Bitboard.ALL));
        assertTrue(Bitboard.intersects(Bitboard.RANK_5,   Bitboard.ALL));
    }

    @Test
    public void disjoint() {
        assertTrue(Bitboard.disjoint(Bitboard.EMPTY,       Bitboard.EMPTY));
        assertTrue(Bitboard.disjoint(Bitboard.EMPTY,       Bitboard.ofSquare(Square.B2)));
        assertTrue(Bitboard.disjoint(Bitboard.EMPTY,       Bitboard.ALL));
        assertTrue(Bitboard.disjoint(Bitboard.ofSquare(Square.B1), Bitboard.ofSquare(Square.B2)));
        assertTrue(Bitboard.disjoint(Bitboard.RANK_1,   Bitboard.RANK_2));

        assertFalse(Bitboard.disjoint(Bitboard.ofSquare(Square.A1), Bitboard.ofSquare(Square.A1)));
        assertFalse(Bitboard.disjoint(Bitboard.ofSquare(Square.B2), Bitboard.FILE_B));
        assertFalse(Bitboard.disjoint(Bitboard.ofSquare(Square.C3), Bitboard.ALL));
        assertFalse(Bitboard.disjoint(Bitboard.RANK_5,   Bitboard.ALL));
    }

    @Test
    public void contains() {
        assertFalse(Bitboard.contains(Bitboard.EMPTY,         Bitboard.ofSquare(Square.B7)));
        assertFalse(Bitboard.contains(Bitboard.EMPTY,         Bitboard.ALL));
        assertFalse(Bitboard.contains(Bitboard.ofSquare(Square.A8),   Bitboard.ofSquare(Square.A7)));
        assertFalse(Bitboard.contains(Bitboard.ofSquare(Square.A7),   Bitboard.RANK_7));
        assertFalse(Bitboard.contains(Bitboard.BLACK_SQUARES, Bitboard.RANK_4));

        assertTrue(Bitboard.contains(Bitboard.EMPTY,       Bitboard.EMPTY));
        assertTrue(Bitboard.contains(Bitboard.ofSquare(Square.A8), Bitboard.EMPTY));
        assertTrue(Bitboard.contains(Bitboard.ofSquare(Square.A4), Bitboard.ofSquare(Square.A4)));
        assertTrue(Bitboard.contains(Bitboard.RANK_3,   Bitboard.ofSquare(Square.C3)));
        assertTrue(Bitboard.contains(Bitboard.FILE_A,    Bitboard.FILE_A));
        assertTrue(Bitboard.contains(Bitboard.ALL,         Bitboard.EMPTY));
        assertTrue(Bitboard.contains(Bitboard.ALL,         Bitboard.ofSquare(Square.A7)));
        assertTrue(Bitboard.contains(Bitboard.ALL,         Bitboard.ALL));
    }

    @Test
    public void count() {
        assertEquals(0, Bitboard.count(Bitboard.EMPTY));
        assertEquals(1, Bitboard.count(Bitboard.ofSquare(Square.A4)));
        assertEquals(8, Bitboard.count(Bitboard.RANK_1));
        assertEquals(32, Bitboard.count(Bitboard.BLACK_SQUARES));
        assertEquals(64, Bitboard.count(Bitboard.ALL));
    }

    @Test
    public void shift() {
        assertBitboardEquals(Bitboard.ofSquare(Square.C5), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.N));
        assertBitboardEquals(Bitboard.ofSquare(Square.D5), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.NE));
        assertBitboardEquals(Bitboard.ofSquare(Square.D4), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.E));
        assertBitboardEquals(Bitboard.ofSquare(Square.D3), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.SE));
        assertBitboardEquals(Bitboard.ofSquare(Square.C3), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.S));
        assertBitboardEquals(Bitboard.ofSquare(Square.B3), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.SW));
        assertBitboardEquals(Bitboard.ofSquare(Square.B4), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.W));
        assertBitboardEquals(Bitboard.ofSquare(Square.B5), Bitboard.shift(Bitboard.ofSquare(Square.C4), Direction.NW));

        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.A8), Direction.N));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.H8), Direction.NE));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.H4), Direction.E));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.H1), Direction.SE));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.A1), Direction.S));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.A1), Direction.SW));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.A4), Direction.W));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Bitboard.ofSquare(Square.A8), Direction.NW));
    }

    @Test
    public void shiftRev() {
        final long bb = Bitboard.ofSquare(Square.D4);
        for (final Direction d : Direction.values()) {
            assertBitboardEquals(
                    bb,
                    Bitboard.shiftRev(Bitboard.shift(bb, d), d));
        }
    }

    @Test
    public void shiftInto() {
        final long area = Bitboard.RANK_5;
        assertBitboardEquals(
                Bitboard.ofSquare(Square.C5),
                Bitboard.shiftInto(Bitboard.ofSquare(Square.C4), Direction.N, area)
        );
        assertBitboardEquals(
                Bitboard.EMPTY,
                Bitboard.shiftInto(Bitboard.ofSquare(Square.C5), Direction.N, area)
        );
        assertBitboardEquals(
                Bitboard.EMPTY,
                Bitboard.shiftInto(Bitboard.ofSquare(Square.H4), Direction.E, Bitboard.ALL)
        );
    }

    @Test
    public void fill() {
        assertBitboardEquals(
                Bitboard.ofSquares(Square.C4, Square.C5, Square.C6, Square.C7, Square.C8),
                Bitboard.fill(Bitboard.ofSquare(Square.C4), Direction.N)
        );
        assertBitboardEquals(
                Bitboard.ofSquares(Square.D4, Square.E4, Square.F4, Square.G4, Square.H4),
                Bitboard.fill(Bitboard.ofSquare(Square.D4), Direction.E)
        );
        assertBitboardEquals(
                Bitboard.ofSquare(Square.A8),
                Bitboard.fill(Bitboard.ofSquare(Square.A8), Direction.N)
        );
    }

    @Test
    public void fillInto() {
        assertBitboardEquals(
                Bitboard.ofSquares(Square.C4, Square.C5, Square.C6),
                Bitboard.fillInto(
                        Bitboard.ofSquare(Square.C4),
                        Direction.N,
                        Bitboard.ofSquares(Square.C5, Square.C6)
                )
        );
        assertBitboardEquals(
                Bitboard.ofSquare(Square.C4),
                Bitboard.fillInto(
                        Bitboard.ofSquare(Square.C4),
                        Direction.N,
                        Bitboard.ofSquare(Square.C4)
                )
        );
        assertBitboardEquals(
                Bitboard.ofSquare(Square.C4),
                Bitboard.fillInto(
                        Bitboard.ofSquare(Square.C4),
                        Direction.N,
                        Bitboard.EMPTY
                )
        );
    }

    @Test
    public void slide() {
        assertBitboardEquals(
                Bitboard.ofSquares(Square.F3, Square.G2, Square.H1),
                Bitboard.slide(
                        Bitboard.ofSquare(Square.E4),
                        Bitboard.ALL,
                        Direction.SE)
        );
        assertBitboardEquals(
                Bitboard.ofSquares(Square.D8, Square.E8, Square.F8),
                Bitboard.slide(
                        Bitboard.ofSquare(Square.C8),
                        Bitboard.ofSquares(Square.D8, Square.E8),
                        Direction.E)
        );
        assertBitboardEquals(
                Bitboard.ofSquares(Square.C4, Square.B5),
                Bitboard.slide(
                        Bitboard.ofSquare(Square.D3),
                        Bitboard.ofSquares(Square.C4),
                        Direction.NW)
        );
    }

    private void assertBitboardEquals(final long expected, final long actual) {
        assertEquals(expected, actual, String.format("Expected:%n%sGot:%n%s%n",
                Bitboard.toString(expected), Bitboard.toString(actual)));
    }
}
