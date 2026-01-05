package dev.pig.chess.game;

import dev.pig.chess.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ChessGameTest {

    @Test
    void standard() {
        final ChessGame game = ChessGame.standard();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", game.toFen());
        assertFalse(game.isGameOver());
        assertFalse(game.isCheckMate());
        assertEquals(0, game.getWinner());
        assertFalse(game.isCheck());
        assertTrue(game.isWhiteTurn());
        assertEquals(20, game.getLegalMoves().size());
        assertEquals(1, game.getTurnNumber());
        assertEquals(0, game.getTurnsSincePushOrCapture());
        assertEquals(0b1111, game.getCastlesPossible());
        assertEquals(Bitboard.EMPTY, game.getEnPassantTarget());
        assertEquals(Bitboard.RANKS[5] | Bitboard.RANKS[6] | Bitboard.RANKS[7] ^ (Bitboard.INDEX[63] | Bitboard.INDEX[56]), game.getThreatenedSquares());
        assertEquals(Bitboard.ALL ^ (Bitboard.RANKS[0] | Bitboard.RANKS[1]), game.getMovableSquares());
        assertEquals(Bitboard.EMPTY, game.getPinSquares());
        assertEquals(Bitboard.RANKS[1], game.getPieceBitboard(Piece.WHITE | Piece.PAWN));
        assertEquals(Piece.WHITE | Piece.ROOK, game.getPieceAtAlgebraNotation("a1"));
    }

    @Test
    void isGameOver() {
        assertTrue(Fen.toGame("k5qr/8/8/8/8/8/8/7K w k - 0 1").isGameOver());
        assertTrue(Fen.toGame("k7/8/8/8/8/8/8/QR5K b - - 0 1").isGameOver());
        assertTrue(Fen.toGame("k5r1/8/8/8/8/8/r7/7K w - - 0 1").isGameOver());
        assertTrue(Fen.toGame("k7/8/8/3n4/8/8/8/7K w - - 0 1").isGameOver()); //Dead
        assertFalse(Fen.toGame("k7/8/8/3n4/8/8/8/3R3K w - - 0 1").isGameOver());
    }

    @Test
    void isCheckMate() {
        assertTrue(Fen.toGame("k5qr/8/8/8/8/8/8/7K w k - 0 1").isCheckMate());
        assertTrue(Fen.toGame("k7/8/8/8/8/8/8/QR5K b - - 0 1").isCheckMate());
        assertFalse(Fen.toGame("k6r/8/8/8/8/8/8/7K w k - 0 1").isCheckMate());
        assertFalse(Fen.toGame("k5r1/8/8/8/8/8/r7/7K w - - 0 1").isCheckMate());
    }

    @Test
    void getWinner() {
        assertEquals(-1, Fen.toGame("k5qr/8/8/8/8/8/8/7K w k - 0 1").getWinner());
        assertEquals(1, Fen.toGame("k7/8/8/8/8/8/8/QR5K b - - 0 1").getWinner());
        assertEquals(0, Fen.toGame("k5r1/8/8/8/8/8/r7/7K w - - 0 1").getWinner());
        assertEquals(0, Fen.toGame("k7/8/8/8/8/8/8/7K w - - 0 1").getWinner());
        assertEquals(0, Fen.toGame("k1r5/8/8/8/8/8/8/3R3K w - - 0 1").getWinner());
    }

    @Test
    void applyAndUndoMove() {
        final ChessGame game = ChessGame.standard();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", game.toFen());
        game.applyMove(ChessMove.doublePush(AlgebraNotation.toBitboard("e2"), AlgebraNotation.toBitboard("e4"), Piece.WHITE | Piece.PAWN, AlgebraNotation.toBitboard("e3")));
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", game.toFen());
        game.applyMove(ChessMove.doublePush(AlgebraNotation.toBitboard("c7"), AlgebraNotation.toBitboard("c5"), Piece.BLACK | Piece.PAWN, AlgebraNotation.toBitboard("c6")));
        assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2", game.toFen());
        game.applyMove(ChessMove.basicMove(Bitboard.INDEX[6], AlgebraNotation.toBitboard("f3"), Piece.KNIGHT | Piece.WHITE));
        assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2", game.toFen());
        game.undoMove();
        assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2", game.toFen());
        game.undoMove();
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", game.toFen());
        game.undoMove();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", game.toFen());
    }

    @Test
    void debugString() {
        final ChessGame game = ChessGame.fromFen("k5qr/8/8/8/8/8/8/7K w k - 0 1");
        assertEquals("+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "| k |   |   |   |   |   | q | r |\n" +
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
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "|   |   |   |   |   |   |   | K |\n" +
                "|   |   |   |   |   |   |   |   |\n" +
                "+---+---+---+---+---+---+---+---+\n" +
                "\n" +
                "\tWinner: -1\n" +
                "\tCheck\n" +
                "\tWhite Turn\n" +
                "\t\tCastles: k\n" +
                "\t\tEnPassant Target: -\n" +
                "\t\tTurns Since Push/Cap: 0\n" +
                "\t\tTurn Number: 1", game.debugString()
        );
    }
}