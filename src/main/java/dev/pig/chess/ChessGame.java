package dev.pig.chess;

import dev.pig.game.PartisanGame;

/**
 * Interface for a game of chess implemented using bitboards.
 *
 * @see Bitboard
 */
public interface ChessGame extends PartisanGame<ChessMove> {

    /**
     * Return value representing who has won or if it's a draw.
     * Should be used in conjunction with {@link #isGameOver()} to avoid
     * active games being marked as a stalemate.
     * <ol>
     *     <li>-1 = Black has won</li>
     *     <li>0 = Draw or game is not over</li>
     *     <li>1 = White has won</li></>
     * </ol>
     *
     * @return winner value
     */
    int getWinner();

    /**
     * If the current moving player is in checkmate.
     *
     * @return is moving player in checkmate
     */
    boolean isCheckMate();

    /**
     * Get whether the current moving player is in check.
     *
     * @return is moving player in check
     */
    boolean isCheck();

    @Override
    default boolean isQuiet() {
        return !isCheck();
    }

    /**
     * Whether it is currently white's turn to move.
     *
     * @return is white's turn to move
     */
    boolean isWhiteTurn();

    /**
     * Is player ones turn; is white's turn.
     *
     * @return is player ones turn
     */
    @Override
    default boolean isPlayerOneTurn() {
        return isWhiteTurn();
    }

    /**
     * Decide if the board is a dead position (always stalemate).
     *
     * @return whether the board is in a dead position
     */
    boolean isDeadPosition();

    /**
     * Get the occupancy bitboard for a given piece.
     *
     * @param piece piece
     * @return bitboard
     */
    long getPieceBitboard(final int piece);

    /**
     * Get the piece at a given bit.
     *
     * @param bitboard bit to retrieve piece for
     * @return piece
     */
    int getPieceAtBit(final long bitboard);

    /**
     * Get the piece at the given square index.
     *
     * @param index square index
     * @return piece
     */
    default int getPieceAtIndex(final int index) {
        return getPieceAtBit(Bitboard.INDEX[index]);
    }

    /**
     * Get the piece at the given algebra notation square.
     *
     * @param algebra algebra notation square
     * @return piece
     */
    default int getPieceAtAlgebraNotation(final String algebra) {
        return getPieceAtBit(AlgebraNotation.toBitboard(algebra));
    }

    // Useful/debug values

    /**
     * Current turn number.
     *
     * @return turn number
     */
    int getTurnNumber();

    /**
     * Turns since pawn push or a capture.
     *
     * @return turns since push/capture
     */
    int getTurnsSincePushOrCapture();

    /**
     * Get the bitmap of the current castles allowed.
     *
     * @return bitmap of castles allowed
     * @see Castling
     */
    int getCastlesPossible();

    /**
     * Get the current target square of an en passant.
     * Only present directly after a double pawn push move.
     *
     * @return en passant target bitboard
     */
    long getEnPassantTarget();

    /**
     * Get bitboard of squares currently threatened by the non-moving player.
     *
     * @return threat bitboard
     */
    long getThreatenedSquares();

    /**
     * Get the squares in which the moving team is allowed to move.
     * When NOT in check:
     * - All squares not occupied by an allied piece
     * When in check:
     * - Squares that can be moved/captured to block check
     *
     * @return movable squares for the current moving player
     */
    long getMovableSquares();

    /**
     * Get a bitboard of 'pin rays'.
     * A 'pin ray' starts at the enemy piece's square and ends at the king (excluding the king square).
     * Contains exactly one allied piece (the pinned piece), per ray.
     *
     * @return bitboard of pin rays
     */
    long getPinSquares();

    // -- Utility --

    /**
     * Convert the game into a full FEN string.
     *
     * @return fen
     */
    String toFen();

    /**
     * Parse a Fen string into a game.
     *
     * @param fen fen
     * @return game
     */
    static ChessGame fromFen(final String fen) {
        return Fen.toGame(fen);
    }

    /**
     * Game with standard chess set up.
     *
     * @return standard chess game
     */
    static ChessGame standard() {
        return ChessGameImpl.standard();
    }

    /**
     * Get a debug string, view the chess board from a CLI.
     *
     * @return debug string
     */
    String debugString();

}
