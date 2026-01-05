package dev.pig.chess;

import org.junit.jupiter.api.Test;
import dev.pig.evaluation.ChessGameEvaluator;
import dev.pig.search.ChessGameMoveSearcher;
import dev.pig.game.evaluation.GameEvaluatorCounter;
import dev.pig.game.search.CombinatorialGameMoveSearcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptimalMoveSearchTest {

    @Test
    public void perftTests() {
        System.out.println("----- Starting MS-PERFTs -----");
        final long startTime = System.currentTimeMillis();
        mateInTwo_queenTrade().runMinmax().runAlphaBeta();
        mateInTwo_rookTrade().runMinmax().runAlphaBeta();
        mateInTwo_castle().runMinmax().runAlphaBeta();
        mateInThree().runMinmax().runAlphaBeta();
        //mateInFour().runAlphaBeta();
        final long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        System.out.println("----- Finished MS-PERFTs -----");
        System.out.println("All MS-PERFTs completed in " + elapsed + "s");
    }

    private ForcedMateSearchTest mateInTwo_queenTrade() {
        return forcedMateSearchTest("mateInTwo_queenTrade",
                "r1bq2r1/b4pk1/p1pp1p2/1p2pP2/1P2P1PB/3P4/1PPQ2P1/R3K2R w - - 0 1",
                4,
                "d2h6");
    }

    private ForcedMateSearchTest mateInTwo_rookTrade() {
        return forcedMateSearchTest("mateInTwo_rookTrade",
                "kbK5/pp6/1P6/8/8/8/8/R7 w - - 0 1",
                4,
                "a1a6");
    }

    private ForcedMateSearchTest mateInTwo_castle() {
        return forcedMateSearchTest("mateInTwo_castle",
                "8/8/8/2P3R1/5B2/2rP1p2/p1P1PP2/RnQ1K2k w Q - 0 1",
                4,
                "c1b2");
    }

    private ForcedMateSearchTest mateInThree() {
        return forcedMateSearchTest("mateInThree",
                "r5rk/5p1p/5R2/4B3/8/8/7P/7K w - - 0 1",
                5,
                "f6a6");
    }

    private ForcedMateSearchTest mateInFour() {
        return forcedMateSearchTest("mateInFour",
                "1KRr4/2P2PBP/PP2PNQ1/n7/3q2B1/4bb2/ppp1ppRp/1kr4r b - - 0 1",
                7,
                "e1f1");
    }

    private static ForcedMateSearchTest forcedMateSearchTest(final String name, final String fen, final int depth, final String bestMove) {
        return new ForcedMateSearchTest(name, fen, depth, bestMove);
    }
    
    private static final class ForcedMateSearchTest {

        private final String name;
        private final String fen;
        private final int depth;
        private final String bestMove;

        private ForcedMateSearchTest(final String name, final String fen, final int depth, final String bestMove) {
            this.name = name;
            this.fen = fen;
            this.depth = depth;
            this.bestMove = bestMove;
        }

        private void run(final CombinatorialGameMoveSearcher<ChessGame, ChessMove> searcher, final GameEvaluatorCounter<ChessGame> counter, final String algName) {
            final ChessGame game = ChessGame.fromFen(this.fen);
            final long startTime = System.currentTimeMillis();
            final String move = searcher.search(game).toString();
            final long elapsed = System.currentTimeMillis() - startTime;
            assertEquals(this.bestMove, move);
            System.out.println("MS-PERFT '" + this.name + "' success: " +
                    algName + "=[nodes=" + counter.getCount() + ", elapsed=" + elapsed + "ms]");
        }

        private ForcedMateSearchTest runAlphaBeta() {
            final GameEvaluatorCounter<ChessGame> abCounter = new GameEvaluatorCounter<>(ChessGameEvaluator.position());
            final CombinatorialGameMoveSearcher<ChessGame, ChessMove> abSearcher = ChessGameMoveSearcher.alphaBeta(abCounter, this.depth);
            run(abSearcher, abCounter, "alphaBe");
            return this;
        }

        private ForcedMateSearchTest runMinmax() {
            final GameEvaluatorCounter<ChessGame> mmCounter = new GameEvaluatorCounter<>(ChessGameEvaluator.position());
            final CombinatorialGameMoveSearcher<ChessGame, ChessMove> mmSearcher = ChessGameMoveSearcher.minmax(mmCounter, this.depth);
            run(mmSearcher, mmCounter, "min-max");
            return this;
        }
    }
}
