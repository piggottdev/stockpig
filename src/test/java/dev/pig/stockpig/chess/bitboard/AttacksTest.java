package dev.pig.stockpig.chess.bitboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Attack map generation tests.
 */
public final class AttacksTest {

    @Test
    public void kingAttacks() {
        assertBitboardEquals(
                Bitboard.of(Square.C3, Square.D3, Square.E3, Square.C4,  Square.E4, Square.C5, Square.D5, Square.E5),
                Attacks.king(Square.D4.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.B1, Square.A2, Square.B2),
                Attacks.king(Square.A1.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.A7, Square.B7, Square.B8),
                Attacks.king(Square.A8.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.G1, Square.G2, Square.H2),
                Attacks.king(Square.H1.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.G7, Square.H7, Square.G8),
                Attacks.king(Square.H8.bitboard())
        );
    }

    @Test
    public void knightAttacks() {
        assertBitboardEquals(
                Bitboard.of(Square.D3, Square.F3, Square.G4, Square.G6, Square.F7, Square.D7, Square.C6, Square.C4),
                Attacks.knight(Square.E5.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.A4, Square.C4, Square.D3, Square.D1),
                Attacks.knight(Square.B2.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.A5, Square.C5, Square.D6, Square.D8),
                Attacks.knight(Square.B7.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.E1, Square.E3, Square.F4, Square.H4),
                Attacks.knight(Square.G2.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.E8, Square.E6, Square.F5, Square.H5),
                Attacks.knight(Square.G7.bitboard())
        );
    }

    @Test
    public void bishopAttacks() {
        assertBitboardEquals(
                Bitboard.of(Square.B5, Square.B7, Square.A8, Square.D7, Square.E8, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1),
                Attacks.bishop(
                        Square.C6.bitboard(),
                        Bitboard.of(Square.B7, Square.A8, Square.D7, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1))
        );
    }

    @Test
    public void rookAttacks() {
        assertBitboardEquals(
                Bitboard.of(Square.C7, Square.C8, Square.B6, Square.A6, Square.C5, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6),
                Attacks.rook(
                        Square.C6.bitboard(),
                        Bitboard.of(Square.B6, Square.C7, Square.C8, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6))
        );
    }

    @Test
    public void queenAttacks() {
        assertBitboardEquals(
                Bitboard.of(Square.C7, Square.C8, Square.B6, Square.A6, Square.C5, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6,
                              Square.B5, Square.B7, Square.A8, Square.D7, Square.E8, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1),
                Attacks.queen(
                        Square.C6.bitboard(),
                        Bitboard.of(Square.B6, Square.C7, Square.C8, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6,
                                      Square.B7, Square.A8, Square.D7, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1))
        );
    }

    @Test
    public void slideAttacks() {
        assertBitboardEquals(
                Bitboard.of(Square.F3, Square.G2, Square.H1),
                Attacks.slide(
                        Square.E4.bitboard(),
                        Bitboard.ALL,
                        Direction.SE)
        );
        assertBitboardEquals(
                Bitboard.of(Square.D8, Square.E8, Square.F8),
                Attacks.slide(
                        Square.C8.bitboard(),
                        Bitboard.of(Square.D8, Square.E8),
                        Direction.E)
        );
        assertBitboardEquals(
                Bitboard.of(Square.C4, Square.B5),
                Attacks.slide(
                        Square.D3.bitboard(),
                        Bitboard.of(Square.C4),
                        Direction.NW)
        );
    }

    private void assertBitboardEquals(final long expected, final long actual) {
        assertEquals(expected, actual, String.format("Expected:%n%sGot:%n%s%n",
                Bitboard.toString(expected), Bitboard.toString(actual)));
    }
}
