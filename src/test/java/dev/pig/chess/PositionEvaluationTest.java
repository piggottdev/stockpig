package dev.pig.chess;

import org.junit.jupiter.api.Test;
import dev.pig.evaluation.ChessGameEvaluator;
import dev.pig.game.evaluation.GameEvaluator;
import dev.pig.game.evaluation.GameEvaluatorCounter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionEvaluationTest {

    @Test
    public void perftTests() {
        System.out.println("----- Starting PE-PERFTs -----");
        final long startTime = System.currentTimeMillis();
        mateInTwo_queenTrade().runMinmax().runAlphaBeta();
        mateInTwo_rookTrade().runMinmax().runAlphaBeta();
        mateInTwo_castle().runMinmax().runAlphaBeta();
        mateInThree().runMinmax().runAlphaBeta();
        //mateInFour().runAlphaBeta();
        final long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        System.out.println("----- Finished PE-PERFTs -----");
        System.out.println("All PE-PERFTs completed in " + elapsed + "s");
    }

    private ForcedMateEvaluationTest mateInTwo_queenTrade() {
        return forcedMateEvaluationTest("mateInTwo_queenTrade", "r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - - 0 1", 3, 12000);
    }

    private ForcedMateEvaluationTest mateInTwo_rookTrade() {
        return forcedMateEvaluationTest("mateInTwo_rookTrade", "kbK5/pp6/1P6/8/8/8/8/R7 w - - 0 1", 3, 12000);
    }

    private ForcedMateEvaluationTest mateInTwo_castle() {
        return forcedMateEvaluationTest("mateInTwo_castle", "8/8/8/2P3R1/5B2/2rP1p2/p1P1PP2/RnQ1K2k w Q - 0 1", 3, 12000);
    }

    private ForcedMateEvaluationTest mateInThree() {
        return forcedMateEvaluationTest("mateInThree", "r5rk/5p1p/5R2/4B3/8/8/7P/7K w - - 0 1", 5, 12000);
    }

    private ForcedMateEvaluationTest mateInFour() {
        return forcedMateEvaluationTest("mateInFour", "1KRr4/2P2PBP/PP2PNQ1/n7/3q2B1/4bb2/ppp1ppRp/1kr4r b - - 0 1", 7, -12000);
    }

    private static ForcedMateEvaluationTest forcedMateEvaluationTest(final String name, final String fen, final int depth, final int expectedScore) {
        return new ForcedMateEvaluationTest(name, fen, depth, expectedScore);
    }

    private static final class ForcedMateEvaluationTest {

        private final String name;
        private final String fen;
        private final int depth;
        private final int expectedScore;

        private ForcedMateEvaluationTest(final String name, final String fen, final int depth, final int expectedScore) {
            this.name = name;
            this.fen = fen;
            this.depth = depth;
            this.expectedScore = expectedScore;
        }

        private void run(final GameEvaluator<ChessGame> evaluator, final GameEvaluatorCounter<ChessGame> counter, final String algName) {
            final ChessGame game = ChessGame.fromFen(this.fen);
            final long startTime = System.currentTimeMillis();
            final int score = evaluator.evaluate(game);
            final long elapsed = System.currentTimeMillis() - startTime;
            assertEquals(this.expectedScore, score);
            System.out.println("PE-PERFT '" + this.name + "' success: " +
                    algName + "=[nodes=" + counter.getCount() + ", elapsed=" + elapsed + "ms]");
        }

        private ForcedMateEvaluationTest runAlphaBeta() {
            final GameEvaluatorCounter<ChessGame> abCounter = new GameEvaluatorCounter<>(ChessGameEvaluator.position());
            final GameEvaluator<ChessGame> abEvaluator = ChessGameEvaluator.alphaBeta(abCounter, this.depth);
            run(abEvaluator, abCounter, "alphaBe");
            return this;
        }

        private ForcedMateEvaluationTest runMinmax() {
            final GameEvaluatorCounter<ChessGame> mmCounter = new GameEvaluatorCounter<>(ChessGameEvaluator.position());
            final GameEvaluator<ChessGame> mmEvaluator = ChessGameEvaluator.minmax(mmCounter, this.depth);
            run(mmEvaluator, mmCounter, "min-max");
            return this;
        }
    }
}
