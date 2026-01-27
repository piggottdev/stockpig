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
                Bitboard.from(Square.C3, Square.D3, Square.E3, Square.C4,  Square.E4, Square.C5, Square.D5, Square.E5),
                Attacks.king(Square.D4.bitboard()));
        assertBitboardEquals(
                Bitboard.from(Square.B1, Square.A2, Square.B2),
                Attacks.king(Square.A1.bitboard()));
        assertBitboardEquals(
                Bitboard.from(Square.A7, Square.B7, Square.B8),
                Attacks.king(Square.A8.bitboard()));
        assertBitboardEquals(
                Bitboard.from(Square.G1, Square.G2, Square.H2),
                Attacks.king(Square.H1.bitboard()));
        assertBitboardEquals(
                Bitboard.from(Square.G7, Square.H7, Square.G8),
                Attacks.king(Square.H8.bitboard()));
    }

    @Test
    public void knightAttacks() {
        assertBitboardEquals(
                Bitboard.from(Square.A5, Square.C5, Square.D6, Square.D8),
                Attacks.knight(Square.B7.bitboard()));
        assertBitboardEquals(
                Bitboard.from(Square.E1, Square.E3, Square.F4, Square.H4),
                Attacks.knight(Square.G2.bitboard()));
    }

    @Test
    public void bishopAttacks() {
        assertBitboardEquals(
                Bitboard.from(Square.B5, Square.B7, Square.A8, Square.D7, Square.E8, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1),
                Attacks.bishop(
                        Square.C6.bitboard(),
                        Bitboard.from(Square.B7, Square.A8, Square.D7, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1))
        );
    }

    @Test
    public void rookAttacks() {
        assertBitboardEquals(
                Bitboard.from(Square.C7, Square.C8, Square.B6, Square.A6, Square.C5, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6),
                Attacks.rook(
                        Square.C6.bitboard(),
                        Bitboard.from(Square.B6, Square.C7, Square.C8, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6))
        );
    }

    @Test
    public void queenAttacks() {
        assertBitboardEquals(
                Bitboard.from(Square.C7, Square.C8, Square.B6, Square.A6, Square.C5, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6,
                              Square.B5, Square.B7, Square.A8, Square.D7, Square.E8, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1),
                Attacks.queen(
                        Square.C6.bitboard(),
                        Bitboard.from(Square.B6, Square.C7, Square.C8, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6,
                                      Square.B7, Square.A8, Square.D7, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1))
        );
    }

    @Test
    public void slideAttacks() {
        assertBitboardEquals(
                Bitboard.from(Square.F3, Square.G2, Square.H1),
                Attacks.slide(
                        Square.E4.bitboard(),
                        Bitboard.ALL,
                        Direction.SE));
        assertBitboardEquals(
                Bitboard.from(Square.D8, Square.E8, Square.F8),
                Attacks.slide(
                        Square.C8.bitboard(),
                        Bitboard.from(Square.D8, Square.E8),
                        Direction.E));
        assertBitboardEquals(
                Bitboard.from(Square.C4, Square.B5),
                Attacks.slide(
                        Square.D3.bitboard(),
                        Bitboard.from(Square.C4),
                        Direction.NW));
    }

    private void assertBitboardEquals(final long expected, final long actual) {
        assertEquals(expected, actual, String.format("Expected:%n%sGot:%n%s%n",
                Bitboard.toString(expected), Bitboard.toString(actual)));
    }
}
