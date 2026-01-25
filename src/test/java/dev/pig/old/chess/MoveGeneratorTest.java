package dev.pig.old.chess;

import dev.pig.old.chess.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {

    @Test
    void getThreatened_starting() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        assertEquals(Bitboard.RANKS[5] | Bitboard.RANKS[6] | (Bitboard.RANKS[7] ^ (Bitboard.INDEX[56] | Bitboard.INDEX[63])), analyser.getThreatened());
    }

    @Test
    void getThreatened_rooks() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("1r1r4/2k5/1r1r4/8/8/8/7K/1r1r4"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        assertEquals(Bitboard.RANKS[0] | Bitboard.RANKS[5] | Bitboard.RANKS[7] | Bitboard.FILES[1] | Bitboard.FILES[3], analyser.getThreatened());
    }

    @Test
    void getThreatened_bishops() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("kb1b4/b3b3/7P/4b3/5b2/2b5/1b1P1K1P/4b3"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        assertEquals((Bitboard.BLACK_SQUARES | Bitboard.INDEX[49]) ^ Bitboard.INDEX[4], analyser.getThreatened());
    }

    @Test
    void getThreatened_knights() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("8/2p3p1/k3N3/2N3N1/4N3/8/8/6K1"), false, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {5, 7, 11, 13, 14, 15, 17, 18, 19, 21, 22, 23, 24, 27, 28, 29, 34, 38, 40, 43, 44, 45, 49, 50, 51, 53, 54, 55, 59, 61};
        long threats = 0L;
        for (int square : squares) {
            threats |= Bitboard.INDEX[square];
        }
        assertEquals(threats, analyser.getThreatened());
    }

    @Test
    void getThreatened_queens() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k4Q2/8/8/8/8/8/8/2Q4K"), false, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {9, 11, 14, 15, 16, 20, 25, 38, 43, 47, 52, 54};
        long threats = (Bitboard.RANKS[0] | Bitboard.RANKS[7] | Bitboard.FILES[2] | Bitboard.FILES[5]) ^ (Bitboard.INDEX[2] | Bitboard.INDEX[61]);
        for (int square : squares) {
            threats |= Bitboard.INDEX[square];
        }
        assertEquals(threats, analyser.getThreatened());
    }

    @Test
    void getThreatened_pawns() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k7/8/5p2/p1p1p3/1p1P4/8/8/7K"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {16, 18, 25, 27, 29, 36, 38, 48, 49, 57};
        long threats = 0L;
        for (int square : squares) {
            threats |= Bitboard.INDEX[square];
        }
        assertEquals(threats, analyser.getThreatened());
    }

    @Test
    void getMovableSquares_notCheck() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k7/8/4P3/8/3K4/8/8/1Q3N2"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {1, 5, 27, 44};
        long movable = Bitboard.ALL;
        for (int square : squares) {
            movable ^= Bitboard.INDEX[square];
        }
        assertEquals(movable, analyser.getMovableSquares());
        assertFalse(analyser.isCheck());
    }

    @Test
    void getMovableSquares_notCheck_blocked() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k2q4/8/3Q4/8/3K4/8/8/8"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {27, 43};
        long movable = Bitboard.ALL;
        for (int square : squares) {
            movable ^= Bitboard.INDEX[square];
        }
        assertEquals(movable, analyser.getMovableSquares());
        assertFalse(analyser.isCheck());
    }

    @Test
    void getMovableSquares_check_rook() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k2r4/8/8/8/3K4/8/8/8"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {35, 43, 51, 59};
        long movable = Bitboard.EMPTY;
        for (int square : squares) {
            movable ^= Bitboard.INDEX[square];
        }
        assertEquals(movable, analyser.getMovableSquares());
        assertTrue(analyser.isCheck());
    }

    @Test
    void getMovableSquares_check_bishop() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k6b/8/5K2/8/8/8/8/8"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {54, 63};
        long movable = Bitboard.EMPTY;
        for (int square : squares) {
            movable ^= Bitboard.INDEX[square];
        }
        assertEquals(movable, analyser.getMovableSquares());
        assertTrue(analyser.isCheck());
    }

    @Test
    void getMovableSquares_check_knight() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k7/8/5n2/4PP2/4K3/8/8/8"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {45};
        long movable = Bitboard.EMPTY;
        for (int square : squares) {
            movable ^= Bitboard.INDEX[square];
        }
        assertEquals(movable, analyser.getMovableSquares());
        assertTrue(analyser.isCheck());
    }

    @Test
    void getMovableSquares_check_pawn() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("k7/8/8/3pPP2/4K3/8/8/8"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {35};
        long movable = Bitboard.EMPTY;
        for (int square : squares) {
            movable ^= Bitboard.INDEX[square];
        }
        assertEquals(movable, analyser.getMovableSquares());
        assertTrue(analyser.isCheck());
    }

    @Test
    void getAllPin_horizontal() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("8/8/7k/8/8/8/8/KR5r"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {0};
        long pin = Bitboard.RANKS[0];
        for (int square : squares) {
            pin ^= Bitboard.INDEX[square];
        }
        assertEquals(pin, analyser.getAllPin());
        assertFalse(analyser.isCheck());
    }

    @Test
    void getAllPin_diagonal() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("7b/8/8/4P3/3K4/2R5/8/8"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {36, 45, 54, 63};
        long pin = Bitboard.EMPTY;
        for (int square : squares) {
            pin ^= Bitboard.INDEX[square];
        }
        assertEquals(pin, analyser.getAllPin());
        assertFalse(analyser.isCheck());
    }

    @Test
    void getAllPin_allDirections() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("7b/q2r4/8/2BNP3/r1RKN2q/2QRB3/8/b2r2b1"), true, Castling.ALL_ALLOWED, Bitboard.EMPTY);
        final int[] squares = {0, 3, 6, 9, 11, 13, 18, 19, 20, 34, 35, 36, 41, 43, 45, 48, 51, 54, 63};
        long pin = Bitboard.RANKS[3] ^ Bitboard.INDEX[27];
        for (int square : squares) {
            pin ^= Bitboard.INDEX[square];
        }
        assertEquals(pin, analyser.getAllPin());
        assertFalse(analyser.isCheck());
    }

    // The majority of move generation will be covered in position depth tests
    // These tests are specifically to cover the very rare en passant check move

    @Test
    void generateLegalMoves_illegalEnPassant_true() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("rnbqk1n1/pppp1p2/8/2KPp2r/8/8/PPP1PPPP/RNBQ1BNR"), true, Castling.ALL_ALLOWED, Bitboard.INDEX[44]);
        for (ChessMove move : analyser.generateLegalMoves()) {
            assertNotEquals("d5e6", move.toString());
        }
    }

    @Test
    void generateLegalMoves_illegalEnPassant_false() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("rnbqk1n1/pppp1p2/8/2KPp3/7r/8/PPP1PPPP/RNBQ1BNR"), true, Castling.ALL_ALLOWED, Bitboard.INDEX[44]);
        boolean movePresent = false;
        for (ChessMove move : analyser.generateLegalMoves()) {
            if (move.toString().equals("d5e6")) movePresent = true;
        }
        assertTrue(movePresent);
    }

    @Test
    void generateLegalMoves_illegalEnPassant_pawnCheck() {
        final MoveGenerator analyser = new MoveGenerator(Fen.toBoard("rnbqk1n1/pppp1p2/8/3Pp3/3K4/8/PPP1PPPP/RNBQ1BNR"), true, Castling.ALL_ALLOWED, Bitboard.INDEX[44]);
        boolean movePresent = false;
        for (ChessMove move : analyser.generateLegalMoves()) {
            if (move.toString().equals("d5e6")) movePresent = true;
        }
        assertTrue(movePresent);
    }

}