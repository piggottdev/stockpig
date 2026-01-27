package dev.pig.stockpig.chess.perft;

import java.util.List;

/**
 * Suite provides a collection of move enumeration PERFT test cases for
 * validating and testing performance of move generation.
 */
public final class Suite {

    /**
     * List of move enumeration PERFT test cases.
     */
    public static final List<TestCase> TESTS = List.of(
            new TestCase("starting",
                    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                    6,
                    119060324L
            ),
            new TestCase("kiwipete",
                    "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1",
                    5,
                    193690690L
            ),
            new TestCase("position 3",
                    "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1",
                    7,
                    178633661L
            ),
            new TestCase("position 4",
                    "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
                    6,
                    706045033L
            ),
            new TestCase("position 5",
                    "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8",
                    5,
                    89941194L
            ),
            new TestCase("position 6",
                    "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10",
                    5,
                    164075551L
            )
    );
}
