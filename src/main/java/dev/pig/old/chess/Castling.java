package dev.pig.old.chess;

/**
 * An int bit array is used to show whether castling is possible for each team/side.
 * This class is a helper class to extract data from the bit array and to help with castling logic.
 */
public class Castling {

    // Bit maps showing whether castling each side is possible
    static final int ALL_ALLOWED = 0b00001111;

    private static final int NONE_ALLOWED = 0;
    private static final int[] BLACK_CASTLE_ALLOWED = {0b00000001, 0b00000010, 0b00000011};
    private static final int[] WHITE_CASTLE_ALLOWED = {0b00000100, 0b00001000, 0b00001100};

    private static final int KING_SIDE = 0;
    private static final int QUEEN_SIDE = 1;
    private static final int EITHER_SIDE = 2; // Only relevant for bit maps above

    // Pre-computed squares for empty squares required to castle,
    private static final int EMPTY_SQUARES = 0;
    private static final int CHECK_SQUARES = 1;
    private static final int ROOK_STARTS = 2;
    private static final long[][] BLACK_CASTLING = initBlackCastling();
    private static final long[][] WHITE_CASTLING = initWhiteCastling();

    // Pre-created moves for each castle
    private static final ChessMove[] WHITE_MOVES = {
            ChessMove.castle(Bitboard.INDEX[4], Bitboard.INDEX[6], Piece.WHITE | Piece.KING, Bitboard.INDEX[5] | Bitboard.INDEX[7]),
            ChessMove.castle(Bitboard.INDEX[4], Bitboard.INDEX[2], Piece.WHITE | Piece.KING, Bitboard.INDEX[0] | Bitboard.INDEX[3])
    };
    private static final ChessMove[] BLACK_MOVES = {
            ChessMove.castle(Bitboard.INDEX[60], Bitboard.INDEX[62], Piece.BLACK | Piece.KING, Bitboard.INDEX[61] | Bitboard.INDEX[63]),
            ChessMove.castle(Bitboard.INDEX[60], Bitboard.INDEX[58], Piece.BLACK | Piece.KING, Bitboard.INDEX[56] | Bitboard.INDEX[59])
    };

    private static final char BLACK_KING_SIDE = 'k';
    private static final char BLACK_QUEEN_SIDE = 'q';
    private static final char WHITE_KING_SIDE = 'K';
    private static final char WHITE_QUEEN_SIDE = 'Q';

    private Castling() {}

    /**
     * Updates the allowed castles bitmap after a given move.
     *
     * @param currentCastlesPossible castles allowed before the move
     * @param move move performed
     * @param isWhiteTurn which team performed the move
     * @return new bitmap of allowed castles
     */
    static int getCastlesAllowedAfterMove(int currentCastlesPossible, final ChessMove move, final boolean isWhiteTurn) {
        if (currentCastlesPossible == NONE_ALLOWED) return NONE_ALLOWED;

        // Check if the move affects the movers castle possibilities
        if (isAllowed(currentCastlesPossible, isWhiteTurn, EITHER_SIDE)) {
            if (move.isKingMove()) {
                currentCastlesPossible = removeCastlePossible(currentCastlesPossible, isWhiteTurn, EITHER_SIDE);
            } else if (move.getFrom() == get(ROOK_STARTS, isWhiteTurn, KING_SIDE)) {
                currentCastlesPossible = removeCastlePossible(currentCastlesPossible, isWhiteTurn, KING_SIDE);
            } else if (move.getFrom() == get(ROOK_STARTS, isWhiteTurn, QUEEN_SIDE)) {
                currentCastlesPossible = removeCastlePossible(currentCastlesPossible, isWhiteTurn, QUEEN_SIDE);
            }
        }

        // Check if the move affects the enemies castle possibilities
        if (isAllowed(currentCastlesPossible, !isWhiteTurn, EITHER_SIDE)) {
            if (move.getTo() == get(ROOK_STARTS, !isWhiteTurn, KING_SIDE)) {
                currentCastlesPossible = removeCastlePossible(currentCastlesPossible, !isWhiteTurn, KING_SIDE);
            } else if (move.getTo() == get(ROOK_STARTS, !isWhiteTurn, QUEEN_SIDE)) {
                currentCastlesPossible = removeCastlePossible(currentCastlesPossible, !isWhiteTurn, QUEEN_SIDE);
            }
        }

        return currentCastlesPossible;
    }

    /**
     * Get the king side castle move for the given team, only if it is possible.
     * If not possible return null.
     *
     * @param currentCastlesPossible bitmap of which castles are allowed
     * @param isWhiteTurn is it white's turn
     * @param unoccupiedSquares unoccupied squares bitboard
     * @param checkedSquares threatened/checked squares bitboard
     * @return king side castle move OR null
     */
    static ChessMove getKingSideCastleIfPossible(final int currentCastlesPossible, final boolean isWhiteTurn, final long unoccupiedSquares, final long checkedSquares) {
        return getCastleMoveIfPossible(KING_SIDE, currentCastlesPossible, isWhiteTurn, unoccupiedSquares, checkedSquares);
    }

    /**
     * Get the queen side castle move for the given team, only if it is possible.
     * If not possible return null.
     *
     * @param currentCastlesPossible bitmap of which castles are allowed
     * @param isWhiteTurn is it white's turn
     * @param unoccupiedSquares unoccupied squares bitboard
     * @param checkedSquares threatened/checked squares bitboard
     * @return queen side castle move OR null
     */
    static ChessMove getQueenSideCastleIfPossible(final int currentCastlesPossible, final boolean isWhiteTurn, final long unoccupiedSquares, final long checkedSquares) {
        return getCastleMoveIfPossible(QUEEN_SIDE, currentCastlesPossible, isWhiteTurn, unoccupiedSquares, checkedSquares);
    }

    private static ChessMove getCastleMoveIfPossible(final int side, final int currentCastlesPossible, final boolean isWhiteTurn, final long unoccupiedSquares, final long checkedSquares) {
        if (!isAllowed(currentCastlesPossible, isWhiteTurn, side)) return null; // Castling not allowed this side

        final long emptySquaresRequired = get(EMPTY_SQUARES, isWhiteTurn, side);
        if (!Bitboard.contains(unoccupiedSquares, emptySquaresRequired)) return null; // Squares required to be empty are not

        final long squaresRequiredFreeOfCheck = get(CHECK_SQUARES, isWhiteTurn, side);
        if (Bitboard.intersects(checkedSquares, squaresRequiredFreeOfCheck)) return null; //One of the squares the king must move through is in check

        return isWhiteTurn ? WHITE_MOVES[side] : BLACK_MOVES[side];
    }

    private static long get(final int constant, final boolean isWhite, final int side) {
        return isWhite ? WHITE_CASTLING[side][constant] : BLACK_CASTLING[side][constant];
    }

    private static boolean isAllowed(final int currentCastlesPossible, final boolean isWhite, final int side) {
        return (currentCastlesPossible & getCastlePossibleBits(isWhite, side)) != 0;
    }

    private static int removeCastlePossible(final int currentsCastlesPossible, final boolean isWhite, final int side) {
        return (currentsCastlesPossible & ~getCastlePossibleBits(isWhite, side));
    }

    private static int getCastlePossibleBits(final boolean isWhite, final int side) {
        return isWhite ? WHITE_CASTLE_ALLOWED[side] : BLACK_CASTLE_ALLOWED[side];
    }

    public static int fromString(final String fen) {
        int details = 0;
        for (int i = 0; i < fen.length(); i++) {
            switch (fen.charAt(i)) {
                case WHITE_KING_SIDE -> details |= WHITE_CASTLE_ALLOWED[KING_SIDE];
                case WHITE_QUEEN_SIDE -> details |= WHITE_CASTLE_ALLOWED[QUEEN_SIDE];
                case BLACK_KING_SIDE -> details |= BLACK_CASTLE_ALLOWED[KING_SIDE];
                case BLACK_QUEEN_SIDE -> details |= BLACK_CASTLE_ALLOWED[QUEEN_SIDE];
            }
        }
        return details;
    }

    public static String toString(final int details) {
        if (details == 0L) return "-";
        String str = "";

        if (isAllowed(details, true, KING_SIDE)) str = str + WHITE_KING_SIDE;
        if (isAllowed(details, true, QUEEN_SIDE)) str = str + WHITE_QUEEN_SIDE;
        if (isAllowed(details, false, KING_SIDE)) str = str + BLACK_KING_SIDE;
        if (isAllowed(details, false, QUEEN_SIDE)) str = str + BLACK_QUEEN_SIDE;

        return str;
    }

    // -- Constant Initialisation --

    private static long[][] initBlackCastling() {
        final long[][] castling = new long[2][3];

        castling[KING_SIDE][EMPTY_SQUARES]  = 0b01100000L << 56;
        castling[KING_SIDE][CHECK_SQUARES]  = 0b01110000L << 56;
        castling[KING_SIDE][ROOK_STARTS]    = 0b10000000L << 56;

        castling[QUEEN_SIDE][EMPTY_SQUARES] = 0b00001110L << 56;
        castling[QUEEN_SIDE][CHECK_SQUARES] = 0b00011100L << 56;
        castling[QUEEN_SIDE][ROOK_STARTS]   = 0b00000001L << 56;

        return castling;
    }

    private static long[][] initWhiteCastling() {
        final long[][] castling = new long[2][3];

        castling[KING_SIDE][EMPTY_SQUARES]  = 0b01100000L;
        castling[KING_SIDE][CHECK_SQUARES]  = 0b01110000L;
        castling[KING_SIDE][ROOK_STARTS]    = 0b10000000L;

        castling[QUEEN_SIDE][EMPTY_SQUARES] = 0b00001110L;
        castling[QUEEN_SIDE][CHECK_SQUARES] = 0b00011100L;
        castling[QUEEN_SIDE][ROOK_STARTS]   = 0b00000001L;

        return castling;
    }

}
