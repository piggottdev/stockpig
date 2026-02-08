package dev.pig.stockpig.chess.bitboard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Attack map generation tests.
 */
public final class AttackTest {

    @Test
    public void kingAttack() {
        assertBitboardEquals(
                Bitboard.of(Square.C3, Square.D3, Square.E3, Square.C4,  Square.E4, Square.C5, Square.D5, Square.E5),
                Attack.king(Square.D4.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.B1, Square.A2, Square.B2),
                Attack.king(Square.A1.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.A7, Square.B7, Square.B8),
                Attack.king(Square.A8.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.G1, Square.G2, Square.H2),
                Attack.king(Square.H1.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.G7, Square.H7, Square.G8),
                Attack.king(Square.H8.bitboard())
        );
    }

    @Test
    public void knightAttack() {
        assertBitboardEquals(
                Bitboard.of(Square.D3, Square.F3, Square.G4, Square.G6, Square.F7, Square.D7, Square.C6, Square.C4),
                Attack.knight(Square.E5.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.A4, Square.C4, Square.D3, Square.D1),
                Attack.knight(Square.B2.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.A5, Square.C5, Square.D6, Square.D8),
                Attack.knight(Square.B7.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.E1, Square.E3, Square.F4, Square.H4),
                Attack.knight(Square.G2.bitboard())
        );
        assertBitboardEquals(
                Bitboard.of(Square.E8, Square.E6, Square.F5, Square.H5),
                Attack.knight(Square.G7.bitboard())
        );
    }

    @Test
    public void bishopAttack() {
        assertBitboardEquals(
                Bitboard.of(Square.B5, Square.B7, Square.A8, Square.D7, Square.E8, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1),
                Attack.bishop(
                        Square.C6.bitboard(),
                        ~Bitboard.of(Square.B7, Square.A8, Square.D7, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1))
        );
    }

    @Test
    public void rookAttack() {
        assertBitboardEquals(
                Bitboard.of(Square.C7, Square.C8, Square.B6, Square.A6, Square.C5, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6),
                Attack.rook(
                        Square.C6.bitboard(),
                        ~Bitboard.of(Square.B6, Square.C7, Square.C8, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6))
        );
    }

    @Test
    public void queenAttack() {
        assertBitboardEquals(
                Bitboard.of(Square.C7, Square.C8, Square.B6, Square.A6, Square.C5, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6,
                              Square.B5, Square.B7, Square.A8, Square.D7, Square.E8, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1),
                Attack.queen(
                        Square.C6.bitboard(),
                        ~Bitboard.of(Square.B6, Square.C7, Square.C8, Square.D6, Square.E6, Square.F6, Square.G6, Square.H6,
                                      Square.B7, Square.A8, Square.D7, Square.D5, Square.E4, Square.F3, Square.G2, Square.H1))
        );
    }

    private void assertBitboardEquals(final long expected, final long actual) {
        assertEquals(expected, actual, String.format("Expected:%n%sGot:%n%s%n",
                Bitboard.toString(expected), Bitboard.toString(actual)));
    }
}
