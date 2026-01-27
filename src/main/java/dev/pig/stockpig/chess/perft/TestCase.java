package dev.pig.stockpig.chess.perft;

import dev.pig.stockpig.chess.MoveList;
import dev.pig.stockpig.chess.Position;

/**
 * Test case represents a move enumeration PERFT test case. Move enumeration tests construct
 * a position tree by making all legal moves up to a depth ply, the amount of nodes
 * is then compared against the known correct value to validate move generation. Monitoring
 * time elapsed for tests is a good way of benchmarking move generation.
 * @param name test name
 * @param fen fen string of starting pos
 * @param depth depth ply
 * @param expectedNodes expected node count
 */
public record TestCase(String name, String fen, int depth, long expectedNodes) {

    /**
     * Result is the result of a move enumeration PERFT test.
     * @param nodes visited node count
     * @param runtimeNs runtime (ns)
     */
    public record Result(long nodes, long runtimeNs) {}

    /**
     * Run a move enumeration PERFT test and return a result.
     * @param test move enumeration PERFT test
     * @return result
     */
    public static Result run(final TestCase test) {
        final Position pos = Position.fromFen(test.fen);

        final long start = System.nanoTime();
        final long nodes = perft(pos, test.depth);
        final long elapsed = System.nanoTime() - start;

        return new Result(nodes, elapsed);
    }

    /**
     * Move enumeration PERFT internal runner.
     * @param pos current position
     * @param depth current remaining depth
     * @return nodes visited
     */
    private static long perft(final Position pos, final int depth) {
        if (depth == 0) return 1;
        if (depth == 1) return pos.moves().size();

        final MoveList moves = pos.moves().clone();
        long nodes = 0;

        for (int i = 0; i < moves.size(); i++) {
            final int move = moves.get(i);
            pos.makeMove(move);
            nodes += perft(pos, depth - 1);
            pos.unmakeMove();
        }

        return nodes;
    }
}