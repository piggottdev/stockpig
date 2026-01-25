package dev.pig.old.game.evaluation;

import dev.pig.old.game.PartisanGame;

/**
 * Perform an evaluation of the position using the alpha beta min max algorithm.
 * The evaluation is calculated by building the game tree to the desired depth,
 * evaluating each leaf position and assume that each player maximises/minimises the
 * final position.
 * Alpha beta is an optimisation on standard min max which always returns the same result but
 * faster.
 * Alpha beta performs best when better moves are evaluated first.
 *
 * @param <G> game type
 * @param <M> move type
 */
public class AlphaBetaEvaluator<G extends PartisanGame<M>, M extends PartisanGame.Move> implements GameEvaluator<G> {

    protected final GameEvaluator<G> positionEvaluator;
    protected final int depth;
    protected final int alpha;
    protected final int beta;

    public AlphaBetaEvaluator(final GameEvaluator<G> positionEvaluator, final int depth, final int alpha, final int beta) {
        this.positionEvaluator = positionEvaluator;
        this.depth = depth;
        this.alpha = alpha;
        this.beta = beta;
    }

    public AlphaBetaEvaluator(final GameEvaluator<G> positionEvaluator, final int depth) {
        this(positionEvaluator, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public int evaluate(final G game) {
        final int score = game.isPlayerOneTurn() ?
                max(game, this.depth, this.alpha, this.beta) :
                min(game, this.depth, this.alpha, this.beta);
        game.generateLegalMoves();
        return score;
    }

    protected int max(final G game, final int depth, int alpha, int beta) {
        if (depth == 0 || game.isGameOver()) {
            return maxEval(game, alpha, beta);
        }
        for (final M move : game.getLegalMoves()) {
            game.applyMove(move);
            final int score = min(game, depth - 1, alpha, beta);
            game.undoMoveWithoutMoveGen();
            if (score >= beta) {
                return beta;
            }
            if (score > alpha) {
                alpha = score;
            }
        }
        return alpha;
    }

    protected int min(final G game, final int depth, int alpha, int beta) {
        if (depth == 0 || game.isGameOver()) {
            return minEval(game, alpha, beta);
        }
        for (final M move : game.getLegalMoves()) {
            game.applyMove(move);
            final int score = max(game, depth - 1, alpha, beta);
            game.undoMoveWithoutMoveGen();
            if (score <= alpha) {
                return alpha;
            }
            if (score < beta) {
                beta = score;
            }
        }
        return beta;
    }

    protected int maxEval(final G game, final int alpha, final int beta) {
        return this.positionEvaluator.evaluate(game);
    }

    protected int minEval(final G game, final int alpha, final int beta) {
        return this.positionEvaluator.evaluate(game);
    }

}
