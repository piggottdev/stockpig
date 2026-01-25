package dev.pig.old.search;

import dev.pig.old.evaluation.ChessGameEvaluator;
import dev.pig.old.chess.ChessGame;
import dev.pig.old.chess.ChessMove;
import dev.pig.old.game.evaluation.GameEvaluator;
import dev.pig.old.game.search.AlphaBetaSearcher;
import dev.pig.old.game.search.CombinatorialGameMoveSearcher;
import dev.pig.old.game.search.MinMaxSearcher;
import dev.pig.old.game.search.QuiescenceSearcher;

public interface ChessGameMoveSearcher {

    static CombinatorialGameMoveSearcher<ChessGame, ChessMove> minmax(final int depth) {
        return new MinMaxSearcher<>(ChessGameEvaluator.position(), depth);
    }

    static CombinatorialGameMoveSearcher<ChessGame, ChessMove> minmax(final GameEvaluator<ChessGame> positionEvaluator, final int depth) {
        return new MinMaxSearcher<>(positionEvaluator, depth);
    }

    static CombinatorialGameMoveSearcher<ChessGame, ChessMove> alphaBeta(final int depth) {
        return new AlphaBetaSearcher<>(ChessGameEvaluator.position(), depth);
    }

    static CombinatorialGameMoveSearcher<ChessGame, ChessMove> alphaBeta(final GameEvaluator<ChessGame> positionEvaluator, final int depth) {
        return new AlphaBetaSearcher<>(positionEvaluator, depth);
    }

    static CombinatorialGameMoveSearcher<ChessGame, ChessMove> quiescence(final int depth, final int quiescenceDepth) {
        return new QuiescenceSearcher<>(ChessGameEvaluator.position(), depth, quiescenceDepth);
    }

    static CombinatorialGameMoveSearcher<ChessGame, ChessMove> quiescence(final GameEvaluator<ChessGame> positionEvaluator, final int depth, final int quiescenceDepth) {
        return new QuiescenceSearcher<>(positionEvaluator, depth, quiescenceDepth);
    }

}
