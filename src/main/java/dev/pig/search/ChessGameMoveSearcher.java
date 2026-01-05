package dev.pig.search;

import dev.pig.evaluation.ChessGameEvaluator;
import dev.pig.chess.ChessGame;
import dev.pig.chess.ChessMove;
import dev.pig.game.evaluation.GameEvaluator;
import dev.pig.game.search.AlphaBetaSearcher;
import dev.pig.game.search.CombinatorialGameMoveSearcher;
import dev.pig.game.search.MinMaxSearcher;
import dev.pig.game.search.QuiescenceSearcher;

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
