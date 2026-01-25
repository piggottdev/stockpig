package dev.pig.old.evaluation;

import dev.pig.old.chess.ChessGame;
import dev.pig.old.chess.Piece;
import dev.pig.old.game.evaluation.GameEvaluator;

/**
 * A simple chess material evaluation function, each piece has a value.
 * Theoretical max per team: 10300
 */
public class MaterialEvaluator implements GameEvaluator<ChessGame> {

    private final int[] materialScore;

    public MaterialEvaluator() {
        this.materialScore = new int[]{100, 300, 300, 500, 900};
    }

    @Override
    public int evaluate(final ChessGame game) {
        return evaluatePiece(game, Piece.PAWN, materialScore[0]) +
                evaluatePiece(game, Piece.KNIGHT, materialScore[1]) +
                evaluatePiece(game, Piece.BISHOP, materialScore[2]) +
                evaluatePiece(game, Piece.ROOK, materialScore[3]) +
                evaluatePiece(game, Piece.QUEEN, materialScore[4]);
    }

    private int evaluatePiece(final ChessGame game, final int piece, final int pieceScore) {
        return pieceScore *
                (Long.bitCount(game.getPieceBitboard(Piece.WHITE | piece)) - Long.bitCount(game.getPieceBitboard(piece)));
    }

}
