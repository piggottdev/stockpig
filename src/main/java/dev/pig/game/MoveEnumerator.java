package dev.pig.game;

/**
 * Move enumeration tests are a form of test that build the game tree
 * from a given game position, nodes of the tree are counted and compared against
 * known values - very good at checking the correctness of the implementation of the game.
 * Also, for larger depth values can be a good performance indicator of the implementation.
 */
public class MoveEnumerator<M extends CombinatorialGame.Move> {

    protected final CombinatorialGame<M> game;

    protected long elapsed;
    protected long nodes;

    public MoveEnumerator(final CombinatorialGame<M> game, final int depth) {
        this.game = game;
        runTimed(depth);
        this.game.generateLegalMoves();
    }

    /**
     * Test run time in ms.
     *
     * @return elapsed time (ms)
     */
    public long getElapsed() {
        return this.elapsed;
    }

    /**
     * Total node count of position tree - the result.
     *
     * @return move enumeration result
     */
    public long getNodes() {
        return this.nodes;
    }

    private void runTimed(final int depth) {
        final long startTime = System.currentTimeMillis();
        run(depth);
        this.elapsed = System.currentTimeMillis() - startTime;
    }

    protected void run(final int depth) {
        this.nodes = moveEnumeration(depth);
    }

    protected long moveEnumeration(final int depth) {
        if (depth < 1) return 1;
        if (depth == 1) return this.game.getLegalMoves().size();

        long numberOfPositions = 0;

        for (final M move : this.game.getLegalMoves()) {
            this.game.applyMove(move);
            numberOfPositions += moveEnumeration(depth - 1);
            this.game.undoMoveWithoutMoveGen();
        }

        return numberOfPositions;
    }

}
