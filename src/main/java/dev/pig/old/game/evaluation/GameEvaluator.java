package dev.pig.old.game.evaluation;

import dev.pig.old.game.Game;

/**
 * Interface of a function that evaluates a game to a score.
 *
 * @param <G> game type
 */
@FunctionalInterface
public interface GameEvaluator<G extends Game> extends Evaluator<G> {

    /**
     * Evaluate the game to a score.
     *
     * @param game game
     * @return score
     */
    @Override
    int evaluate(final G game);
}
