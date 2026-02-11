package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Bitboard;

import java.util.Arrays;

/**
 * Board stores all piece/material state for a chess position.
 * Arrays of occupancy bitboards store all piece information, the index within the array encodes the
 * piece type.
 */
public final class Board {

    private final long[] pieceBBs   = new long[PieceType.values().length];
    private final long[] colourBBs  = new long[Colour.values().length];

    private final PieceType[] squares = new PieceType[64];


    // ====================================================================================================
    //                                  Constructors and Builders
    // ====================================================================================================

    private Board() {
        this.pieceBBs[PieceType.EMPTY.ordinal()] |= Bitboard.ALL;
        Arrays.fill(this.squares, PieceType.EMPTY);
    }

    /**
     * Create an empty chess board.
     * @return empty board
     */
    public static Board empty() {
        return new Board();
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
    public void addPiece(final Colour c, final PieceType pt, final int sq) {
        final long bitboard = Bitboard.ofSquare(sq);
        this.squares[sq] = pt;
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
    public void removePiece(final Colour c, final PieceType pt, final int sq) {
        final long bitboard = Bitboard.ofSquare(sq);
        this.squares[sq] = PieceType.EMPTY;
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
     * Get the piece type at the square.
     * @param sq square
     * @return piece type
     */
    public PieceType pieceType(final int sq) {
        return this.squares[sq];
    }

    /**
     * Get the piece at the square.
     * @param sq square
     * @return piece
     */
    public Piece piece(final int sq) {
        return Piece.of(
                Colour.of(Bitboard.intersects(Bitboard.ofSquare(sq), this.colourBBs[Colour.WHITE.ordinal()])),
                pieceType(sq)
        );
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
        final int from = Move.from(move);
        final int to = Move.to(move);
        final PieceType mover = Move.mover(move);
        final PieceType capture = Move.capture(move);
        final PieceType promote = Move.promote(move);

        // If this was a capture remove that piece from the board
        if (capture != PieceType.EMPTY) {
            removePiece(c.flip(), capture, Move.isEnPassant(move) ? to + c.backward().offset() : to);
        }

        // Remove the moving piece from the's start location and add it to the destination
        removePiece(c, mover, from);
        addPiece(c, promote == PieceType.EMPTY ? mover : promote, to);

        // If it's a castle move then move the rook
        if (Move.isCastle(move)) {
            removePiece(c, PieceType.ROOK, Castling.getRookFrom(c, to));
            addPiece(c, PieceType.ROOK, Castling.getRookTo(c, to));
        }
    }

    /**
     * Unmake the move for the colour.
     * @param c colour
     * @param move move
     */
    public void unmakeMove(final Colour c, final int move) {
        final int from = Move.from(move);
        final int to = Move.to(move);
        final PieceType mover = Move.mover(move);
        final PieceType capture = Move.capture(move);
        final PieceType promote = Move.promote(move);

        // If it's a castle move then move the rook
        if (Move.isCastle(move)) {
            removePiece(c, PieceType.ROOK, Castling.getRookTo(c, to));
            addPiece(c, PieceType.ROOK, Castling.getRookFrom(c, to));
        }

        // Move the moving piece back to the source location (and un-promote if needed)
        removePiece(c, promote == PieceType.EMPTY ? mover : promote, to);
        addPiece(c, mover, from);

        // If this was a capture add that piece to the board
        if (capture != PieceType.EMPTY) {
            addPiece(c.flip(), capture, Move.isEnPassant(move) ? to + c.backward().offset() : to);
        }
    }


    // ====================================================================================================
    //                                  Dead Position
    // ====================================================================================================

    /**
     * Get whether the position is dead, insufficient material for a checkmate.
     * @return is dead position
     */
    public boolean isDeadPosition() {
        // Any pawn, rook, or queen on board => not dead
        if ((pieces(PieceType.PAWN) | pieces(PieceType.ROOK) | pieces(PieceType.QUEEN)) != 0L) return false;

        final long knights = pieces(PieceType.KNIGHT);
        final long bishops = pieces(PieceType.BISHOP);
        final long minors = knights | bishops;

        final int minorCount = Bitboard.count(minors);

        // More than 2 minor pieces
        if (minorCount > 2) return false;

        // Kings only / King vs King + Minor
        if (minorCount == 0 || minorCount == 1) return true;

        // 2 Minor pieces...

        // King + Knight vs King + Knight or Bishop
        if (knights != Bitboard.EMPTY) return false;

        // 2 Bishops...

        // Bishops are on same team
        final long whiteBishops = bishops & pieces(Colour.WHITE);
        if (whiteBishops == bishops || whiteBishops == Bitboard.EMPTY) return false;

        // Dead if bishops are on same colour
        final long bishopsOnWhite = bishops & Bitboard.WHITE_SQUARES;
        return bishopsOnWhite == bishops || bishopsOnWhite == Bitboard.EMPTY;
    }


    // ====================================================================================================
    //                                  Utils
    // ====================================================================================================

    /**
     * Create a pretty printed debug string of the board.
     * @return pretty debug string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                final int sq = rank*8+file;
                sb.append(piece(sq)).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

