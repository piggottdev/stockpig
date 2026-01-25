package dev.pig.old.chess;

/**
 * Stores all square and piece data of a chess board.
 * An array of longs is used with each long being an occupancy bitboard of each piece/team.
 * The index of the array represents the piece.
 * A redundant mailbox array is also used to speed up identifying which piece (if any) is at a
 * given square index.
 *
 * @see Piece
 * @see Bitboard
 */
class Board {

    private final long[] pieceBitboards;
    private final int[] pieceSquares;

    private Board(final long[] pieceBitboards, final int[] pieceSquares) {
        this.pieceBitboards = pieceBitboards;
        this.pieceSquares = pieceSquares;
    }

    /**
     * Board with standard set up.
     *
     * @return board with standard starting position
     */
    static Board standard() {
        final Board board = Board.empty();
        board.addPiece(Piece.WHITE | Piece.ROOK, Bitboard.INDEX[0] | Bitboard.INDEX[7]);
        board.addPiece(Piece.WHITE | Piece.KNIGHT, Bitboard.INDEX[1] | Bitboard.INDEX[6]);
        board.addPiece(Piece.WHITE | Piece.BISHOP, Bitboard.INDEX[2] | Bitboard.INDEX[5]);
        board.addPiece(Piece.WHITE | Piece.QUEEN, Bitboard.INDEX[3]);
        board.addPiece(Piece.WHITE | Piece.KING, Bitboard.INDEX[4]);
        board.addPiece(Piece.WHITE | Piece.PAWN, Bitboard.RANKS[1]);

        board.addPiece(Piece.ROOK, Bitboard.INDEX[56] | Bitboard.INDEX[63]);
        board.addPiece(Piece.KNIGHT, Bitboard.INDEX[57] | Bitboard.INDEX[62]);
        board.addPiece(Piece.BISHOP, Bitboard.INDEX[58] | Bitboard.INDEX[61]);
        board.addPiece(Piece.QUEEN, Bitboard.INDEX[59]);
        board.addPiece(Piece.KING, Bitboard.INDEX[60]);
        board.addPiece(Piece.PAWN, Bitboard.RANKS[6]);

        return board;
    }

    /**
     * Board with no pieces.
     *
     * @return empty board
     */
    static Board empty() {
        final long[] pieceBitboards = new long[15];
        final int[] pieceSquares = new int[64];
        pieceBitboards[Piece.UNOCCUPIED] = Bitboard.ALL;
        for (int i = 0; i < 64; i++) {
            pieceSquares[i] = Piece.UNOCCUPIED;
        }
        return new Board(pieceBitboards, pieceSquares);
    }

    /**
     * Decide if the board is a dead position (always stalemate).
     *
     * @return whether the board is in a dead position
     */
    boolean isDeadPosition() {
        int whiteTeamSize = Long.bitCount(getPieceBitboard(Piece.WHITE));
        int blackTeamSize = Long.bitCount(getPieceBitboard(Piece.BLACK));

        if (whiteTeamSize > 2 || blackTeamSize > 2) return false; //A team has more than two pieces

        //Each team has at most 2 pieces...

        if (whiteTeamSize == 1 && blackTeamSize == 1) return true; //Just the kings

        boolean whiteKnight = (getPieceBitboard(Piece.WHITE | Piece.KNIGHT) != 0);
        boolean whiteBishop = (getPieceBitboard(Piece.WHITE | Piece.BISHOP) != 0);
        boolean blackKnight = (getPieceBitboard(Piece.KNIGHT) != 0);
        boolean blackBishop = (getPieceBitboard(Piece.BISHOP) != 0);

        if (whiteTeamSize == 2 && !whiteKnight && !whiteBishop) return false; //White has a piece that can check
        if (blackTeamSize == 2 && !blackKnight && !blackBishop) return false; //Black has a piece that can check

        if (blackTeamSize + whiteTeamSize == 3) return true; //King vs King+Minor Piece

        //Each team has a minor piece...

        if (whiteKnight || blackKnight) return false;

        //Bishop vs bishop...

        boolean whiteBishopOnBlackSquare = Bitboard.intersects(getPieceBitboard(Piece.WHITE | Piece.BISHOP), Bitboard.BLACK_SQUARES);
        boolean blackBishopOnBlackSquare = Bitboard.intersects(getPieceBitboard(Piece.BISHOP), Bitboard.BLACK_SQUARES);

        return (whiteBishopOnBlackSquare == blackBishopOnBlackSquare); //If they are on the same colour, dead
    }

    // -- Pieces --

    /**
     * Get the occupancy bitboard for a given piece.
     *
     * @param piece piece
     * @return bitboard
     */
    long getPieceBitboard(final int piece) {
        return this.pieceBitboards[piece];
    }

    /**
     * Add a piece at bitboard location.
     *
     * @param piece piece
     * @param bitboard bitboard
     */
    void addPiece(final int piece, final long bitboard) {
        this.pieceBitboards[piece] |= bitboard;
        this.pieceBitboards[Piece.getTeamOnly(piece)] |= bitboard;
        this.pieceBitboards[Piece.UNOCCUPIED] &= ~bitboard;
        Bitboard.forEachBit(bitboard, b -> this.pieceSquares[Bitboard.toIndex(b)] =  piece);
    }

    /**
     * Remove a piece at bitboard location.
     *
     * @param piece piece
     * @param bitboard bitboard
     */
    void removePiece(final int piece, final long bitboard) {
        this.pieceBitboards[piece] &= ~bitboard;
        this.pieceBitboards[Piece.getTeamOnly(piece)] &= ~bitboard;
        this.pieceBitboards[Piece.UNOCCUPIED] |= bitboard;
        Bitboard.forEachBit(bitboard, b -> this.pieceSquares[Bitboard.toIndex(b)] = Piece.UNOCCUPIED);
    }

    /**
     * Get the piece at a given bit.
     *
     * @param bitboard bit to retrieve piece for
     * @return piece
     */
    int getPieceAtBit(final long bitboard) {
        return getPieceAtIndex(Bitboard.toIndex(bitboard));
    }

    /**
     * Get the piece at a given square index.
     *
     * @param index square index
     * @return piece
     */
    int getPieceAtIndex(final int index) {
        return this.pieceSquares[index];
    }

    // -- Moves --

    /**
     * Apply a move to the board.
     *
     * @param move move
     */
    void applyMove(final ChessMove move) {
        // Remove the moving piece from it's start location
        this.pieceBitboards[move.getMovingPiece()] &= ~move.getFrom();
        this.pieceBitboards[Piece.UNOCCUPIED] |= move.getFrom();
        this.pieceSquares[Bitboard.toIndex(move.getFrom())] = Piece.UNOCCUPIED;

        // Flip the teams To and From bits
        this.pieceBitboards[Piece.getTeamOnly(move.getMovingPiece())] ^= (move.getFrom() | move.getTo());

        // Mark the To bit as occupied
        this.pieceBitboards[Piece.UNOCCUPIED] &= ~move.getTo();

        // Add the correct piece at the To location
        if (move.isPromotion()) {
            this.pieceBitboards[move.getPromotedToPiece()] |= move.getTo();
            this.pieceSquares[Bitboard.toIndex(move.getTo())] = move.getPromotedToPiece();
        } else {
            this.pieceBitboards[move.getMovingPiece()] |= move.getTo();
            this.pieceSquares[Bitboard.toIndex(move.getTo())] = move.getMovingPiece();
        }

        // Remove any captured pieces, if en passant then the target square is different
        if (move.isEnPassant()) {
            this.pieceBitboards[move.getCapturedPiece()] &= ~move.getCapturedEnPassantPawn();
            this.pieceBitboards[Piece.getTeamOnly(move.getCapturedPiece())] &= ~move.getCapturedEnPassantPawn();
            this.pieceBitboards[Piece.UNOCCUPIED] |= move.getCapturedEnPassantPawn();
            this.pieceSquares[Bitboard.toIndex(move.getCapturedEnPassantPawn())] = Piece.UNOCCUPIED;
        } else if (move.isCapture()) {
            this.pieceBitboards[move.getCapturedPiece()] &= ~move.getTo();
            this.pieceBitboards[Piece.getTeamOnly(move.getCapturedPiece())] &= ~move.getTo();
        }

        // Apply the castle move if present
        if (move.isCastle()) {
            final long rookFrom = move.getCastleRookMove() & this.pieceBitboards[Piece.getSameTeamRook(move.getMovingPiece())];
            final long rookTo = move.getCastleRookMove() ^ rookFrom;
            this.pieceBitboards[Piece.getSameTeamRook(move.getMovingPiece())] ^= move.getCastleRookMove();
            this.pieceBitboards[Piece.getTeamOnly(move.getMovingPiece())] ^= move.getCastleRookMove();
            this.pieceBitboards[Piece.UNOCCUPIED] ^= move.getCastleRookMove();
            this.pieceSquares[Bitboard.toIndex(rookFrom)] = Piece.UNOCCUPIED;
            this.pieceSquares[Bitboard.toIndex(rookTo)] = Piece.getSameTeamRook(move.getMovingPiece());
        }
    }

    /**
     * Undo move from board.
     *
     * @param move move
     */
    void undoMove(final ChessMove move) {
        // Add the moving piece back where it started
        this.pieceBitboards[move.getMovingPiece()] |= move.getFrom();
        this.pieceBitboards[Piece.UNOCCUPIED] &= ~move.getFrom();
        this.pieceSquares[Bitboard.toIndex(move.getFrom())] = move.getMovingPiece();

        // Flip the teams To and From bits
        this.pieceBitboards[Piece.getTeamOnly(move.getMovingPiece())] ^= (move.getFrom() | move.getTo());

        // Remove the correct piece from the To location
        if (move.isPromotion()) {
            this.pieceBitboards[move.getPromotedToPiece()] &= ~move.getTo();
        } else {
            this.pieceBitboards[move.getMovingPiece()] &= ~move.getTo();
        }
        this.pieceBitboards[Piece.UNOCCUPIED] |= move.getTo();
        this.pieceSquares[Bitboard.toIndex(move.getTo())] = Piece.UNOCCUPIED;

        // Undo the capture if present
        if (move.isEnPassant()) {
            this.pieceBitboards[move.getCapturedPiece()] |= move.getCapturedEnPassantPawn();
            this.pieceBitboards[Piece.getTeamOnly(move.getCapturedPiece())] |= move.getCapturedEnPassantPawn();
            this.pieceBitboards[Piece.UNOCCUPIED] &= ~move.getCapturedEnPassantPawn();
            this.pieceSquares[Bitboard.toIndex(move.getCapturedEnPassantPawn())] = move.getCapturedPiece();
        } else if (move.isCapture()) {
            this.pieceBitboards[move.getCapturedPiece()] |= move.getTo();
            this.pieceBitboards[Piece.getTeamOnly(move.getCapturedPiece())] |= move.getTo();
            this.pieceBitboards[Piece.UNOCCUPIED] &= ~move.getTo();
            this.pieceSquares[Bitboard.toIndex(move.getTo())] = move.getCapturedPiece();
        }

        // Undo the castle move if present
        if (move.isCastle()) {
            final long rookTo = move.getCastleRookMove() & this.pieceBitboards[Piece.getSameTeamRook(move.getMovingPiece())];
            final long rookFrom = move.getCastleRookMove() ^ rookTo;
            this.pieceBitboards[Piece.getSameTeamRook(move.getMovingPiece())] ^= move.getCastleRookMove();
            this.pieceBitboards[Piece.getTeamOnly(move.getMovingPiece())] ^= move.getCastleRookMove();
            this.pieceBitboards[Piece.UNOCCUPIED] ^= move.getCastleRookMove();
            this.pieceSquares[Bitboard.toIndex(rookTo)] = Piece.UNOCCUPIED;
            this.pieceSquares[Bitboard.toIndex(rookFrom)] = Piece.getSameTeamRook(move.getMovingPiece());
        }
    }

    // -- Debug String --

    private static final String LINE_ROW = "+---+---+---+---+---+---+---+---+\n";
    private static final String PADDING_ROW = "|   |   |   |   |   |   |   |   |\n";
    private static final String ROW_START = "| ";
    private static final String ROW_END = " |\n";
    private static final String CELL_BORDER = " | ";

    String debugString() {
        final StringBuilder boardString = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            boardString.append(LINE_ROW);
            boardString.append(PADDING_ROW);
            boardString.append(ROW_START);

            long bit = 1L;
            bit <<= ((i * 8));

            for (int j = 0; j < 7; j++) {
                boardString.append(Piece.toChar(getPieceAtBit(bit)));
                boardString.append(CELL_BORDER);
                bit <<= 1;
            }
            boardString.append(Piece.toChar(getPieceAtBit(bit)));

            boardString.append(ROW_END);
            boardString.append(PADDING_ROW);
        }
        boardString.append(LINE_ROW);

        return boardString.toString();
    }

}
