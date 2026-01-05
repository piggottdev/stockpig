package dev.pig.game.evaluation;

import dev.pig.game.PartisanGame;

/**
 * Perform an evaluation of the position using the min max algorithm.
 * The evaluation is calculated by building the game tree to the desired depth,
 * evaluating each leaf position and assume that each player maximises/minimises the
 * final position.
 *
 * @param <G> game type
 * @param <M> move type
 */
public class MinMaxEvaluator<G extends PartisanGame<M>, M extends PartisanGame.Move> implements GameEvaluator<G> {

    private final GameEvaluator<G> positionEvaluator;
    private final int depth;

    public MinMaxEvaluator(final GameEvaluator<G> positionEvaluator, final int depth) {
        this.positionEvaluator = positionEvaluator;
        this.depth = depth;
    }

    @Override
    public int evaluate(final G game) {
        final int score = game.isPlayerOneTurn() ? max(game, this.depth) : min(game, this.depth);
        game.generateLegalMoves();
        return score;
    }

    private int max(final G game, final int depth) {
        if (depth == 0 || game.isGameOver()) {
            return this.positionEvaluator.evaluate(game);
        }
        int maxScore = Integer.MIN_VALUE;
        for (final M move : game.getLegalMoves()) {
            game.applyMove(move);
            final int score = min(game, depth - 1);
            game.undoMoveWithoutMoveGen();
            if (score > maxScore) {
                maxScore = score;
            }
        }
        return maxScore;
    }

    private int min(final G game, final int depth) {
        if (depth == 0 || game.isGameOver()) {
            return this.positionEvaluator.evaluate(game);
        }
        int minScore = Integer.MAX_VALUE;
        for (final M move : game.getLegalMoves()) {
            game.applyMove(move);
            final int score = max(game, depth - 1);
            game.undoMoveWithoutMoveGen();
            if (score < minScore) {
                minScore = score;
            }
        }
        return minScore;
    }

}
