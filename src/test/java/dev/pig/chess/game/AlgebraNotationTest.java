package dev.pig.chess.game;

import dev.pig.chess.AlgebraNotation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlgebraNotationTest {

    private static final char[] FILES = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    private static final int[] RANKS = new int[] {1, 2, 3, 4, 5, 6, 7, 8};

    @Test
    void toBitboard() {

        assertEquals(0L, AlgebraNotation.toBitboard(null));
        assertEquals(0L, AlgebraNotation.toBitboard("-"));

        for (int rank = 0; rank < 8; rank++) { // For each rank...
            for (int file = 0; file < 8; file++) { // ... and each file...
                final String algebra = FILES[file] + "" + RANKS[rank]; // ... construct the algebra notation...
                assertEquals(1L << (rank * 8) + file, AlgebraNotation.toBitboard(algebra)); // ... and check it's the same as bitboard
            }
        }
    }

    @Test
    void fromBitboard() {

        assertEquals("-", AlgebraNotation.fromBitboard(0L));

        for (int rank = 0; rank < 8; rank++) { // For each rank...
            for (int file = 0; file < 8; file++) { // ... and each file...
                final String algebra = FILES[file] + "" + RANKS[rank]; // ... construct the algebra notation...
                assertEquals(algebra, AlgebraNotation.fromBitboard(1L << (rank * 8) + file)); // ... and check it's the same as bitboard
            }
        }
    }
}