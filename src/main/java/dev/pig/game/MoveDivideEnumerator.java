package dev.pig.game;

import java.util.HashMap;
import java.util.Map;

/**
 * An extension of {@link MoveEnumerator} where the positions are
 * enumerated after depth 1.
 * Useful for debugging and determining issues with implementation.
 */
public class MoveDivideEnumerator<M extends CombinatorialGame.Move> extends MoveEnumerator<M> {

    private Map<String, Long> divide;

    public MoveDivideEnumerator(final CombinatorialGame<M> game, final int depth) {
        super(game, depth);
    }

    /**
     * Get the result of the divide PERFT.
     * This is the toString of each move mapped to
     * the move enumeration result after the move.
     *
     * @return divide result
     */
    public Map<String, Long> getDivide() {
        return this.divide;
    }

    @Override
    protected void run(final int depth) {
        long nodes = 0L;
        this.divide = new HashMap<>();
        for (final M move: this.game.getLegalMoves()) {
            this.game.applyMove(move);
            final long nodesAfterMove = moveEnumeration(depth - 1);
            nodes += nodesAfterMove;
            this.divide.put(move.toString(), nodesAfterMove);
            this.game.undoMoveWithoutMoveGen();
        }
        this.nodes = nodes;
    }
}
