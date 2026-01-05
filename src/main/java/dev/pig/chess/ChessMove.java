package dev.pig.chess;

import dev.pig.game.PartisanGame;

/**
 * Provides an object to store chess move data.
 */
public class ChessMove implements PartisanGame.Move {

    private final long from;
    private final long to;
    private final int movingPiece;
    private final int capturedPiece;
    private final int promotedToPiece;
    private final long capturedEnPassantPawn;
    private final long enPassantTarget;
    private final long castleRookMove;

    /**
     * @param from bitboard, primary piece is moving from
     * @param to bitboard, primary piece is moving to
     * @param movingPiece primary moving piece
     * @param capturedPiece piece that has been captured
     * @param promotedToPiece piece that a pawn is being promoted to
     * @param capturedEnPassantPawn bitboard, pawn captured in an en passant move
     * @param enPassantTarget bitboard, if the move is a double pawn push, this is the square that can now be the target of an en passant
     * @param castleRookMove bitboard of rook movement, for castle moves
     */
    private ChessMove(final long from, final long to, final int movingPiece, final int capturedPiece, final int promotedToPiece, final long capturedEnPassantPawn, final long enPassantTarget, final long castleRookMove) {
        this.from = from;
        this.to = to;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
        this.promotedToPiece = promotedToPiece;
        this.capturedEnPassantPawn = capturedEnPassantPawn;
        this.enPassantTarget = enPassantTarget;
        this.castleRookMove = castleRookMove;
    }

    static ChessMove castle(final long from, final long to, final int movingPiece, final long castleRookMove) {
        return new ChessMove(from, to, movingPiece, Piece.EMPTY, Piece.EMPTY, Bitboard.EMPTY, Bitboard.EMPTY, castleRookMove);
    }

    static ChessMove doublePush(final long from, final long to, final int movingPiece, final long enPassantTarget) {
        return new ChessMove(from, to, movingPiece, Piece.EMPTY, Piece.EMPTY, Bitboard.EMPTY, enPassantTarget, Bitboard.EMPTY);
    }

    static ChessMove enPassantCapture(final long from, final long to, final int movingPiece, final int capturedPiece, final long capturedEnPassantPawn) {
        return new ChessMove(from, to, movingPiece, capturedPiece, Piece.EMPTY, capturedEnPassantPawn, Bitboard.EMPTY, Bitboard.EMPTY);
    }

    static ChessMove pawnPromotionWithCapture(final long from, final long to, final int movingPiece, final int capturedPiece, final int promotedToPiece) {
        return new ChessMove(from, to, movingPiece, capturedPiece, promotedToPiece, Bitboard.EMPTY, Bitboard.EMPTY, Bitboard.EMPTY);
    }

    static ChessMove pawnPromotion(final long from, final long to, final int movingPiece, final int promotedToPiece) {
        return new ChessMove(from, to, movingPiece, Piece.EMPTY, promotedToPiece, Bitboard.EMPTY, Bitboard.EMPTY, Bitboard.EMPTY);
    }

    static ChessMove basicCapture(final long from, final long to, final int movingPiece, final int capturedPiece) {
        return new ChessMove(from, to, movingPiece, capturedPiece, Piece.EMPTY, Bitboard.EMPTY, Bitboard.EMPTY, Bitboard.EMPTY);
    }

    static ChessMove basicMove(final long from, final long to, final int movingPiece) {
        return new ChessMove(from, to, movingPiece, Piece.EMPTY, Piece.EMPTY, Bitboard.EMPTY, Bitboard.EMPTY, Bitboard.EMPTY);
    }

    @Override
    public boolean isQuiet() {
        return !isCapture();
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public int getMovingPiece() {
        return movingPiece;
    }

    public boolean isCapture() {
        return capturedPiece != Piece.EMPTY;
    }

    public int getCapturedPiece() {
        return capturedPiece;
    }

    public boolean isPromotion() {
        return promotedToPiece != Piece.EMPTY;
    }

    public int getPromotedToPiece() {
        return promotedToPiece;
    }

    public boolean isEnPassant() {
        return capturedEnPassantPawn != Bitboard.EMPTY;
    }

    public long getCapturedEnPassantPawn() {
        return capturedEnPassantPawn;
    }

    public boolean isDoublePawnPush() {
        return enPassantTarget != Bitboard.EMPTY;
    }

    public long getEnPassantTarget() {
        return enPassantTarget;
    }

    public boolean isCastle() {
        return castleRookMove != Bitboard.EMPTY;
    }

    public long getCastleRookMove() {
        return castleRookMove;
    }

    public boolean isPawnMove() {
        return Piece.isPawn(movingPiece);
    }

    public boolean isKingMove() {
        return Piece.isKing(movingPiece);
    }

    @Override
    public String toString() {
        return AlgebraNotation.fromBitboard(from) + AlgebraNotation.fromBitboard(to) + (isPromotion() ? Piece.toChar(promotedToPiece) : "");
    }

}
