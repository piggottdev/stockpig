package dev.pig.stockpig.engine.evaluation;

import dev.pig.stockpig.chess.core.Colour;
import dev.pig.stockpig.chess.core.PieceType;
import dev.pig.stockpig.chess.core.Position;
import dev.pig.stockpig.chess.core.bitboard.Bitboard;
import dev.pig.stockpig.chess.core.bitboard.Square;

public final class PositionEvaluator {

    private static final int WHITE_WIN = 1000000;
    private static final int BLACK_WIN = -1000000;
    private static final int DRAW      = 0;

    private static final int QUEEN_SCORE  = 9000;
    private static final int ROOK_SCORE   = 5000;
    private static final int BISHOP_SCORE = 3000;
    private static final int KNIGHT_SCORE = 3000;
    private static final int PAWN_SCORE   = 1000;

    public static int eval(final Position pos, final int depth) {
        if (pos.isGameOver() && !pos.isCheckmate()) {
            return DRAW;
        }
        if (pos.isGameOver() && pos.sideToMove() == Colour.BLACK) {
            return WHITE_WIN + depth;
        }
        if (pos.isGameOver() && pos.sideToMove() == Colour.WHITE) {
            return BLACK_WIN - depth;
        }

        int materialScore = 0;

        final long white = pos.board().pieces(Colour.WHITE);

        long pieces = pos.board().occupied();
        while (pieces != 0L) {
            final long piece = Bitboard.pop(pieces);
            final byte square = Square.ofBitboard(piece);
            final byte pieceType = pos.board().pieceAt(square);
            final int pieceScore = switch (pieceType) {
                case PieceType.QUEEN -> QUEEN_SCORE;
                case PieceType.ROOK -> ROOK_SCORE;
                case PieceType.BISHOP -> BISHOP_SCORE;
                case PieceType.KNIGHT -> KNIGHT_SCORE;
                case PieceType.PAWN -> PAWN_SCORE;
                default -> 0;
            };

            materialScore += Bitboard.contains(white, piece) ? pieceScore : -pieceScore;

            pieces ^= piece;
        }
        return materialScore;
    }
}
