package dev.pig.chess.game;

import dev.pig.chess.Bitboard;
import dev.pig.chess.game.Board;
import dev.pig.chess.ChessMove;
import dev.pig.chess.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void addPiece() {
        final Board board = Board.empty();
        board.addPiece(Piece.WHITE | Piece.QUEEN, Bitboard.INDEX[4]);
        board.addPiece(Piece.BLACK | Piece.PAWN, Bitboard.RANKS[5]);

        assertEquals(Bitboard.INDEX[4], board.getPieceBitboard(Piece.WHITE));
        assertEquals(Bitboard.INDEX[4], board.getPieceBitboard(Piece.WHITE | Piece.QUEEN));

        assertEquals(Bitboard.RANKS[5], board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.RANKS[5], board.getPieceBitboard(Piece.BLACK | Piece.PAWN));

        assertEquals(Bitboard.ALL ^ (Bitboard.RANKS[5] | Bitboard.INDEX[4]), board.getPieceBitboard(Piece.UNOCCUPIED));
    }

    @Test
    void removePiece() {
        final Board board = Board.standard();
        board.removePiece(Piece.BLACK | Piece.PAWN, Bitboard.RANKS[6]);
        board.removePiece(Piece.WHITE | Piece.PAWN, Bitboard.RANKS[1]);

        assertEquals(Bitboard.RANKS[0], board.getPieceBitboard(Piece.WHITE));
        assertEquals(0L, board.getPieceBitboard(Piece.WHITE | Piece.PAWN));

        assertEquals(Bitboard.RANKS[7], board.getPieceBitboard(Piece.BLACK));
        assertEquals(0L, board.getPieceBitboard(Piece.BLACK | Piece.PAWN));

        assertEquals(Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[7]), board.getPieceBitboard(Piece.UNOCCUPIED));
    }

    @Test
    void applyMove_basicMove() {
        final Board board = Board.standard();
        final ChessMove move = ChessMove.basicMove(Bitboard.INDEX[10], Bitboard.INDEX[18], Piece.WHITE | Piece.PAWN);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[10]) | Bitboard.RANKS[0] | Bitboard.INDEX[18], board.getPieceBitboard(Piece.WHITE));
        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[10]) | Bitboard.INDEX[18], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));

        assertEquals(Bitboard.RANKS[6] | Bitboard.RANKS[7], board.getPieceBitboard(Piece.BLACK));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[10] | Bitboard.INDEX[18]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_basicCapture() {
        final Board board = Board.standard();
        final ChessMove move = ChessMove.basicCapture(Bitboard.INDEX[10], Bitboard.INDEX[53], Piece.WHITE | Piece.PAWN, Piece.BLACK | Piece.PAWN);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[10]) | Bitboard.RANKS[0] | Bitboard.INDEX[53], board.getPieceBitboard(Piece.WHITE));
        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[10]) | Bitboard.INDEX[53], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));

        assertEquals((Bitboard.RANKS[6] | Bitboard.RANKS[7]) ^ Bitboard.INDEX[53], board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.RANKS[6] ^ Bitboard.INDEX[53], board.getPieceBitboard(Piece.BLACK | Piece.PAWN));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ Bitboard.INDEX[10], board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_pawnPromotion() {
        final Board board = Board.standard();
        board.removePiece(Piece.ROOK, Bitboard.INDEX[56]);
        board.removePiece(Piece.PAWN, Bitboard.INDEX[48]);
        board.removePiece(Piece.WHITE | Piece.PAWN, Bitboard.INDEX[8]);
        board.addPiece(Piece.WHITE | Piece.PAWN, Bitboard.INDEX[48]);
        final ChessMove move = ChessMove.pawnPromotion(Bitboard.INDEX[48], Bitboard.INDEX[56], Piece.WHITE | Piece.PAWN, Piece.WHITE | Piece.QUEEN);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[8]) | Bitboard.RANKS[0] | Bitboard.INDEX[56], board.getPieceBitboard(Piece.WHITE));
        assertEquals(Bitboard.RANKS[1] ^ Bitboard.INDEX[8], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));
        assertEquals(Bitboard.INDEX[3] | Bitboard.INDEX[56], board.getPieceBitboard(Piece.WHITE | Piece.QUEEN));

        assertEquals((Bitboard.RANKS[6] | Bitboard.RANKS[7]) ^ (Bitboard.INDEX[56] | Bitboard.INDEX[48]), board.getPieceBitboard(Piece.BLACK));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[8] | Bitboard.INDEX[48]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_pawnPromotionWithCapture() {
        final Board board = Board.standard();
        board.removePiece(Piece.PAWN, Bitboard.INDEX[48]);
        board.removePiece(Piece.WHITE | Piece.PAWN, Bitboard.INDEX[8]);
        board.addPiece(Piece.WHITE | Piece.PAWN, Bitboard.INDEX[48]);
        final ChessMove move = ChessMove.pawnPromotionWithCapture(Bitboard.INDEX[48], Bitboard.INDEX[57], Piece.WHITE | Piece.PAWN, Piece.BLACK | Piece.KNIGHT, Piece.WHITE | Piece.ROOK);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[8]) | Bitboard.RANKS[0] | Bitboard.INDEX[57], board.getPieceBitboard(Piece.WHITE));
        assertEquals(Bitboard.RANKS[1] ^ Bitboard.INDEX[8], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));
        assertEquals(Bitboard.INDEX[0] | Bitboard.INDEX[7] | Bitboard.INDEX[57], board.getPieceBitboard(Piece.WHITE | Piece.ROOK));

        assertEquals((Bitboard.RANKS[7] ^ Bitboard.INDEX[57]) | (Bitboard.RANKS[6] ^ Bitboard.INDEX[48]), board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.INDEX[62], board.getPieceBitboard(Piece.BLACK | Piece.KNIGHT));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[8] | Bitboard.INDEX[48]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_enPassantCapture() {
        final Board board = Board.standard();
        board.removePiece(Piece.PAWN, Bitboard.INDEX[49]);
        board.addPiece(Piece.PAWN, Bitboard.INDEX[33]);
        board.removePiece(Piece.WHITE | Piece.PAWN, Bitboard.INDEX[8]);
        board.addPiece(Piece.WHITE | Piece.PAWN, Bitboard.INDEX[32]);
        final ChessMove move = ChessMove.enPassantCapture(Bitboard.INDEX[32], Bitboard.INDEX[41], Piece.WHITE | Piece.PAWN, Piece.BLACK | Piece.PAWN, Bitboard.INDEX[33]);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[8]) | Bitboard.RANKS[0] | Bitboard.INDEX[41], board.getPieceBitboard(Piece.WHITE));
        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[8]) | Bitboard.INDEX[41], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));

        assertEquals(Bitboard.RANKS[7] | (Bitboard.RANKS[6] ^ Bitboard.INDEX[49]), board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.RANKS[6] ^ Bitboard.INDEX[49], board.getPieceBitboard(Piece.BLACK | Piece.PAWN));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[8] | Bitboard.INDEX[49] | Bitboard.INDEX[41]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_doublePush() {
        final Board board = Board.standard();
        final ChessMove move = ChessMove.doublePush(Bitboard.INDEX[8], Bitboard.INDEX[24], Piece.WHITE | Piece.PAWN, Bitboard.INDEX[16]);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[8]) | Bitboard.RANKS[0] | Bitboard.INDEX[24], board.getPieceBitboard(Piece.WHITE));
        assertEquals((Bitboard.RANKS[1] ^ Bitboard.INDEX[8]) | Bitboard.INDEX[24], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));

        assertEquals(Bitboard.RANKS[7] | Bitboard.RANKS[6], board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.RANKS[6], board.getPieceBitboard(Piece.BLACK | Piece.PAWN));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[8] | Bitboard.INDEX[24]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_castle_white_kingSide() {
        final Board board = Board.standard();
        board.removePiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[5]);
        board.removePiece(Piece.WHITE | Piece.KNIGHT, Bitboard.INDEX[6]);
        final ChessMove move = ChessMove.castle(Bitboard.INDEX[4], Bitboard.INDEX[6], Piece.WHITE | Piece.KING, Bitboard.INDEX[5] | Bitboard.INDEX[7]);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals(Bitboard.RANKS[1] | Bitboard.RANKS[0] ^ (Bitboard.INDEX[4] | Bitboard.INDEX[7]), board.getPieceBitboard(Piece.WHITE));
        assertEquals(Bitboard.INDEX[6], board.getPieceBitboard(Piece.WHITE | Piece.KING));
        assertEquals(Bitboard.INDEX[0] | Bitboard.INDEX[5], board.getPieceBitboard(Piece.WHITE | Piece.ROOK));

        assertEquals(Bitboard.RANKS[7] | Bitboard.RANKS[6], board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.RANKS[6], board.getPieceBitboard(Piece.BLACK | Piece.PAWN));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[7] | Bitboard.INDEX[4]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_castle_white_queenSide() {
        final Board board = Board.standard();
        board.removePiece(Piece.WHITE | Piece.KNIGHT, Bitboard.INDEX[1]);
        board.removePiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[2]);
        board.removePiece(Piece.WHITE | Piece.QUEEN, Bitboard.INDEX[3]);
        final ChessMove move = ChessMove.castle(Bitboard.INDEX[4], Bitboard.INDEX[2], Piece.WHITE | Piece.KING, Bitboard.INDEX[0] | Bitboard.INDEX[3]);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals(Bitboard.RANKS[1] | Bitboard.RANKS[0] ^ (Bitboard.INDEX[0] | Bitboard.INDEX[1] | Bitboard.INDEX[4]), board.getPieceBitboard(Piece.WHITE));
        assertEquals(Bitboard.INDEX[2], board.getPieceBitboard(Piece.WHITE | Piece.KING));
        assertEquals(Bitboard.INDEX[3] | Bitboard.INDEX[7], board.getPieceBitboard(Piece.WHITE | Piece.ROOK));

        assertEquals(Bitboard.RANKS[7] | Bitboard.RANKS[6], board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.RANKS[6], board.getPieceBitboard(Piece.BLACK | Piece.PAWN));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[0] | Bitboard.INDEX[1] | Bitboard.INDEX[4]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_castle_black_kingSide() {
        final Board board = Board.standard();
        board.removePiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[61]);
        board.removePiece(Piece.BLACK | Piece.KNIGHT, Bitboard.INDEX[62]);
        final ChessMove move = ChessMove.castle(Bitboard.INDEX[60], Bitboard.INDEX[62], Piece.BLACK | Piece.KING, Bitboard.INDEX[61] | Bitboard.INDEX[63]);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals(Bitboard.RANKS[1] | Bitboard.RANKS[0], board.getPieceBitboard(Piece.WHITE));
        assertEquals(Bitboard.RANKS[1], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));

        assertEquals((Bitboard.RANKS[7] | Bitboard.RANKS[6]) ^ (Bitboard.INDEX[63] | Bitboard.INDEX[60]), board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.INDEX[62], board.getPieceBitboard(Piece.BLACK | Piece.KING));
        assertEquals(Bitboard.INDEX[56] | Bitboard.INDEX[61], board.getPieceBitboard(Piece.BLACK | Piece.ROOK));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[63] | Bitboard.INDEX[60]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void applyMove_castle_black_queenSide() {
        final Board board = Board.standard();
        board.removePiece(Piece.BLACK | Piece.KNIGHT, Bitboard.INDEX[57]);
        board.removePiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[58]);
        board.removePiece(Piece.BLACK | Piece.QUEEN, Bitboard.INDEX[59]);
        final ChessMove move = ChessMove.castle(Bitboard.INDEX[60], Bitboard.INDEX[58], Piece.BLACK | Piece.KING, Bitboard.INDEX[56] | Bitboard.INDEX[59]);
        final String beforeString = board.debugString();

        board.applyMove(move);

        assertEquals(Bitboard.RANKS[0] | Bitboard.RANKS[1], board.getPieceBitboard(Piece.WHITE));
        assertEquals(Bitboard.RANKS[1], board.getPieceBitboard(Piece.WHITE | Piece.PAWN));

        assertEquals((Bitboard.RANKS[6] | Bitboard.RANKS[7]) ^ (Bitboard.INDEX[56] | Bitboard.INDEX[57] | Bitboard.INDEX[60]), board.getPieceBitboard(Piece.BLACK));
        assertEquals(Bitboard.INDEX[58], board.getPieceBitboard(Piece.BLACK | Piece.KING));
        assertEquals(Bitboard.INDEX[63] | Bitboard.INDEX[59], board.getPieceBitboard(Piece.BLACK | Piece.ROOK));

        assertEquals((Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1] | Bitboard.RANKS[6] | Bitboard.RANKS[7])) ^ (Bitboard.INDEX[56] | Bitboard.INDEX[57] | Bitboard.INDEX[60]), board.getPieceBitboard(Piece.UNOCCUPIED));

        board.undoMove(move);
        assertEquals(beforeString, board.debugString());
    }

    @Test
    void getPieceAtBit() {
        final Board board = Board.standard();
        assertEquals(Piece.WHITE | Piece.ROOK, board.getPieceAtBit(Bitboard.INDEX[0]));
        assertEquals(Piece.WHITE | Piece.KING, board.getPieceAtBit(Bitboard.INDEX[4]));
        assertEquals(Piece.WHITE | Piece.PAWN, board.getPieceAtBit(Bitboard.INDEX[15]));
        assertEquals(Piece.WHITE | Piece.QUEEN, board.getPieceAtBit(Bitboard.INDEX[3]));

        assertEquals(Piece.BLACK | Piece.ROOK, board.getPieceAtBit(Bitboard.INDEX[63]));
        assertEquals(Piece.BLACK | Piece.KING, board.getPieceAtBit(Bitboard.INDEX[60]));
        assertEquals(Piece.BLACK | Piece.PAWN, board.getPieceAtBit(Bitboard.INDEX[54]));
        assertEquals(Piece.BLACK | Piece.QUEEN, board.getPieceAtBit(Bitboard.INDEX[59]));

        assertEquals(Piece.UNOCCUPIED, board.getPieceAtBit(Bitboard.INDEX[43]));
        assertEquals(Piece.UNOCCUPIED, board.getPieceAtBit(Bitboard.INDEX[25]));
    }

    @Test
    void testGetPieceAtBitFromTeam() {
        final Board board = Board.standard();
        assertEquals(Piece.WHITE | Piece.ROOK, board.getPieceAtBit(Bitboard.INDEX[0]));
        assertEquals(Piece.WHITE | Piece.KING, board.getPieceAtBit(Bitboard.INDEX[4]));
        assertEquals(Piece.WHITE | Piece.PAWN, board.getPieceAtBit(Bitboard.INDEX[15]));
        assertEquals(Piece.WHITE | Piece.QUEEN, board.getPieceAtBit(Bitboard.INDEX[3]));

        assertEquals(Piece.BLACK | Piece.ROOK, board.getPieceAtBit(Bitboard.INDEX[63]));
        assertEquals(Piece.BLACK | Piece.KING, board.getPieceAtBit(Bitboard.INDEX[60]));
        assertEquals(Piece.BLACK | Piece.PAWN, board.getPieceAtBit(Bitboard.INDEX[54]));
        assertEquals(Piece.BLACK | Piece.QUEEN, board.getPieceAtBit(Bitboard.INDEX[59]));

        assertEquals(Piece.UNOCCUPIED, board.getPieceAtBit(Bitboard.INDEX[43]));
        assertEquals(Piece.UNOCCUPIED, board.getPieceAtBit(Bitboard.INDEX[25]));
    }

    @Test
    void isDeadPosition() {
        final Board board = Board.empty();

        // Just Kings
        board.addPiece(Piece.WHITE | Piece.KING, Bitboard.INDEX[0]);
        board.addPiece(Piece.BLACK | Piece.KING, Bitboard.INDEX[2]);
        assertTrue(board.isDeadPosition());

        // King + Knight vs King
        board.addPiece(Piece.WHITE | Piece.KNIGHT, Bitboard.INDEX[10]);
        assertTrue(board.isDeadPosition());
        board.removePiece(Piece.WHITE | Piece.KNIGHT, Bitboard.INDEX[10]);
        board.addPiece(Piece.BLACK | Piece.KNIGHT, Bitboard.INDEX[10]);
        assertTrue(board.isDeadPosition());
        board.removePiece(Piece.BLACK | Piece.KNIGHT, Bitboard.INDEX[10]);

        // King + Bishop vs King
        board.addPiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[10]);
        assertTrue(board.isDeadPosition());
        board.removePiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[10]);
        board.addPiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[10]);
        assertTrue(board.isDeadPosition());
        board.removePiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[10]);

        // King + Knight vs King + Knight
        board.addPiece(Piece.WHITE | Piece.KNIGHT, Bitboard.INDEX[10]);
        board.addPiece(Piece.BLACK | Piece.KNIGHT, Bitboard.INDEX[11]);
        assertFalse(board.isDeadPosition());
        board.removePiece(Piece.BLACK | Piece.KNIGHT, Bitboard.INDEX[11]);
        board.removePiece(Piece.WHITE | Piece.KNIGHT, Bitboard.INDEX[10]);

        // King + Bishop vs King + Bishop (same colour)
        board.addPiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[7]);
        board.addPiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[23]);
        assertTrue(board.isDeadPosition());
        board.removePiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[23]);
        board.removePiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[7]);

        // King + Bishop vs King + Bishop (different colour)
        board.addPiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[7]);
        board.addPiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[15]);
        assertFalse(board.isDeadPosition());
        board.removePiece(Piece.BLACK | Piece.BISHOP, Bitboard.INDEX[15]);
        board.removePiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[7]);

        // King + Rook vs King
        board.addPiece(Piece.WHITE | Piece.ROOK, Bitboard.INDEX[7]);
        assertFalse(board.isDeadPosition());
        board.removePiece(Piece.WHITE | Piece.ROOK, Bitboard.INDEX[7]);

        // King + Rook vs King
        board.addPiece(Piece.BLACK | Piece.ROOK, Bitboard.INDEX[7]);
        assertFalse(board.isDeadPosition());
        board.removePiece(Piece.BLACK | Piece.ROOK, Bitboard.INDEX[7]);
    }

    @Test
    void debugString() {
        assertEquals(
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| r | n | b | q | k | b | n | r |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| p | p | p | p | p | p | p | p |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| P | P | P | P | P | P | P | P |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| R | N | B | Q | K | B | N | R |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n", Board.standard().debugString());
    }

}