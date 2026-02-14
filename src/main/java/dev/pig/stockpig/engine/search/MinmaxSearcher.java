package dev.pig.stockpig.engine.search;

import dev.pig.stockpig.chess.core.Colour;
import dev.pig.stockpig.chess.core.MoveList;
import dev.pig.stockpig.chess.core.Position;
import dev.pig.stockpig.engine.evaluation.PositionEvaluator;

public final class MinmaxSearcher {

    public static int search(final Position pos) {
        return pos.sideToMove() == Colour.WHITE ? searchMax(pos, 5) : searchMin(pos, 5);
    }

    private static int searchMax(final Position pos, final int depth) {
        int max = Integer.MIN_VALUE;
        int best = 0;

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = minimise(pos, depth-1);
            pos.undo();

            if (score > max) {
                max = score;
                best = move;
            }
        }
        return best;
    }

    private static int searchMin(final Position pos, final int depth) {
        int min = Integer.MAX_VALUE;
        int best = 0;

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = maximise(pos, depth-1);
            pos.unmakeMove();

            if (score < min) {
                min = score;
                best = move;
            }
        }
        return best;
    }

    private static int maximise(final Position pos, final int depth) {
        if (depth == 0 || pos.isGameOver()) return PositionEvaluator.eval(pos, depth);

        int max = Integer.MIN_VALUE;

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = minimise(pos, depth-1);
            pos.undo();

            if (score > max) {
                max = score;
            }
        }
        return max;
    }

    private static int minimise(final Position pos, final int depth) {
        if (depth == 0 || pos.isGameOver()) return PositionEvaluator.eval(pos, depth);

        int min = Integer.MAX_VALUE;

        final MoveList moves = pos.moves().clone();
        for (int i = 0; i < moves.size(); i ++) {
            final int move = moves.get(i);

            pos.makeMove(move);
            final int score = maximise(pos, depth-1);
            pos.undo();

            if (score < min) {
                min = score;
            }
        }
        return min;
    }
}
