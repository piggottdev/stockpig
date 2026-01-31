package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Square;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests for board representation and queries.
 * Attempts to only test API to allow implementation changes in the future.
 */
public final class BoardTest {

    @Test
    public void addAndRemovePiece() {
        final Board board = Board.empty();

        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.A2);
        assertEquals(Square.A2.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.A2));
        assertEquals(Bitboard.EMPTY ^ Square.A2.bitboard(), board.occupied());
        assertEquals(Bitboard.ALL ^ Square.A2.bitboard(), board.unoccupied());

        board.removePiece(Colour.WHITE, PieceType.PAWN, Square.A2);
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.EMPTY, board.occupied());
        assertEquals(Bitboard.ALL, board.unoccupied());
    }

    @Test
    public void makeAndUnmake_Basic() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.A2);
        final int move = Move.basic(Square.A2, Square.A3, PieceType.PAWN);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Square.A3.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.A3));
        assertEquals(Bitboard.EMPTY ^ Square.A3.bitboard(), board.occupied());
        assertEquals(Bitboard.ALL ^ Square.A3.bitboard(), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Square.A2.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.A2));
        assertEquals(Bitboard.EMPTY ^ Square.A2.bitboard(), board.occupied());
        assertEquals(Bitboard.ALL ^ Square.A2.bitboard(), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_Capture() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.A2);
        board.addPiece(Colour.BLACK, PieceType.PAWN, Square.B3);
        final int move = Move.capture(Square.A2, Square.B3, PieceType.PAWN, PieceType.PAWN);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Square.B3.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.B3));
        assertEquals(Bitboard.EMPTY ^ Square.B3.bitboard(), board.occupied());
        assertEquals(Bitboard.ALL ^ Square.B3.bitboard(), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Square.A2.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Square.B3.bitboard(), board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.A2));
        assertEquals(PieceType.PAWN, board.pieceType(Square.B3));
        assertEquals(Bitboard.EMPTY ^ (Square.A2.bitboard()|Square.B3.bitboard()), board.occupied());
        assertEquals(Bitboard.ALL ^ (Square.A2.bitboard()|Square.B3.bitboard()), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_Promotion() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.A7);
        final int move = Move.addPromotion(Move.basic(Square.A7, Square.A8, PieceType.PAWN), PieceType.QUEEN);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Square.A8.bitboard(), board.pieces(Colour.WHITE, PieceType.QUEEN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.QUEEN, board.pieceType(Square.A8));
        assertEquals(Bitboard.EMPTY ^ Square.A8.bitboard(), board.occupied());
        assertEquals(Bitboard.ALL ^ Square.A8.bitboard(), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Square.A7.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.WHITE, PieceType.QUEEN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.A7));
        assertEquals(Bitboard.EMPTY ^ Square.A7.bitboard(), board.occupied());
        assertEquals(Bitboard.ALL ^ Square.A7.bitboard(), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_Castle() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.WHITE, PieceType.ROOK, Square.H1);
        final int move = Move.castle(Square.E1, Square.G1);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Square.G1.bitboard(), board.pieces(Colour.WHITE, PieceType.KING));
        assertEquals(Square.F1.bitboard(), board.pieces(Colour.WHITE, PieceType.ROOK));
        assertEquals(PieceType.KING, board.pieceType(Square.G1));
        assertEquals(PieceType.ROOK, board.pieceType(Square.F1));
        assertEquals(Bitboard.EMPTY ^ (Bitboard.of(Square.F1, Square.G1)), board.occupied());
        assertEquals(Bitboard.ALL ^ (Bitboard.of(Square.F1, Square.G1)), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Square.E1.bitboard(), board.pieces(Colour.WHITE, PieceType.KING));
        assertEquals(Square.H1.bitboard(), board.pieces(Colour.WHITE, PieceType.ROOK));
        assertEquals(PieceType.KING, board.pieceType(Square.E1));
        assertEquals(PieceType.ROOK, board.pieceType(Square.H1));
        assertEquals(Bitboard.EMPTY ^ (Bitboard.of(Square.E1, Square.H1)), board.occupied());
        assertEquals(Bitboard.ALL ^ (Bitboard.of(Square.E1, Square.H1)), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_EnPassant() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.E5);
        board.addPiece(Colour.BLACK, PieceType.PAWN, Square.D5);
        final int move = Move.enPassant(Square.E5, Square.D6);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Square.D6.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.D6));
        assertEquals(Bitboard.EMPTY ^ Square.D6.bitboard(), board.occupied());
        assertEquals(Bitboard.ALL ^ Square.D6.bitboard(), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Square.E5.bitboard(), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Square.D5.bitboard(), board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceType(Square.E5));
        assertEquals(PieceType.PAWN, board.pieceType(Square.D5));
        assertEquals(Bitboard.EMPTY ^ (Bitboard.of(Square.E5, Square.D5)), board.occupied());
        assertEquals(Bitboard.ALL ^ (Bitboard.of(Square.E5, Square.D5)), board.unoccupied());
    }

    @Test
    public void isDeadPosition_TwoKings() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        assertTrue(board.isDeadPosition());
    }

    @Test
    public void isDeadPosition_KingVsKingKnight() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        board.addPiece(Colour.WHITE, PieceType.KNIGHT, Square.B1);
        assertTrue(board.isDeadPosition());
    }

    @Test
    public void isDeadPosition_KingKnightVsKingKnight() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        board.addPiece(Colour.WHITE, PieceType.KNIGHT, Square.B1);
        board.addPiece(Colour.BLACK, PieceType.KNIGHT, Square.B8);
        assertFalse(board.isDeadPosition());
    }

    @Test
    public void isDeadPosition_KingVsKingBishop() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.WHITE, PieceType.BISHOP, Square.C1);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        assertTrue(board.isDeadPosition());
    }

    @Test
    public void isDeadPosition_KingBishopVsKingBishop_SameColour() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.WHITE, PieceType.BISHOP, Square.C1);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        board.addPiece(Colour.BLACK, PieceType.BISHOP, Square.C7);
        assertTrue(board.isDeadPosition());
    }

    @Test
    public void isDeadPosition_KingBishopBishopVsKing_SameColour() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.WHITE, PieceType.BISHOP, Square.C1);
        board.addPiece(Colour.WHITE, PieceType.BISHOP, Square.C7);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        assertFalse(board.isDeadPosition());
    }

    @Test
    public void isDeadPosition_KingBishopVsKingBishop_DifferentColour() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.WHITE, PieceType.BISHOP, Square.C1);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        board.addPiece(Colour.BLACK, PieceType.BISHOP, Square.C8);
        assertFalse(board.isDeadPosition());
    }

    @Test
    public void isDeadPosition_KingVsKingPawn() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.C2);
        board.addPiece(Colour.BLACK, PieceType.KING, Square.E8);
        assertFalse(board.isDeadPosition());
    }
}
