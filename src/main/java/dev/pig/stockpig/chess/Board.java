package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.File;
import dev.pig.stockpig.chess.bitboard.Rank;
import dev.pig.stockpig.chess.bitboard.Square;

import java.util.Arrays;

/**
 * Board stores all piece data for a game of chess.
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
    public void addPiece(final Colour c, final PieceType pt, final Square sq) {
        final long bitboard = sq.bitboard();
        this.squares[sq.ordinal()] = pt;
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
        final long bitboard = sq.bitboard();
        this.squares[sq.ordinal()] = PieceType.EMPTY;
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
    public PieceType pieceType(final Square sq) {
        return this.squares[sq.ordinal()];
    }

    /**
     * Get the piece at the square.
     * @param sq square
     * @return piece
     */
    public Piece piece(final Square sq) {
        return Piece.of(
                Colour.of(Bitboard.intersects(sq.bitboard(), this.colourBBs[Colour.WHITE.ordinal()])),
                pieceType(sq));
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
        final Square from = Move.from(move);
        final Square to = Move.to(move);
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
    public boolean isDeadPosition() {
        // Any pawn, rook, or queen on board => not dead
        if ((pieces(PieceType.PAWN) | pieces(PieceType.ROOK) | pieces(PieceType.QUEEN)) != 0L) return false;

        final long knights = pieces(PieceType.KNIGHT);
        final long bishops = pieces(PieceType.BISHOP);
        final long minors = knights | bishops;

        final int minorCount = Long.bitCount(minors);

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

