package dev.pig.game.evaluation;

/**
 * Interface of a function that evaluates some object to obtain a score.
 *
 * @param <E> evaluatee type
 */
@FunctionalInterface
public interface Evaluator<E> {

    /**
     * Evaluate the evaluatee to a score.
     *
     * @param evaluatee evaluatee
     * @return score
     */
    int evaluate(final E evaluatee);

}
