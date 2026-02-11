package dev.pig.stockpig.chess.notation;

import dev.pig.stockpig.chess.*;
import dev.pig.stockpig.chess.bitboard.Square;

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
                    fromBoardFen(parts[0]),
                    "w".equals(parts[1]) ? Colour.WHITE : Colour.BLACK,
                    Castling.fromString(parts[2]),
                    Square.fromString(parts[3]),
                    Integer.parseInt(parts[4]),
                    Integer.parseInt(parts[5])
            );
        } catch (final Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    /**
     * Create a chess board from the material part of a FEN string.
     * @param fen board part FEN string
     * @return board
     */
    private static Board fromBoardFen(final String fen) {
        final Board board = Board.empty();

        int sq = 56;
        for (int i = 0; i < fen.length(); i++) {
            final char c = fen.charAt(i);

            if      (c == '/')              sq -= 16;
            else if (Character.isDigit(c))  sq += Character.digit(c, 10);
            else {
                final Piece p = Piece.fromString(Character.toString(c));
                board.addPiece(p.colour(), p.type(), sq);
                sq++;
            }
        }
        return board;
    }


    // ====================================================================================================
    //                                  To Fen / Encode / Format
    // ====================================================================================================

    /**
     * Get a FEN string for a chess position.
     * @param pos position
     * @return FEN string
     */
    public static String format(final Position pos) {
        return String.join(" ",
                toBoardFen(pos.board()),
                pos.sideToMove() == Colour.WHITE ? "w" : "b",
                Castling.toString(pos.castlingRights()),
                Square.toString(pos.enPassantTarget()),
                Integer.toString(pos.halfMoveClock()),
                Integer.toString(pos.turn())
        );
    }

    /**
     * Create the board part of FEN string from the board.
     * @return board part FEN string
     */
    private static String toBoardFen(final Board board) {
        final StringBuilder fen = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            int emptyRun = 0;
            for (int file = 0; file < 8; file++) {
                final int sq = rank*8+file;
                final Piece piece = board.piece(sq);
                if (piece == Piece.EMPTY) {
                    emptyRun++;
                } else {
                    if (emptyRun > 0) {
                        fen.append(emptyRun);
                        emptyRun = 0;
                    }
                    fen.append(piece.toString());
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


    private Fen() {}
}
