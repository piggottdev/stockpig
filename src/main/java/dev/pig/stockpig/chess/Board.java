package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Bitboard;

import java.util.Arrays;

/**
 * Board stores all piece/material state for a chess position.
 * Arrays of occupancy bitboards store all piece information; the index within the array encodes the piece type.
 */
public final class Board {

    private final long[] pieceBBs   = new long[7];
    private final long[] colourBBs  = new long[2];

    private final byte[] squares = new byte[64];


    // ====================================================================================================
    //                                  Constructors and Builders
    // ====================================================================================================

    private Board() {
        this.pieceBBs[PieceType.EMPTY] = Bitboard.ALL;
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
    public void addPiece(final boolean c, final byte pt, final byte sq) {
        final long bitboard = Bitboard.ofSquare(sq);
        this.squares[sq]                =   pt;
        this.colourBBs[c ? 1 : 0]       |=  bitboard;
        this.pieceBBs[pt]               |=  bitboard;
        this.pieceBBs[PieceType.EMPTY]  &=~ bitboard;
    }

    /**
     * Remove a piece from a square.
     * @param c colour
     * @param pt piece type
     * @param sq square
     */
    public void removePiece(final boolean c, final byte pt, final byte sq) {
        final long bitboard = Bitboard.ofSquare(sq);
        this.squares[sq]                  =   PieceType.EMPTY;
        this.colourBBs[c ? 1 : 0]         &=~ bitboard;
        this.pieceBBs[pt]                 &=~ bitboard;
        this.pieceBBs[PieceType.EMPTY]    |=  bitboard;
    }


    // ====================================================================================================
    //                                  Piece Bitboard Accessors
    // ====================================================================================================

    /**
     * Get the occupancy bitboard for the colour.
     * @param c colour
     * @return bitboard
     */
    public long pieces(final boolean c) {
        return this.colourBBs[c ? 1 : 0];
    }

    /**
     * Get the occupancy bitboard for the piece type.
     * @param pt piece type
     * @return bitboard
     */
    public long pieces(final byte pt) {
        return this.pieceBBs[pt];
    }

    /**
     * Get the occupancy bitboard for the colour and piece type.
     * @param c colour
     * @param pt piece type
     * @return bitboard
     */
    public long pieces(final boolean c, final byte pt) {
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
     * Get the occupied bitboard.
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
    public byte pieceAt(final byte sq) {
        return this.squares[sq];
    }

    // ====================================================================================================
    //                                  Move Make / Unmake
    // ====================================================================================================

    /**
     * Apply the move for the colour.
     * @param c colour
     * @param move move
     */
    public void makeMove(final boolean c, final int move) {
        final byte from    = Move.from(move);
        final byte to      = Move.to(move);
        final byte mover   = Move.mover(move);
        final byte capture = Move.capture(move);
        final byte promote = Move.promote(move);

        // If this was a capture remove that piece from the board
        if (capture != PieceType.EMPTY) {
            removePiece(Colour.flip(c), capture, Move.isEnPassant(move) ? (byte) (to + Colour.backward(c).offset()) : to);
        }

        // Remove the moving piece from the start location and add it to the destination
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
    public void unmakeMove(final boolean c, final int move) {
        final byte from     = Move.from(move);
        final byte to       = Move.to(move);
        final byte mover    = Move.mover(move);
        final byte capture  = Move.capture(move);
        final byte promote  = Move.promote(move);

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
            addPiece(Colour.flip(c), capture, Move.isEnPassant(move) ? (byte) (to + Colour.backward(c).offset()) : to);
        }
    }


    // ====================================================================================================
    //                                  Dead Position
    // ====================================================================================================

    /**
     * Returns whether the position is a dead position (insufficient material for checkmate).
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
}

