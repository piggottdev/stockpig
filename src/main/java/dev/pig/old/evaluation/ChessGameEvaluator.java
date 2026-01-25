package dev.pig.old.evaluation;

import dev.pig.old.chess.ChessGame;
import dev.pig.old.game.evaluation.AlphaBetaEvaluator;
import dev.pig.old.game.evaluation.GameEvaluator;
import dev.pig.old.game.evaluation.MinMaxEvaluator;
import dev.pig.old.game.evaluation.QuiescenceEvaluator;

public interface ChessGameEvaluator {

    static GameEvaluator<ChessGame> material() {
        return new MaterialEvaluator();
    }

    static GameEvaluator<ChessGame> position() {
        return new GameOverEvaluator(material());
    }

    static GameEvaluator<ChessGame> position(final GameEvaluator<ChessGame> materialEvaluator) {
        return new GameOverEvaluator(materialEvaluator);
    }

    static GameEvaluator<ChessGame> minmax(final int depth) {
        return new MinMaxEvaluator<>(position(), depth);
    }

    static GameEvaluator<ChessGame> minmax(final GameEvaluator<ChessGame> positionEvaluator, final int depth) {
        return new MinMaxEvaluator<>(positionEvaluator, depth);
    }

    static GameEvaluator<ChessGame> alphaBeta(final int depth) {
        return new AlphaBetaEvaluator<>(position(), depth);
    }

    static GameEvaluator<ChessGame> alphaBeta(final GameEvaluator<ChessGame> positionEvaluator, final int depth) {
        return new AlphaBetaEvaluator<>(positionEvaluator, depth);
    }

    static GameEvaluator<ChessGame> quiescence(final int depth, final int quiescenceDepth) {
        return new QuiescenceEvaluator<>(position(), depth, quiescenceDepth);
    }

    static GameEvaluator<ChessGame> quiescence(final GameEvaluator<ChessGame> positionEvaluator, final int depth, final int quiescenceDepth) {
        return new QuiescenceEvaluator<>(positionEvaluator, depth, quiescenceDepth);
    }

}
