package dev.pig.old.chess;

import dev.pig.old.chess.ChessGame;
import dev.pig.old.chess.ChessMove;
import org.junit.jupiter.api.Test;
import dev.pig.old.game.MoveEnumerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovePathEnumerationTest {

    @Test
    public void perftTests() {
        System.out.println("----- Starting ME-PERFTs -----");
        final long startTime = System.currentTimeMillis();
        starting().run();
        kiwipete().run();
        pos3().run();
        pos4().run();
        pos5().run();
        pos6().run();
        final long elapsed = (System.currentTimeMillis() - startTime) / 1000L;
        System.out.println("----- Finished ME-PERFTs -----");
        System.out.println("All ME-PERFTs completed in " + elapsed + "s");
    }

    private MoveEnumerationTest starting() {
        return moveEnumerationTest("starting", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 6, 119060324L);
    }

    private MoveEnumerationTest kiwipete() {
        return moveEnumerationTest("kiwipete", "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", 5, 193690690L);
    }

    private MoveEnumerationTest pos3() {
        return moveEnumerationTest("pos3", "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1", 7, 178633661L);
    }

    private MoveEnumerationTest pos4() {
        return moveEnumerationTest("pos4","r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1", 6, 706045033L);
    }

    private MoveEnumerationTest pos5() {
        return moveEnumerationTest("pos5", "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8  ", 5, 89941194L);
    }

    private MoveEnumerationTest pos6() {
        return moveEnumerationTest("pos6", "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10", 5, 164075551L);
    }

    private static MoveEnumerationTest moveEnumerationTest(final String name, final String fen, final int depth, final long expectedPositions) {
        return new MoveEnumerationTest(name, fen, depth, expectedPositions);
    }

    private static final class MoveEnumerationTest {

        private final String name;
        private final String fen;
        private final int depth;
        private final long expectedPositions;

        private MoveEnumerationTest(final String name, final String fen, final int depth, final long expectedPositions) {
            this.name = name;
            this.fen = fen;
            this.depth = depth;
            this.expectedPositions = expectedPositions;
        }

        private void run() {
            final MoveEnumerator<ChessMove> test = new MoveEnumerator<>(ChessGame.fromFen(this.fen), this.depth);
            assertEquals(this.expectedPositions, test.getNodes());
            System.out.println("ME-PERFT '" + this.name + "' success: nodes=" + test.getNodes() + ", elapsed=" + test.getElapsed() + "ms");
        }
    }
}
