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

        assertFalse(Bitboard.isEmpty(Square.H6.bitboard()));
        assertFalse(Bitboard.isEmpty(Bitboard.BLACK_SQUARES));
        assertFalse(Bitboard.isEmpty(Bitboard.ALL));
    }

    @Test
    public void intersects() {
        assertFalse(Bitboard.intersects(Bitboard.EMPTY,       Bitboard.EMPTY));
        assertFalse(Bitboard.intersects(Bitboard.EMPTY,       Square.B2.bitboard()));
        assertFalse(Bitboard.intersects(Bitboard.EMPTY,       Bitboard.ALL));
        assertFalse(Bitboard.intersects(Square.B1.bitboard(), Square.B2.bitboard()));
        assertFalse(Bitboard.intersects(Rank.r1.bitboard(),   Rank.r2.bitboard()));

        assertTrue(Bitboard.intersects(Square.A1.bitboard(), Square.A1.bitboard()));
        assertTrue(Bitboard.intersects(Square.B2.bitboard(), File.B.bitboard()));
        assertTrue(Bitboard.intersects(Square.C3.bitboard(), Bitboard.ALL));
        assertTrue(Bitboard.intersects(Rank.r5.bitboard(),   Bitboard.ALL));
    }

    @Test
    public void disjoint() {
        assertTrue(Bitboard.disjoint(Bitboard.EMPTY,       Bitboard.EMPTY));
        assertTrue(Bitboard.disjoint(Bitboard.EMPTY,       Square.B2.bitboard()));
        assertTrue(Bitboard.disjoint(Bitboard.EMPTY,       Bitboard.ALL));
        assertTrue(Bitboard.disjoint(Square.B1.bitboard(), Square.B2.bitboard()));
        assertTrue(Bitboard.disjoint(Rank.r1.bitboard(),   Rank.r2.bitboard()));

        assertFalse(Bitboard.disjoint(Square.A1.bitboard(), Square.A1.bitboard()));
        assertFalse(Bitboard.disjoint(Square.B2.bitboard(), File.B.bitboard()));
        assertFalse(Bitboard.disjoint(Square.C3.bitboard(), Bitboard.ALL));
        assertFalse(Bitboard.disjoint(Rank.r5.bitboard(),   Bitboard.ALL));
    }

    @Test
    public void contains() {
        assertFalse(Bitboard.contains(Bitboard.EMPTY,         Square.B7.bitboard()));
        assertFalse(Bitboard.contains(Bitboard.EMPTY,         Bitboard.ALL));
        assertFalse(Bitboard.contains(Square.A8.bitboard(),   Square.A7.bitboard()));
        assertFalse(Bitboard.contains(Square.A7.bitboard(),   Rank.r7.bitboard()));
        assertFalse(Bitboard.contains(Bitboard.BLACK_SQUARES, Rank.r4.bitboard()));

        assertTrue(Bitboard.contains(Bitboard.EMPTY,       Bitboard.EMPTY));
        assertTrue(Bitboard.contains(Square.A8.bitboard(), Bitboard.EMPTY));
        assertTrue(Bitboard.contains(Square.A4.bitboard(), Square.A4.bitboard()));
        assertTrue(Bitboard.contains(Rank.r3.bitboard(),   Square.C3.bitboard()));
        assertTrue(Bitboard.contains(File.A.bitboard(),    File.A.bitboard()));
        assertTrue(Bitboard.contains(Bitboard.ALL,         Bitboard.EMPTY));
        assertTrue(Bitboard.contains(Bitboard.ALL,         Square.A7.bitboard()));
        assertTrue(Bitboard.contains(Bitboard.ALL,         Bitboard.ALL));
    }

    @Test
    public void intersection() {
        assertBitboardEquals(
                Bitboard.intersection(Bitboard.WHITE_SQUARES, Bitboard.BLACK_SQUARES),
                Bitboard.EMPTY
        );
        assertBitboardEquals(
                Bitboard.intersection(Bitboard.of(Square.C7, Square.C4), Bitboard.of(Square.C4)),
                Square.C4.bitboard()
        );
    }

    @Test
    public void union() {
        assertBitboardEquals(
                Bitboard.union(Bitboard.WHITE_SQUARES, Bitboard.BLACK_SQUARES),
                Bitboard.ALL
        );
        assertBitboardEquals(
                Bitboard.union(Bitboard.of(Square.C7, Square.C4), Bitboard.of(Square.C4)),
                Bitboard.of(Square.C7, Square.C4)
        );
    }

    @Test
    public void xor() {
        assertBitboardEquals(
                Bitboard.xor(Bitboard.WHITE_SQUARES, Bitboard.BLACK_SQUARES),
                Bitboard.ALL
        );
        assertBitboardEquals(
                Bitboard.xor(Bitboard.of(Square.C7, Square.C4), Bitboard.of(Square.C4)),
                Square.C7.bitboard()
        );
    }

    @Test
    public void inverse() {
        assertBitboardEquals(
                Bitboard.inverse(Bitboard.ALL),
                Bitboard.EMPTY
        );
        assertBitboardEquals(
                Bitboard.inverse(Bitboard.WHITE_SQUARES),
                Bitboard.BLACK_SQUARES
        );
    }

    @Test
    public void count() {
        assertEquals(0, Bitboard.count(Bitboard.EMPTY));
        assertEquals(1, Bitboard.count(Square.A4.bitboard()));
        assertEquals(8, Bitboard.count(Rank.r1.bitboard()));
        assertEquals(32, Bitboard.count(Bitboard.BLACK_SQUARES));
        assertEquals(64, Bitboard.count(Bitboard.ALL));
    }

    @Test
    public void shift() {
        assertBitboardEquals(Square.C5.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.N));
        assertBitboardEquals(Square.D5.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.NE));
        assertBitboardEquals(Square.D4.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.E));
        assertBitboardEquals(Square.D3.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.SE));
        assertBitboardEquals(Square.C3.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.S));
        assertBitboardEquals(Square.B3.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.SW));
        assertBitboardEquals(Square.B4.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.W));
        assertBitboardEquals(Square.B5.bitboard(), Bitboard.shift(Square.C4.bitboard(), Direction.NW));

        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.A8.bitboard(), Direction.N));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.H8.bitboard(), Direction.NE));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.H4.bitboard(), Direction.E));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.H1.bitboard(), Direction.SE));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.A1.bitboard(), Direction.S));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.A1.bitboard(), Direction.SW));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.A4.bitboard(), Direction.W));
        assertBitboardEquals(Bitboard.EMPTY, Bitboard.shift(Square.A8.bitboard(), Direction.NW));
    }

    @Test
    public void shiftRev() {
        final long bb = Square.D4.bitboard();
        for (final Direction d : Direction.values()) {
            assertBitboardEquals(
                    bb,
                    Bitboard.shiftRev(Bitboard.shift(bb, d), d));
        }
    }

    @Test
    public void shiftInto() {
        final long area = Rank.r5.bitboard();
        assertBitboardEquals(
                Square.C5.bitboard(),
                Bitboard.shiftInto(Square.C4.bitboard(), Direction.N, area)
        );
        assertBitboardEquals(
                Bitboard.EMPTY,
                Bitboard.shiftInto(Square.C5.bitboard(), Direction.N, area)
        );
        assertBitboardEquals(
                Bitboard.EMPTY,
                Bitboard.shiftInto(Square.H4.bitboard(), Direction.E, Bitboard.ALL)
        );
    }

    @Test
    public void fill() {
        assertBitboardEquals(
                Bitboard.of(Square.C4, Square.C5, Square.C6, Square.C7, Square.C8),
                Bitboard.fill(Square.C4.bitboard(), Direction.N)
        );
        assertBitboardEquals(
                Bitboard.of(Square.D4, Square.E4, Square.F4, Square.G4, Square.H4),
                Bitboard.fill(Square.D4.bitboard(), Direction.E)
        );
        assertBitboardEquals(
                Square.A8.bitboard(),
                Bitboard.fill(Square.A8.bitboard(), Direction.N)
        );
    }

    @Test
    public void fillInto() {
        assertBitboardEquals(
                Bitboard.of(Square.C4, Square.C5, Square.C6),
                Bitboard.fillInto(
                        Square.C4.bitboard(),
                        Direction.N,
                        Bitboard.of(Square.C5, Square.C6)
                )
        );
        assertBitboardEquals(
                Square.C4.bitboard(),
                Bitboard.fillInto(
                        Square.C4.bitboard(),
                        Direction.N,
                        Square.C4.bitboard()
                )
        );
        assertBitboardEquals(
                Square.C4.bitboard(),
                Bitboard.fillInto(
                        Square.C4.bitboard(),
                        Direction.N,
                        Bitboard.EMPTY
                )
        );
    }

    private void assertBitboardEquals(final long expected, final long actual) {
        assertEquals(expected, actual, String.format("Expected:%n%sGot:%n%s%n",
                Bitboard.toString(expected), Bitboard.toString(actual)));
    }
}
