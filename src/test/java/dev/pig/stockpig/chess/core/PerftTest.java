package dev.pig.stockpig.chess.core;

import dev.pig.stockpig.chess.notation.Fen;
import dev.pig.stockpig.chess.perft.Suite;
import dev.pig.stockpig.chess.perft.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PERFT move enumeration tests.
 * These tests construct a position tree by making all legal moves up to a depth ply,
 * the amount of nodes is then compared against the known correct value to validate move generation.
 * By using a suite of selected test positions we can test almost the entire implementation.
 */
public final class PerftTest {

    @Test
    public void PerftSuite() throws Fen.ParseException {
        for (final TestCase test: Suite.TESTS) {
            final TestCase.Result result = TestCase.run(test);
            assertEquals(test.expectedNodes(), result.nodes(), test.name());
        }
    }

}
