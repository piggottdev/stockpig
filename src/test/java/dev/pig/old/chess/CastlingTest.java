package dev.pig.old.chess;

import dev.pig.old.chess.Bitboard;
import dev.pig.old.chess.Castling;
import dev.pig.old.chess.ChessMove;
import dev.pig.old.chess.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CastlingTest {

    @Test
    void getCastlesAllowedAfterMove_white_takeKingSideRook() {
        final ChessMove move = ChessMove.basicCapture(Bitboard.INDEX[54], Bitboard.INDEX[63], Piece.WHITE | Piece.QUEEN, Piece.BLACK | Piece.ROOK);

        assertEquals(0b1110, Castling.getCastlesAllowedAfterMove(Castling.ALL_ALLOWED, move, true));

        assertEquals(0b1110, Castling.getCastlesAllowedAfterMove(0b1110, move, true));
        assertEquals(0b1100, Castling.getCastlesAllowedAfterMove(0b1100, move, true));
        assertEquals(0b1000, Castling.getCastlesAllowedAfterMove(0b1000, move, true));

        assertEquals(0b1010, Castling.getCastlesAllowedAfterMove(0b1011, move, true));
        assertEquals(0b1100, Castling.getCastlesAllowedAfterMove(0b1101, move, true));
        assertEquals(0b0100, Castling.getCastlesAllowedAfterMove(0b0101, move, true));

        assertEquals(0, Castling.getCastlesAllowedAfterMove(0, move, true));
    }

    @Test
    void getCastlesAllowedAfterMove_white_takeQueenSideRook() {
        final ChessMove move = ChessMove.basicCapture(Bitboard.INDEX[32], Bitboard.INDEX[56], Piece.WHITE | Piece.BISHOP, Piece.BLACK | Piece.ROOK);

        assertEquals(0b1101, Castling.getCastlesAllowedAfterMove(Castling.ALL_ALLOWED, move, true));

        assertEquals(0b1100, Castling.getCastlesAllowedAfterMove(0b1100, move, true));
        assertEquals(0b1101, Castling.getCastlesAllowedAfterMove(0b1101, move, true));
        assertEquals(0b1001, Castling.getCastlesAllowedAfterMove(0b1001, move, true));

        assertEquals(0b1001, Castling.getCastlesAllowedAfterMove(0b1011, move, true));
        assertEquals(0b1100, Castling.getCastlesAllowedAfterMove(0b1110, move, true));
        assertEquals(0b0100, Castling.getCastlesAllowedAfterMove(0b0110, move, true));

        assertEquals(0, Castling.getCastlesAllowedAfterMove(0, move, true));
    }

    @Test
    void getCastlesAllowedAfterMove_white_moveKing() {
        final ChessMove move = ChessMove.basicMove(Bitboard.INDEX[4], Bitboard.INDEX[12], Piece.WHITE | Piece.KING);

        assertEquals(0b0011, Castling.getCastlesAllowedAfterMove(Castling.ALL_ALLOWED, move, true));

        assertEquals(0b0001, Castling.getCastlesAllowedAfterMove(0b1101, move, true));
        assertEquals(0b0010, Castling.getCastlesAllowedAfterMove(0b1110, move, true));
        assertEquals(0b0011, Castling.getCastlesAllowedAfterMove(0b1011, move, true));

        assertEquals(0b0001, Castling.getCastlesAllowedAfterMove(0b0001, move, true));
        assertEquals(0b0011, Castling.getCastlesAllowedAfterMove(0b0011, move, true));
        assertEquals(0b0010, Castling.getCastlesAllowedAfterMove(0b0010, move, true));

        assertEquals(0, Castling.getCastlesAllowedAfterMove(0, move, true));
    }

    @Test
    void getCastlesAllowedAfterMove_white_moveKingSideRook() {
        final ChessMove move = ChessMove.basicMove(Bitboard.INDEX[7], Bitboard.INDEX[19], Piece.WHITE | Piece.ROOK);

        assertEquals(0b1011, Castling.getCastlesAllowedAfterMove(Castling.ALL_ALLOWED, move, true));

        assertEquals(0b1001, Castling.getCastlesAllowedAfterMove(0b1101, move, true));
        assertEquals(0b0000, Castling.getCastlesAllowedAfterMove(0b0100, move, true));
        assertEquals(0b0001, Castling.getCastlesAllowedAfterMove(0b0101, move, true));

        assertEquals(0b1010, Castling.getCastlesAllowedAfterMove(0b1010, move, true));
        assertEquals(0b0011, Castling.getCastlesAllowedAfterMove(0b0011, move, true));
        assertEquals(0b1010, Castling.getCastlesAllowedAfterMove(0b1010, move, true));

        assertEquals(0, Castling.getCastlesAllowedAfterMove(0, move, true));
    }

    @Test
    void getCastlesAllowedAfterMove_white_moveQueenSideRook() {
        final ChessMove move = ChessMove.basicMove(Bitboard.INDEX[0], Bitboard.INDEX[12], Piece.WHITE | Piece.ROOK);

        assertEquals(0b0111, Castling.getCastlesAllowedAfterMove(Castling.ALL_ALLOWED, move, true));

        assertEquals(0b0101, Castling.getCastlesAllowedAfterMove(0b1101, move, true));
        assertEquals(0b0000, Castling.getCastlesAllowedAfterMove(0b1000, move, true));
        assertEquals(0b0001, Castling.getCastlesAllowedAfterMove(0b1001, move, true));

        assertEquals(0b0010, Castling.getCastlesAllowedAfterMove(0b0010, move, true));
        assertEquals(0b0011, Castling.getCastlesAllowedAfterMove(0b0011, move, true));
        assertEquals(0b0110, Castling.getCastlesAllowedAfterMove(0b0110, move, true));

        assertEquals(0, Castling.getCastlesAllowedAfterMove(0, move, true));
    }

    @Test
    void getKingSideCastleIfPossible_white() {
        // Empty board
        assertNotNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.EMPTY));
        assertNotNull(Castling.getKingSideCastleIfPossible(0b0100, true, Bitboard.ALL ^ (Bitboard.INDEX[4] | Bitboard.INDEX[7]), Bitboard.EMPTY));

        // Limit
        assertNotNull(Castling.getKingSideCastleIfPossible(0b0101, true, Bitboard.EMPTY ^ (Bitboard.INDEX[5] | Bitboard.INDEX[6]),
                Bitboard.ALL ^ (Bitboard.INDEX[4] | Bitboard.INDEX[5] | Bitboard.INDEX[6])));

        // Not allowed due to occupancy
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL ^ Bitboard.INDEX[5], Bitboard.EMPTY));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL ^ Bitboard.INDEX[6], Bitboard.EMPTY));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL ^ (Bitboard.INDEX[5] | Bitboard.INDEX[6]), Bitboard.EMPTY));

        // Not allowed due to check
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[4]));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[5]));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[6]));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[6] | Bitboard.INDEX[4]));

        // Not allowed due to bitmap
        assertNull(Castling.getKingSideCastleIfPossible(0, true, Bitboard.ALL, Bitboard.EMPTY));
        assertNull(Castling.getKingSideCastleIfPossible(0b1011, true, Bitboard.ALL, Bitboard.EMPTY));
    }

    @Test
    void getKingSideCastleIfPossible_black() {
        // Empty board
        assertNotNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.EMPTY));
        assertNotNull(Castling.getKingSideCastleIfPossible(0b0001, false, Bitboard.ALL ^ (Bitboard.INDEX[60] | Bitboard.INDEX[63]), Bitboard.EMPTY));

        // Limit
        assertNotNull(Castling.getKingSideCastleIfPossible(0b0101, false, Bitboard.EMPTY ^ (Bitboard.INDEX[61] | Bitboard.INDEX[62]),
                Bitboard.ALL ^ (Bitboard.INDEX[60] | Bitboard.INDEX[61] | Bitboard.INDEX[62])));

        // Not allowed due to occupancy
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL ^ Bitboard.INDEX[61], Bitboard.EMPTY));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL ^ Bitboard.INDEX[62], Bitboard.EMPTY));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL ^ (Bitboard.INDEX[61] | Bitboard.INDEX[62]), Bitboard.EMPTY));

        // Not allowed due to check
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[60]));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[61]));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[62]));
        assertNull(Castling.getKingSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[60] | Bitboard.INDEX[62]));

        // Not allowed due to bitmap
        assertNull(Castling.getKingSideCastleIfPossible(0, false, Bitboard.ALL, Bitboard.EMPTY));
        assertNull(Castling.getKingSideCastleIfPossible(0b1110, false, Bitboard.ALL, Bitboard.EMPTY));
    }

    @Test
    void getQueenSideCastleIfPossible_white() {
        // Empty board
        assertNotNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.EMPTY));
        assertNotNull(Castling.getQueenSideCastleIfPossible(0b1000, true, Bitboard.ALL ^ (Bitboard.INDEX[4] | Bitboard.INDEX[0]), Bitboard.EMPTY));

        // Limit
        assertNotNull(Castling.getQueenSideCastleIfPossible(0b1001, true, Bitboard.EMPTY ^ (Bitboard.INDEX[1] | Bitboard.INDEX[2] | Bitboard.INDEX[3]),
                Bitboard.ALL ^ (Bitboard.INDEX[2] | Bitboard.INDEX[3] | Bitboard.INDEX[4])));

        // Not allowed due to occupancy
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL ^ Bitboard.INDEX[1], Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL ^ Bitboard.INDEX[2], Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL ^ Bitboard.INDEX[3], Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL ^ (Bitboard.INDEX[1] | Bitboard.INDEX[3]), Bitboard.EMPTY));

        // Not allowed due to check
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[2]));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[3]));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[4]));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, true, Bitboard.ALL, Bitboard.INDEX[2] | Bitboard.INDEX[4]));

        // Not allowed due to bitmap
        assertNull(Castling.getQueenSideCastleIfPossible(0, true, Bitboard.ALL, Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(0b0111, true, Bitboard.ALL, Bitboard.EMPTY));
    }

    @Test
    void getQueenSideCastleIfPossible_black() {
        // Empty board
        assertNotNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.EMPTY));
        assertNotNull(Castling.getQueenSideCastleIfPossible(0b0010, false, Bitboard.ALL ^ (Bitboard.INDEX[60] | Bitboard.INDEX[56]), Bitboard.EMPTY));

        // Limit
        assertNotNull(Castling.getQueenSideCastleIfPossible(0b0110, false, Bitboard.EMPTY ^ (Bitboard.INDEX[57] | Bitboard.INDEX[58] | Bitboard.INDEX[59]),
                Bitboard.ALL ^ (Bitboard.INDEX[58] | Bitboard.INDEX[59] | Bitboard.INDEX[60])));

        // Not allowed due to occupancy
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL ^ Bitboard.INDEX[57], Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL ^ Bitboard.INDEX[58], Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL ^ Bitboard.INDEX[59], Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL ^ (Bitboard.INDEX[57] | Bitboard.INDEX[59]), Bitboard.EMPTY));

        // Not allowed due to check
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[58]));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[59]));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[60]));
        assertNull(Castling.getQueenSideCastleIfPossible(Castling.ALL_ALLOWED, false, Bitboard.ALL, Bitboard.INDEX[58] | Bitboard.INDEX[60]));

        // Not allowed due to bitmap
        assertNull(Castling.getQueenSideCastleIfPossible(0, false, Bitboard.ALL, Bitboard.EMPTY));
        assertNull(Castling.getQueenSideCastleIfPossible(0b1101, false, Bitboard.ALL, Bitboard.EMPTY));
    }

    @Test
    void fromString() {
        assertEquals(Castling.ALL_ALLOWED, Castling.fromString("KQkq"));
        assertEquals(0b0111, Castling.fromString("Kkq"));
        assertEquals(0b1011, Castling.fromString("Qkq"));
        assertEquals(0b1110, Castling.fromString("KQq"));
        assertEquals(0b1101, Castling.fromString("KQk"));

        assertEquals(0b1100, Castling.fromString("KQ"));
        assertEquals(0b1010, Castling.fromString("Qq"));
        assertEquals(0b0101, Castling.fromString("Kk"));
        assertEquals(0b0011, Castling.fromString("kq"));
        assertEquals(0b0010, Castling.fromString("q"));

        assertEquals(0b0000, Castling.fromString(""));
    }

    @Test
    void testToString() {
        assertEquals("KQkq", Castling.toString(Castling.ALL_ALLOWED));
        assertEquals("Kkq", Castling.toString(0b0111));
        assertEquals("Qkq", Castling.toString(0b1011));
        assertEquals("KQq", Castling.toString(0b1110));
        assertEquals("KQk", Castling.toString(0b1101));

        assertEquals("KQ", Castling.toString(0b1100));
        assertEquals("Qq", Castling.toString(0b1010));
        assertEquals("Kk", Castling.toString(0b0101));
        assertEquals("kq", Castling.toString(0b0011));
        assertEquals("q", Castling.toString(0b0010));

        assertEquals("-", Castling.toString(0b0000));
    }
}