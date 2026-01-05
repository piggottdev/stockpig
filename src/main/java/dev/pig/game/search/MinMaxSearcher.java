package dev.pig.game.search;

import dev.pig.game.PartisanGame;
import dev.pig.game.evaluation.GameEvaluator;
import dev.pig.game.evaluation.MinMaxEvaluator;

/**
 * Perform a move search from the position using the min max algorithm.
 * The search is calculated by building the game tree to the desired depth,
 * evaluating each leaf position and assume that each player maximises/minimises the
 * final position.
 * The best move at the current position is returned.
 *
 * @param <G> game type
 * @param <M> move type
 */
public class MinMaxSearcher<G extends PartisanGame<M>, M extends PartisanGame.Move> implements CombinatorialGameMoveSearcher<G, M> {

    private final GameEvaluator<G> evaluator;
    private final int depth;

    public MinMaxSearcher(final GameEvaluator<G> evaluator, final int depth) {
        this.evaluator = evaluator;
        this.depth = depth;
    }

    @Override
    public M search(final G game) {
        final M move = game.isPlayerOneTurn() ? maxMove(game, this.depth) : minMove(game, this.depth);
        game.generateLegalMoves();
        return move;
    }

    private M maxMove(final G game, final int depth) {

        M bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (final M move: game.getLegalMoves()) {
            game.applyMove(move);
            final int score = new MinMaxEvaluator<>(this.evaluator, depth - 1).evaluate(game);
            game.undoMoveWithoutMoveGen();
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private M minMove(final G game, final int depth) {

        M bestMove = null;
        int bestScore = Integer.MAX_VALUE;

        for (final M move: game.getLegalMoves()) {
            game.applyMove(move);
            final int score = new MinMaxEvaluator<>(this.evaluator, depth - 1).evaluate(game);
            game.undoMoveWithoutMoveGen();
            if (score < bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

}
