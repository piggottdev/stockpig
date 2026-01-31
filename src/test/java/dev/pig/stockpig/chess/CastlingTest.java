package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests some basic castling functions, does not test castle moves being
 * applied to a position.
 */
public final class CastlingTest {

    @Test
    public void update_WhiteKing() {
        assertEquals(
                Castling.B_QUEEN_SIDE | Castling.B_KING_SIDE,
                Castling.update(Colour.WHITE, Castling.ALL, Move.basic(Square.E1, Square.G1, PieceType.KING))
        );
    }

    @Test
    public void update_BlackKing() {
        assertEquals(
                Castling.W_QUEEN_SIDE | Castling.W_KING_SIDE,
                Castling.update(Colour.BLACK, Castling.ALL, Move.basic(Square.E8, Square.G8, PieceType.KING))
        );
    }

    @Test
    public void update_WhiteQueenRook() {
        assertEquals(
                Castling.W_KING_SIDE | Castling.B_QUEEN_SIDE | Castling.B_KING_SIDE,
                Castling.update(Colour.WHITE, Castling.ALL, Move.basic(Square.A1, Square.A2, PieceType.ROOK))
        );
    }

    @Test
    public void update_WhiteKingRook() {
        assertEquals(
                Castling.W_QUEEN_SIDE | Castling.B_QUEEN_SIDE | Castling.B_KING_SIDE,
                Castling.update(Colour.WHITE, Castling.ALL, Move.basic(Square.H1, Square.H2, PieceType.ROOK))
        );
    }

    @Test
    public void update_BlackQueenRook() {
        assertEquals(
                Castling.W_QUEEN_SIDE | Castling.W_KING_SIDE | Castling.B_KING_SIDE,
                Castling.update(Colour.BLACK, Castling.ALL, Move.basic(Square.A8, Square.A7, PieceType.ROOK))
        );
    }

    @Test
    public void update_BlackKingRook() {
        assertEquals(
                Castling.W_QUEEN_SIDE | Castling.W_KING_SIDE | Castling.B_QUEEN_SIDE,
                Castling.update(Colour.BLACK, Castling.ALL, Move.basic(Square.H8, Square.H7, PieceType.ROOK))
        );
    }

    @Test
    public void update_WhiteTakesBlackQueenRook() {
        assertEquals(
                Castling.W_QUEEN_SIDE | Castling.W_KING_SIDE | Castling.B_KING_SIDE,
                Castling.update(Colour.WHITE, Castling.ALL, Move.capture(Square.A7, Square.A8, PieceType.QUEEN, PieceType.ROOK))
        );
    }

    @Test
    public void update_WhiteTakesBlackKingRook() {
        assertEquals(
                Castling.W_QUEEN_SIDE | Castling.W_KING_SIDE | Castling.B_QUEEN_SIDE,
                Castling.update(Colour.WHITE, Castling.ALL, Move.capture(Square.H7, Square.H8, PieceType.QUEEN, PieceType.ROOK))
        );
    }

    @Test
    public void update_BlackTakesWhiteQueenRook() {
        assertEquals(
                Castling.W_KING_SIDE | Castling.B_QUEEN_SIDE | Castling.B_KING_SIDE,
                Castling.update(Colour.BLACK, Castling.ALL, Move.capture(Square.A2, Square.A1, PieceType.QUEEN, PieceType.ROOK))
        );
    }

    @Test
    public void update_BlackTakesWhiteKingRook() {
        assertEquals(
                Castling.W_QUEEN_SIDE | Castling.B_QUEEN_SIDE | Castling.B_KING_SIDE,
                Castling.update(Colour.BLACK, Castling.ALL, Move.capture(Square.H2, Square.H1, PieceType.QUEEN, PieceType.ROOK))
        );
    }

    @Test
    public void isQueenSideAllowed_White() {
        final long empty = Castling.W_QUEEN_SIDE_EMPTY_SQUARES;
        final long attacked = Bitboard.EMPTY;

        // White queen side allowed
        assertTrue(Castling.isQueenSideAllowed(Colour.WHITE, Castling.W_QUEEN_SIDE, empty, attacked));
        // White rights missing
        assertFalse(Castling.isQueenSideAllowed(Colour.WHITE, Castling.W_KING_SIDE, empty, attacked));
        // Occupied squares block castle
        assertFalse(Castling.isQueenSideAllowed(Colour.WHITE, Castling.W_QUEEN_SIDE, Bitboard.EMPTY, attacked));
        // Squares under attack
        assertFalse(Castling.isQueenSideAllowed(Colour.WHITE, Castling.W_QUEEN_SIDE, empty, Castling.W_QUEEN_CHECK_SQUARES));
    }

    @Test
    public void isKingSideAllowed_White() {
        final long empty = Castling.W_KING_SIDE_EMPTY_SQUARES;
        final long attacked = Bitboard.EMPTY;

        // White king side allowed
        assertTrue(Castling.isKingSideAllowed(Colour.WHITE, Castling.W_KING_SIDE, empty, attacked));
        // White rights missing
        assertFalse(Castling.isKingSideAllowed(Colour.WHITE, Castling.W_QUEEN_SIDE, empty, attacked));
        // Occupied squares block castle
        assertFalse(Castling.isKingSideAllowed(Colour.WHITE, Castling.W_KING_SIDE, Bitboard.EMPTY, attacked));
        // Squares under attack
        assertFalse(Castling.isKingSideAllowed(Colour.WHITE, Castling.W_KING_SIDE, empty, Castling.W_KING_CHECK_SQUARES));
    }

    @Test
    public void isQueenSideAllowed_Black() {
        final long empty = Castling.B_QUEEN_SIDE_EMPTY_SQUARES;
        final long attacked = Bitboard.EMPTY;

        // Black queen side allowed
        assertTrue(Castling.isQueenSideAllowed(Colour.BLACK, Castling.B_QUEEN_SIDE, empty, attacked));
        // Black rights missing
        assertFalse(Castling.isQueenSideAllowed(Colour.BLACK, Castling.B_KING_SIDE, empty, attacked));
        // Occupied squares block castle
        assertFalse(Castling.isQueenSideAllowed(Colour.BLACK, Castling.B_QUEEN_SIDE, Bitboard.EMPTY, attacked));
        // Squares under attack
        assertFalse(Castling.isQueenSideAllowed(Colour.BLACK, Castling.B_QUEEN_SIDE, empty, Castling.B_QUEEN_CHECK_SQUARES));
    }

    @Test
    public void isKingSideAllowed_Black() {
        final long empty = Castling.B_KING_SIDE_EMPTY_SQUARES;
        final long attacked = Bitboard.EMPTY;

        // Black king side allowed
        assertTrue(Castling.isKingSideAllowed(Colour.BLACK, Castling.B_KING_SIDE, empty, attacked));
        // Black rights missing
        assertFalse(Castling.isKingSideAllowed(Colour.BLACK, Castling.B_QUEEN_SIDE, empty, attacked));
        // Occupied squares block castle
        assertFalse(Castling.isKingSideAllowed(Colour.BLACK, Castling.B_KING_SIDE, Bitboard.EMPTY, attacked));
        // Squares under attack
        assertFalse(Castling.isKingSideAllowed(Colour.BLACK, Castling.B_KING_SIDE, empty, Castling.B_KING_CHECK_SQUARES));
    }

}
