package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Move tests, only tests encoding not application to a position.
 */
public final class MoveTest {

    @Test
    public void basic() {
        final int m = Move.basic(Square.E2, Square.E4, PieceType.PAWN);

        assertEquals(Square.E2, Move.from(m));
        assertEquals(Square.E4, Move.to(m));
        assertEquals(PieceType.PAWN, Move.mover(m));
        assertEquals(PieceType.EMPTY, Move.capture(m));
        assertEquals(PieceType.EMPTY, Move.promote(m));
        assertFalse(Move.isDoublePush(m));
        assertFalse(Move.isCastle(m));
        assertFalse(Move.isEnPassant(m));
        assertFalse(Move.isCapture(m));
        assertFalse(Move.isPromotion(m));
    }

    @Test
    public void capture() {
        final int m = Move.capture(Square.G5, Square.F7, PieceType.BISHOP, PieceType.PAWN);

        assertEquals(Square.G5, Move.from(m));
        assertEquals(Square.F7, Move.to(m));
        assertEquals(PieceType.BISHOP, Move.mover(m));
        assertEquals(PieceType.PAWN, Move.capture(m));
        assertEquals(PieceType.EMPTY, Move.promote(m));
        assertFalse(Move.isDoublePush(m));
        assertFalse(Move.isCastle(m));
        assertFalse(Move.isEnPassant(m));
        assertTrue(Move.isCapture(m));
        assertFalse(Move.isPromotion(m));
    }

    @Test
    public void promotion() {
        final int m = Move.addPromotion(Move.basic(Square.E7, Square.E8, PieceType.PAWN), PieceType.QUEEN);

        assertEquals(Square.E7, Move.from(m));
        assertEquals(Square.E8, Move.to(m));
        assertEquals(PieceType.PAWN, Move.mover(m));
        assertEquals(PieceType.EMPTY, Move.capture(m));
        assertEquals(PieceType.QUEEN, Move.promote(m));
        assertFalse(Move.isDoublePush(m));
        assertFalse(Move.isCastle(m));
        assertFalse(Move.isEnPassant(m));
        assertFalse(Move.isCapture(m));
        assertTrue(Move.isPromotion(m));
    }

    @Test
    public void capturePromotion() {
        final int m = Move.addPromotion(Move.capture(Square.E7, Square.E8, PieceType.PAWN, PieceType.ROOK), PieceType.QUEEN);

        assertEquals(Square.E7, Move.from(m));
        assertEquals(Square.E8, Move.to(m));
        assertEquals(PieceType.PAWN, Move.mover(m));
        assertEquals(PieceType.ROOK, Move.capture(m));
        assertEquals(PieceType.QUEEN, Move.promote(m));
        assertFalse(Move.isDoublePush(m));
        assertFalse(Move.isCastle(m));
        assertFalse(Move.isEnPassant(m));
        assertTrue(Move.isCapture(m));
        assertTrue(Move.isPromotion(m));
    }

    @Test
    public void doublePush() {
        final int m = Move.doublePush(Square.E2, Square.E4);

        assertEquals(Square.E2, Move.from(m));
        assertEquals(Square.E4, Move.to(m));
        assertEquals(PieceType.PAWN, Move.mover(m));
        assertEquals(PieceType.EMPTY, Move.capture(m));
        assertEquals(PieceType.EMPTY, Move.promote(m));
        assertTrue(Move.isDoublePush(m));
        assertFalse(Move.isCastle(m));
        assertFalse(Move.isEnPassant(m));
        assertFalse(Move.isCapture(m));
        assertFalse(Move.isPromotion(m));
    }

    @Test
    public void castle() {
        final int m = Move.castle(Square.E1, Square.G1);

        assertEquals(Square.E1, Move.from(m));
        assertEquals(Square.G1, Move.to(m));
        assertEquals(PieceType.KING, Move.mover(m));
        assertEquals(PieceType.EMPTY, Move.capture(m));
        assertEquals(PieceType.EMPTY, Move.promote(m));
        assertFalse(Move.isDoublePush(m));
        assertTrue(Move.isCastle(m));
        assertFalse(Move.isEnPassant(m));
        assertFalse(Move.isCapture(m));
        assertFalse(Move.isPromotion(m));
    }

    @Test
    public void enPassant() {
        final int m = Move.enPassant(Square.E5, Square.D6);

        assertEquals(Square.E5, Move.from(m));
        assertEquals(Square.D6, Move.to(m));
        assertEquals(PieceType.PAWN, Move.mover(m));
        assertEquals(PieceType.PAWN, Move.capture(m));
        assertEquals(PieceType.EMPTY, Move.promote(m));
        assertFalse(Move.isDoublePush(m));
        assertFalse(Move.isCastle(m));
        assertTrue(Move.isEnPassant(m));
        assertTrue(Move.isCapture(m));
        assertFalse(Move.isPromotion(m));
    }
}
