package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.notation.Fen;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Position tests, testing checkmates and stalemates.
 */
public final class PositionTest {

    @Test
    public void terminal() throws Fen.ParseException {
        final Position checkmate = Position.fromFen("k5qr/8/8/8/8/8/8/7K w k - 0 1");
        assertTrue(checkmate.isGameOver());
        assertTrue(checkmate.isCheck());
        assertTrue(checkmate.isCheckmate());
        assertFalse(checkmate.isDeadPosition());

        final Position stalemate = Position.fromFen("k5q1/8/8/1r6/8/8/8/K7 w k - 1 2");
        assertTrue(stalemate.isGameOver());
        assertFalse(stalemate.isCheck());
        assertFalse(stalemate.isCheckmate());
        assertFalse(stalemate.isDeadPosition());

        final Position check = Position.fromFen("k6r/8/8/8/8/8/8/7K w k - 0 1");
        assertFalse(check.isGameOver());
        assertTrue(check.isCheck());
        assertFalse(check.isCheckmate());
        assertFalse(check.isDeadPosition());

        final Position dead = Position.fromFen("k7/8/8/8/8/8/7K/8 b - - 0 2");
        assertTrue(dead.isGameOver());
        assertFalse(dead.isCheck());
        assertFalse(dead.isCheckmate());
        assertTrue(dead.isDeadPosition());

        final Position stale = Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 50 1");
        assertTrue(stale.isGameOver());
        assertFalse(stale.isCheck());
        assertFalse(stale.isCheckmate());
        assertFalse(stale.isDeadPosition());
    }
}
