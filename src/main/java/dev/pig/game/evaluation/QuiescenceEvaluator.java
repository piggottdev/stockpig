package dev.pig.game.evaluation;

import dev.pig.game.PartisanGame;

import java.util.List;

/**
 * Perform an evaluation of the position using the alpha beta min max algorithm.
 * Also perform an additional quiescence search at depth 0 of each search, a
 * quiescence search continues searching all nodes until a quiet position is reached.
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
public class QuiescenceEvaluator<G extends PartisanGame<M>, M extends PartisanGame.Move> extends AlphaBetaEvaluator<G, M> {

    private final int quiescenceMaxDepth;

    public QuiescenceEvaluator(final GameEvaluator<G> positionEvaluator, final int depth, final int quiescenceMaxDepth, final int alpha, final int beta) {
        super(positionEvaluator, depth, alpha, beta);
        this.quiescenceMaxDepth = quiescenceMaxDepth;
    }

    public QuiescenceEvaluator(final GameEvaluator<G> positionEvaluator, final int depth, final int quiescenceMaxDepth) {
        this(positionEvaluator, depth, quiescenceMaxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    protected int maxEval(final G game, final int alpha, final int beta) {
        return maxQuiescence(game, this.quiescenceMaxDepth, alpha, beta);
    }

    protected int minEval(final G game, final int alpha, final int beta) {
        return minQuiescence(game, this.quiescenceMaxDepth, alpha, beta);
    }

    private int maxQuiescence(final G game, final int depth, int alpha, int beta) {
        if (depth == 0 || game.isGameOver()) {
            return this.positionEvaluator.evaluate(game);
        }
        final List<M> moves = game.isQuiet() ? game.getLegalMoves().stream().filter(m -> !m.isQuiet()).toList() : game.getLegalMoves();
        if (moves.isEmpty()) {
            return this.positionEvaluator.evaluate(game);
        }

        for (final M move : moves) {
            game.applyMove(move);
            final int score = minQuiescence(game, depth - 1, alpha, beta);
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

    private int minQuiescence(final G game, final int depth, int alpha, int beta) {
        if (depth == 0 || game.isGameOver()) {
            return this.positionEvaluator.evaluate(game);
        }
        final List<M> moves = game.isQuiet() ? game.getLegalMoves().stream().filter(m -> !m.isQuiet()).toList() : game.getLegalMoves();
        if (moves.isEmpty()) {
            return this.positionEvaluator.evaluate(game);
        }

        for (final M move : moves) {
            game.applyMove(move);
            final int score = maxQuiescence(game, depth - 1, alpha, beta);
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

}
