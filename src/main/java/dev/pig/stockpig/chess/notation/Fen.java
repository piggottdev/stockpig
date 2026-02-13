package dev.pig.stockpig.chess.notation;

import dev.pig.stockpig.chess.core.*;
import dev.pig.stockpig.chess.core.bitboard.Bitboard;
import dev.pig.stockpig.chess.core.bitboard.Square;

/**
 * Fen (Forsyth-Edwards Notation) provides functions for encoding and decoding FEN strings.
 */
public final class Fen {

    public static final String STARTING = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public final static class ParseException extends Exception {
        private ParseException(final String message) {
            super(message);
        }
    }


    /**
     * Get a starting position by parsing the starting FEN.
     * @return starting position
     */
    public static Position startingPosition() {
        try {
            return parse(STARTING);
        } catch (final ParseException pe) {
            throw new RuntimeException(pe);
        }
    }


    // ====================================================================================================
    //                                  From Fen / Decode / Parse
    // ====================================================================================================

    /**
     * Parse a FEN string and return a chess position.
     * Invalid FENs do not always throw an exception.
     * @param fen FEN string
     * @return position
     * @throws ParseException invalid FEN exception
     */
    public static Position parse(final String fen) throws ParseException {
        final String[] parts = fen.split(" ");
        if (parts.length != 6) throw new ParseException("FEN must have 6 parts");

        try {
            return new Position(
                    parseBoard(parts[0]),
                    "w".equals(parts[1]) ? Colour.WHITE : Colour.BLACK,
                    parseCastling(parts[2]),
                    parseEnPassantTarget(parts[3]),
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5])
            );
        } catch (final Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Parse the board part of a FEN string and return a board.
     * @param fen board part of FEN string
     * @return board
     */
    private static Board parseBoard(final String fen) {
        final Board board = Board.empty();

        byte sq = 56;
        for (int i = 0; i < fen.length(); i++) {
            final char c = fen.charAt(i);

            if      (c == '/')              sq -= 16;
            else if (Character.isDigit(c))  sq += (byte) Character.digit(c, 10);
            else {
                final byte pt = parsePieceType(Character.toString(c));
                board.addPiece(Character.isUpperCase(c), pt, sq);
                sq++;
            }
        }
        return board;
    }

    /**
     * Parse the castling rights part of a FEN string and return castling rights.
     * @param s castling rights part of FEN string
     * @return castling rights
     */
    private static byte parseCastling(final String s) {
        return (byte) (
                        (s.contains("K") ? Castling.W_KING_SIDE  : 0) |
                        (s.contains("Q") ? Castling.W_QUEEN_SIDE : 0) |
                        (s.contains("k") ? Castling.B_KING_SIDE  : 0) |
                        (s.contains("q") ? Castling.B_QUEEN_SIDE : 0));
    }

    /**
     * Parse the en passant target square part of a FEN string and return en passant target square.
     * @param s en passant target part of FEN string
     * @return en passant target
     */
    private static byte parseEnPassantTarget(final String s) {
        if ("-".equals(s)) return Square.EMPTY;

        if (s.length() != 2) throw new IllegalArgumentException("unknown square: " + s);

        final char file = s.toLowerCase().charAt(0);
        final char rank = s.toLowerCase().charAt(1);

        if (file < 'a' || file > 'h') throw new IllegalArgumentException("unknown file: " + file);
        if (rank < '1' || rank > '8') throw new IllegalArgumentException("unknown rank: " + rank);

        return (byte) ((rank - '1')*8 + (file - 'a'));
    }

    /**
     * Parse a piece type from a piece character string.
     * @param s piece type character string
     * @return piece type
     */
    private static byte parsePieceType(final String s) {
        return switch (s.toLowerCase()) {
            case "k" -> PieceType.KING;
            case "p" -> PieceType.PAWN;
            case "n" -> PieceType.KNIGHT;
            case "b" -> PieceType.BISHOP;
            case "r" -> PieceType.ROOK;
            case "q" -> PieceType.QUEEN;
            default -> throw new IllegalArgumentException("unknown piece type: " + s);
        };
    }


    // ====================================================================================================
    //                                  To Fen / Encode / Format
    // ====================================================================================================

    /**
     * Format a chess position into a FEN string.
     * @param pos position
     * @return FEN string
     */
    public static String format(final Position pos) {
        return String.join(" ",
                formatBoard(pos.board()),
                pos.sideToMove() == Colour.WHITE ? "w" : "b",
                formatCastling(pos.castlingRights()),
                formatEnPassantTarget(pos.enPassantTarget()),
                Integer.toString(pos.halfMoveClock()),
                Integer.toString(pos.turn())
        );
    }

    /**
     * Format a board into a board FEN string.
     * @param board board
     * @return board part FEN string
     */
    private static String formatBoard(final Board board) {
        final StringBuilder fen = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            int emptyRun = 0;
            for (int file = 0; file < 8; file++) {
                final byte sq = (byte) (rank*8+file);
                final byte piece = board.pieceAt(sq);
                if (piece == PieceType.EMPTY) {
                    emptyRun++;
                } else {
                    if (emptyRun > 0) {
                        fen.append(emptyRun);
                        emptyRun = 0;
                    }
                    fen.append(
                            Bitboard.intersects(Bitboard.ofSquare(sq), board.pieces(Colour.WHITE)) ?
                                    formatPieceType(piece).toUpperCase() :
                                    formatPieceType(piece)
                    );
                }
            }
            if (emptyRun > 0) {
                fen.append(emptyRun);
            }
            if (rank != 0) {
                fen.append("/");
            }
        }
        return fen.toString();
    }

    /**
     * Format a square into an en passant target square FEN string.
     * @param sq square
     * @return en passant target FEN string
     */
    private static String formatEnPassantTarget(final byte sq) {
        if (sq == Square.EMPTY) return "-";

        final int file = sq & 7;
        final int rank = (sq >> 3) + 1;
        return (char) ('a' + file) + Integer.toString(rank);
    }

    /**
     * Format castling rights into a castling rights FEN string.
     * @param rights castling rights
     * @return castling rights FEN string
     */
    private static String formatCastling(final byte rights) {
        return rights == Castling.NONE ? "-" :
                ((rights & Castling.W_KING_SIDE)     == 0 ? "" : "K") +
                ((rights & Castling.W_QUEEN_SIDE)    == 0 ? "" : "Q") +
                ((rights & Castling.B_KING_SIDE)     == 0 ? "" : "k") +
                ((rights & Castling.B_QUEEN_SIDE)    == 0 ? "" : "q");
    }

    /**
     * Format a piece type to a piece type character string.
     * @param pt piece type
     * @return piece type character string
     */
    private static String formatPieceType(final byte pt) {
        return switch (pt) {
            case PieceType.KING   -> "k";
            case PieceType.PAWN   -> "p";
            case PieceType.KNIGHT -> "n";
            case PieceType.BISHOP -> "b";
            case PieceType.ROOK   -> "r";
            case PieceType.QUEEN  -> "q";
            default -> "";
        };
    }


    private Fen() {}
}
