package dev.pig.game.evaluation;

import dev.pig.game.Game;

/**
 * A game evaluator that wraps another game evaluator and counts invocations.
 *
 * @param <G> game type
 */
public class GameEvaluatorCounter<G extends Game> implements GameEvaluator<G> {

    private final GameEvaluator<G> evaluator;

    private int count = 0;

    public GameEvaluatorCounter(final GameEvaluator<G> evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public int evaluate(final G game) {
        this.count++;
        return this.evaluator.evaluate(game);
    }

    /**
     * Return how many times the evaluator was called.
     *
     * @return evaluation count
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Reset the counter.
     */
    public void clear() {
        this.count = 0;
    }

}
