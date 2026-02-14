package dev.pig.stockpig.engine.search;

import dev.pig.stockpig.chess.core.Colour;
import dev.pig.stockpig.chess.core.MoveList;
import dev.pig.stockpig.chess.core.Position;
import dev.pig.stockpig.engine.evaluation.PositionEvaluator;

public final class AlphaBetaSearcher {

    public static int search(final Position pos) {
        return pos.sideToMove() == Colour.WHITE ? searchMax(pos, 6) : searchMin(pos, 6);
    }

    private static int searchMax(final Position pos, final int depth) {
        int alpha = Integer.MIN_VALUE;
        int best = 0;

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = minimise(pos, depth-1, alpha, Integer.MAX_VALUE);
            pos.unmakeMove();

            if (score > alpha) {
                alpha = score;
                best = move;
            }
        }
        return best;
    }

    private static int searchMin(final Position pos, final int depth) {
        int beta = Integer.MAX_VALUE;
        int best = 0;

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = maximise(pos, depth-1, Integer.MIN_VALUE, beta);
            pos.unmakeMove();

            if (score < beta) {
                beta = score;
                best = move;
            }
        }
        return best;
    }

    private static int maximise(final Position pos, final int depth, int alpha, final int beta) {
        if (depth == 0 || pos.isGameOver()) return PositionEvaluator.eval(pos, depth);

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = minimise(pos, depth-1, alpha, beta);
            pos.undo();

            if (score >= beta) {
                return beta;
            }

            if (score > alpha) {
                alpha = score;
            }
        }
        return alpha;
    }

    private static int minimise(final Position pos, final int depth, final int alpha, int beta) {
        if (depth == 0 || pos.isGameOver()) return PositionEvaluator.eval(pos, depth);

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = maximise(pos, depth-1, alpha, beta);
            pos.undo();

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
