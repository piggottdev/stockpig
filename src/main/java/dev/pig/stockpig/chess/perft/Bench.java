package dev.pig.stockpig.chess.perft;

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
    public static void main(final String[] args) {

        // Warmup
        for (final Test test : Suite.TESTS) Test.run(test);
        System.gc();

        // Suite level results
        boolean pass = true;
        long totalNodes = 0L;
        long totalRuntime = 0L;

        System.out.println("-------------------- Starting ME-PERFTs --------------------");

        for (final Test test : Suite.TESTS) {
            final Test.Result result = Test.run(test);

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
        System.out.printf("ME-PERFT Suite completed: %d nodes in %dms%n", totalNodes, totalRuntime/1000000);

        if (!pass) System.exit(1);
    }
}
