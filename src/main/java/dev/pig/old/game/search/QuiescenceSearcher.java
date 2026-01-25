package dev.pig.old.game.search;

import dev.pig.old.game.PartisanGame;
import dev.pig.old.game.evaluation.GameEvaluator;
import dev.pig.old.game.evaluation.QuiescenceEvaluator;

/**
 * Perform a move search from the position using the alpha beta min max algorithm.
 * Also perform an additional quiescence search at depth 0 of each search, a
 * quiescence search continues searching all nodes until a quiet position is reached.
 * The search is calculated by building the game tree to the desired depth,
 * evaluating each leaf position and assume that each player maximises/minimises the
 * final position.
 * Alpha beta is an optimisation on standard min max which always returns the same result but
 * faster.
 * Alpha beta performs best when better moves are evaluated first.
 *
 * @param <G> game type
 * @param <M> move type
 */
public class QuiescenceSearcher<G extends PartisanGame<M>, M extends PartisanGame.Move> implements CombinatorialGameMoveSearcher<G, M>  {

    private final GameEvaluator<G> positionEvaluator;
    private final int depth;
    private final int quiescenceMaxDepth;

    public QuiescenceSearcher(final GameEvaluator<G> positionEvaluator, final int depth, final int quiescenceMaxDepth) {
        this.positionEvaluator = positionEvaluator;
        this.depth = depth;
        this.quiescenceMaxDepth = quiescenceMaxDepth;
    }

    @Override
    public M search(final G game) {
        final M move = game.isPlayerOneTurn() ? maxMove(game, this.depth) : minMove(game, this.depth);
        game.generateLegalMoves();
        return move;
    }

    private M maxMove(final G game, final int depth) {

        M bestMove = null;
        int alpha = Integer.MIN_VALUE;

        for (final M move : game.getLegalMoves()) {
            game.applyMove(move);
            final int score = new QuiescenceEvaluator<>(this.positionEvaluator, depth - 1, this.quiescenceMaxDepth, alpha, Integer.MAX_VALUE).evaluate(game);
            game.undoMoveWithoutMoveGen();
            if (score > alpha) {
                alpha = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private M minMove(final G game, final int depth) {

        M bestMove = null;
        int beta = Integer.MAX_VALUE;

        for (final M move : game.getLegalMoves()) {
            game.applyMove(move);
            final int score = new QuiescenceEvaluator<>(this.positionEvaluator, depth - 1, this.quiescenceMaxDepth, Integer.MIN_VALUE, beta).evaluate(game);
            game.undoMoveWithoutMoveGen();
            if (score < beta) {
                beta = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

}
