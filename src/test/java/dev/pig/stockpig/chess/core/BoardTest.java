package dev.pig.stockpig.chess.core;

import dev.pig.stockpig.chess.core.bitboard.Bitboard;
import dev.pig.stockpig.chess.core.bitboard.Square;
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
        assertEquals(Bitboard.ofSquare(Square.A2), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.A2));
        assertEquals(Bitboard.EMPTY ^ Bitboard.ofSquare(Square.A2), board.occupied());
        assertEquals(Bitboard.ALL ^ Bitboard.ofSquare(Square.A2), board.unoccupied());

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
        assertEquals(Bitboard.ofSquare(Square.A3), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.A3));
        assertEquals(Bitboard.EMPTY ^ Bitboard.ofSquare(Square.A3), board.occupied());
        assertEquals(Bitboard.ALL ^ Bitboard.ofSquare(Square.A3), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.A2), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.A2));
        assertEquals(Bitboard.EMPTY ^ Bitboard.ofSquare(Square.A2), board.occupied());
        assertEquals(Bitboard.ALL ^ Bitboard.ofSquare(Square.A2), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_Capture() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.A2);
        board.addPiece(Colour.BLACK, PieceType.PAWN, Square.B3);
        final int move = Move.capture(Square.A2, Square.B3, PieceType.PAWN, PieceType.PAWN);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.B3), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.B3));
        assertEquals(Bitboard.EMPTY ^ Bitboard.ofSquare(Square.B3), board.occupied());
        assertEquals(Bitboard.ALL ^ Bitboard.ofSquare(Square.B3), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.A2), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.ofSquare(Square.B3), board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.A2));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.B3));
        assertEquals(Bitboard.EMPTY ^ (Bitboard.ofSquare(Square.A2)|Bitboard.ofSquare(Square.B3)), board.occupied());
        assertEquals(Bitboard.ALL ^ (Bitboard.ofSquare(Square.A2)|Bitboard.ofSquare(Square.B3)), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_Promotion() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.A7);
        final int move = Move.addPromotion(Move.basic(Square.A7, Square.A8, PieceType.PAWN), PieceType.QUEEN);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.A8), board.pieces(Colour.WHITE, PieceType.QUEEN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(PieceType.QUEEN, board.pieceAt(Square.A8));
        assertEquals(Bitboard.EMPTY ^ Bitboard.ofSquare(Square.A8), board.occupied());
        assertEquals(Bitboard.ALL ^ Bitboard.ofSquare(Square.A8), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.A7), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.WHITE, PieceType.QUEEN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.A7));
        assertEquals(Bitboard.EMPTY ^ Bitboard.ofSquare(Square.A7), board.occupied());
        assertEquals(Bitboard.ALL ^ Bitboard.ofSquare(Square.A7), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_Castle() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.KING, Square.E1);
        board.addPiece(Colour.WHITE, PieceType.ROOK, Square.H1);
        final int move = Move.castle(Square.E1, Square.G1);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.G1), board.pieces(Colour.WHITE, PieceType.KING));
        assertEquals(Bitboard.ofSquare(Square.F1), board.pieces(Colour.WHITE, PieceType.ROOK));
        assertEquals(PieceType.KING, board.pieceAt(Square.G1));
        assertEquals(PieceType.ROOK, board.pieceAt(Square.F1));
        assertEquals(Bitboard.EMPTY ^ (Bitboard.ofSquares(Square.F1, Square.G1)), board.occupied());
        assertEquals(Bitboard.ALL ^ (Bitboard.ofSquares(Square.F1, Square.G1)), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.E1), board.pieces(Colour.WHITE, PieceType.KING));
        assertEquals(Bitboard.ofSquare(Square.H1), board.pieces(Colour.WHITE, PieceType.ROOK));
        assertEquals(PieceType.KING, board.pieceAt(Square.E1));
        assertEquals(PieceType.ROOK, board.pieceAt(Square.H1));
        assertEquals(Bitboard.EMPTY ^ (Bitboard.ofSquares(Square.E1, Square.H1)), board.occupied());
        assertEquals(Bitboard.ALL ^ (Bitboard.ofSquares(Square.E1, Square.H1)), board.unoccupied());
    }

    @Test
    public void makeAndUnmake_EnPassant() {
        final Board board = Board.empty();
        board.addPiece(Colour.WHITE, PieceType.PAWN, Square.E5);
        board.addPiece(Colour.BLACK, PieceType.PAWN, Square.D5);
        final int move = Move.enPassant(Square.E5, Square.D6);

        board.makeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.D6), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.EMPTY, board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.D6));
        assertEquals(Bitboard.EMPTY ^ Bitboard.ofSquare(Square.D6), board.occupied());
        assertEquals(Bitboard.ALL ^ Bitboard.ofSquare(Square.D6), board.unoccupied());

        board.unmakeMove(Colour.WHITE, move);
        assertEquals(Bitboard.ofSquare(Square.E5), board.pieces(Colour.WHITE, PieceType.PAWN));
        assertEquals(Bitboard.ofSquare(Square.D5), board.pieces(Colour.BLACK, PieceType.PAWN));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.E5));
        assertEquals(PieceType.PAWN, board.pieceAt(Square.D5));
        assertEquals(Bitboard.EMPTY ^ (Bitboard.ofSquares(Square.E5, Square.D5)), board.occupied());
        assertEquals(Bitboard.ALL ^ (Bitboard.ofSquares(Square.E5, Square.D5)), board.unoccupied());
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
