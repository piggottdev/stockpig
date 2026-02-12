package dev.pig.stockpig.chess.perft;

import dev.pig.stockpig.chess.notation.Fen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Bench is a runner for a suite of move enumeration PERFT tests.
 * The results of the first run are discarded in order to warmup the JVM and
 * reduce noise from runtime/JIT eccentricities.
 */
public final class Bench {

    /**
     * Run move enumeration PERFT benchmarking test suite.
     * @param args args
     */
    public static void main(final String[] args) throws IOException, Fen.ParseException {

        // Warmup
        for (final TestCase test : Suite.TESTS) TestCase.run(test);
        System.gc();

        // Suite level results
        boolean pass = true;
        long totalNodes = 0L;
        long totalRuntime = 0L;

        System.out.println("-------------------- Starting ME-PERFTs --------------------");

        for (final TestCase test : Suite.TESTS) {
            final TestCase.Result result = TestCase.run(test);

            if (result.nodes() == test.expectedNodes()) {
                System.out.printf("ME-PERFT '%-10s' PASS: nodes=%-10s elapsed=%5sms%n",
                        test.name(), result.nodes(), result.runtimeNs()/1000000);

            } else {
                System.out.printf("ME-PERFT '%-10s' FAIL: nodes=%-10s (expected=%-10s) elapsed=%5dms%n",
                        test.name(), result.nodes(), test.expectedNodes(), result.runtimeNs()/1000000);
                pass = false;
            }

            totalNodes   += result.nodes();
            totalRuntime += result.runtimeNs();
        }

        System.out.println("-------------------- Finished ME-PERFTs --------------------");
        System.out.printf("ME-PERFT Suite completed: %d nodes in %dms (%dnps)%n", totalNodes, totalRuntime/1000000, Math.round(totalNodes / (totalRuntime/1000000000d)));

        // If a test failed then exit now
        if (!pass) System.exit(1);

        // If a results file path was passed then print a json result there
        if (args.length > 0) {
            Files.writeString(Path.of(args[0]), String.format(
        """
        {
            "runtime": %d,
            "nodes": %d,
            "nps": %d
        }
        """, totalRuntime, totalNodes, Math.round(totalNodes / (totalRuntime/1000000000d)) ));
        }
    }
}
