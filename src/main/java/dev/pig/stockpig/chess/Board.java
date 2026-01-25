package dev.pig.stockpig.chess;

/**
 * Board stores all piece data for a game of chess.
 * Arrays of occupancy bitboards store all piece information, the index within the array encodes the
 * piece type.
 */
public final class Board {

    // TODO: Candidate optimisation: Test with no unoccupied board - obtain via negation of colour union
    private final long[] pieceBBs   = new long[PieceType.values().length];
    private final long[] colourBBs  = new long[Colour.values().length];


    // ====================================================================================================
    //                                  Constructors and Builders
    // ====================================================================================================

    private Board() {}

    /**
     * Create an empty chess board.
     * @return empty board
     */
    public static Board empty() {
        final Board board = new Board();
        board.pieceBBs[PieceType.EMPTY.ordinal()] |= Bitboard.ALL;
        return board;
    }


    // ====================================================================================================
    //                                  Add / Remove Pieces
    // ====================================================================================================

    /**
     * Add a piece to a square.
     * @param c colour
     * @param pt piece type
     * @param sq square
     */
    public void addPiece(final Colour c, final PieceType pt, final Square sq) {
        addPiece(c, pt, sq.bitboard());
    }

    /**
     * Add a piece to board using a bitboard.
     * @param c color
     * @param pt piece type
     * @param bitboard bitboard
     */
    public void addPiece(final Colour c, final PieceType pt, final long bitboard) {
        this.colourBBs[c.ordinal()]                 |=  bitboard;
        this.pieceBBs[pt.ordinal()]                 |=  bitboard;
        this.pieceBBs[PieceType.EMPTY.ordinal()]    &=~ bitboard;
    }

    /**
     * Remove the piece from the square.
     * @param c colour
     * @param pt piece type
     * @param sq square
     */
    public void removePiece(final Colour c, final PieceType pt, final Square sq) {
        removePiece(c, pt, sq.bitboard());
    }

    /**
     * Remove the piece from the bitboard.
     * @param c colour
     * @param pt piece type
     * @param bitboard bitboard
     */
    public void removePiece(final Colour c, final PieceType pt, final long bitboard) {
        this.colourBBs[c.ordinal()]                 &=~ bitboard;
        this.pieceBBs[pt.ordinal()]                 &=~ bitboard;
        this.pieceBBs[PieceType.EMPTY.ordinal()]    |=  bitboard;
    }


    // ====================================================================================================
    //                                  Piece Bitboard Accessors
    // ====================================================================================================

    /**
     * Get the occupancy bitboard for the colour.
     * @param c colour
     * @return bitboard
     */
    public long pieces(final Colour c) {
        return this.colourBBs[c.ordinal()];
    }

    /**
     * Get the occupancy bitboard for the piece type.
     * @param pt piece type
     * @return bitboard
     */
    public long pieces(final PieceType pt) {
        return this.pieceBBs[pt.ordinal()];
    }

    /**
     * Get the occupancy bitboard for the colour and piece type.
     * @param c colour
     * @param pt piece type
     * @return bitboard
     */
    public long pieces(final Colour c, final PieceType pt) {
        return pieces(c) & pieces(pt);
    }

    /**
     * Get the unoccupied bitboard.
     * @return bitboard
     */
    public long unoccupied() {
        return pieces(PieceType.EMPTY);
    }

    /**
     * Get the occupancy bitboard of all pieces.
     * @return bitboard
     */
    public long occupied() {
        return ~unoccupied();
    }


    // ====================================================================================================
    //                                  Square Queries
    // ====================================================================================================

    /**
     * Get the piece type at the bitboard.
     * @param bitboard bitboard
     * @return piece type
     */
    // TODO: Candidate optimisation: Test splitting into bespoke search function (if move generator calls this
    // TODO:                         it's because a piece is definitely here, maybe start at 1.
    public PieceType pieceType(final long bitboard) {
        for (int i = 0; i < this.pieceBBs.length; i++) {
            if (Bitboard.contains(this.pieceBBs[i], bitboard)) {
                return PieceType.of(i);
            }
        }
        return PieceType.EMPTY;
    }

    /**
     * Get the piece at the square.
     * @param sq square
     * @return piece
     */
    public Piece piece(final Square sq) {
        return piece(sq.bitboard());
    }

    /**
     * Get the piece at the bitboard.
     * @param bitboard bitboard
     * @return piece
     */
    public Piece piece(final long bitboard) {
        final PieceType pt = pieceType(bitboard);
        return Piece.of(Colour.of(Bitboard.contains(this.colourBBs[0], bitboard)), pt);
    }


    // ====================================================================================================
    //                                  Move Make / Unmake
    // ====================================================================================================

    /**
     * Apply the move for the colour.
     * @param c colour
     * @param move move
     */
    public void makeMove(final Colour c, final int move) {
        final Square from = Move.from(move);
        final Square to = Move.to(move);
        final PieceType mover = Move.mover(move);
        final PieceType capture = Move.capture(move);
        final PieceType promote = Move.promote(move);

        // If this was a capture remove that piece from the board
        if (capture != PieceType.EMPTY) {
            removePiece(c.flip(), capture, Move.isEnPassant(move) ? to.move(c.backward()) : to);
        }

        // Remove the moving piece from the's start location and add it to the destination
        removePiece(c, mover, from);
        addPiece(c, promote == PieceType.EMPTY ? mover : promote, to);

        // If it's a castle move then move the rook
        if (Move.isCastle(move)) {
            final long rookbits = Castling.getRookMoveBits(c, to);
            this.pieceBBs[PieceType.ROOK.ordinal()]     ^= rookbits;
            this.colourBBs[c.ordinal()]                 ^= rookbits;
            this.pieceBBs[PieceType.EMPTY.ordinal()]    ^= rookbits;
        }
    }

    /**
     * Unmake the move for the colour.
     * @param c colour
     * @param move move
     */
    public void unmakeMove(final Colour c, final int move) {
        final Square from = Move.from(move);
        final Square to = Move.to(move);
        final PieceType mover = Move.mover(move);
        final PieceType capture = Move.capture(move);
        final PieceType promote = Move.promote(move);

        // If it's a castle move then move the rook
        if (Move.isCastle(move)) {
            final long rookbits = Castling.getRookMoveBits(c, to);
            this.pieceBBs[PieceType.ROOK.ordinal()]     ^= rookbits;
            this.colourBBs[c.ordinal()]                 ^= rookbits;
            this.pieceBBs[PieceType.EMPTY.ordinal()]    ^= rookbits;
        }

        // Move the moving piece back to the source location (and un-promote if needed)
        removePiece(c, promote == PieceType.EMPTY ? mover : promote, to);
        addPiece(c, mover, from);

        // If this was a capture add that piece to the board
        if (capture != PieceType.EMPTY) {
            addPiece(c.flip(), capture, Move.isEnPassant(move) ? to.move(c.backward()) : to);
        }
    }


    // ====================================================================================================
    //                                  Dead Position
    // ====================================================================================================

    /**
     * Get whether the position is dead, insufficient material for a check mate.
     * @return is dead position
     */
    // TODO: Candidate optimisation: Look at
    public boolean isDeadPosition() {
        final int whiteTeamSize = Bitboard.count(pieces(Colour.WHITE));
        final int blackTeamSize = Bitboard.count(pieces(Colour.BLACK));

        if (whiteTeamSize > 2 || blackTeamSize > 2) return false; // A team has more than two pieces

        if (whiteTeamSize == 1 && blackTeamSize == 1) return true; // Just kings

        // Each team has at most 2 pieces and at least one team has 2 pieces...

        final boolean whiteKnight = pieces(Colour.WHITE, PieceType.KNIGHT) != Bitboard.EMPTY;
        final boolean whiteBishop = pieces(Colour.WHITE, PieceType.BISHOP) != Bitboard.EMPTY;
        if (whiteTeamSize == 2 && !whiteKnight && !whiteBishop) return false; // White has a piece that can mate

        final boolean blackKnight = pieces(Colour.BLACK, PieceType.KNIGHT) != Bitboard.EMPTY;
        final boolean blackBishop = pieces(Colour.BLACK, PieceType.BISHOP) != Bitboard.EMPTY;
        if (blackTeamSize == 2 && !blackKnight && !blackBishop) return false; // Black has a piece that can mate

        // Only Kings, Bishops and Knights...

        if (whiteTeamSize + blackTeamSize == 3) return true; // King vs King + Knight or Bishop

        // King + Knight or Bishop vs King + Knight or Bishop...

        if (whiteKnight || blackKnight) return false; // While unlikely, checkmate is possible with a knight

        // King + Bishop vs King + Bishop...

        // Dead if bishops are on the same colour squares
        final int bishopsOnWhite = Bitboard.count(pieces(PieceType.BISHOP) & Bitboard.WHITE_SQUARES);
        return bishopsOnWhite == 0 || bishopsOnWhite == 2;
    }


    // ====================================================================================================
    //                                  Utils
    // ====================================================================================================

    /**
     * Create a chess board from the material part of a FEN string.
     * @param fen board part fen string
     * @return board
     */
    public static Board fromFen(final String fen) {
        final Board board = empty();

        int sq = 56;
        for (int i = 0; i < fen.length(); i++) {
            final char c = fen.charAt(i);

            if      (c == '/')              sq -= 16;
            else if (Character.isDigit(c))  sq += Character.digit(c, 10);
            else {
                board.addPiece(Colour.of(Character.isUpperCase(c)), PieceType.fromChar(c), Square.of(sq));
                sq++;
            }
        }
        return board;
    }

    /**
     * Create a pretty printed debug string of the board.
     * @return pretty debug string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        Rank.forEach(r -> {
            File.forEach(f ->
                    sb.append(piece(Square.of(f, r))).append(" "));
            sb.append("\n");
        });
        return sb.toString();
    }
}

