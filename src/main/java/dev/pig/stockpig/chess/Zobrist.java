package dev.pig.stockpig.chess;

import java.util.Random;

public final class Zobrist {

    /**
     * Get the Zobrist hash for the side to move.
     * @param colour side to move
     * @return side to move hash
     */
    static long side(final boolean colour) {
        return colour ? 0 : BLACK_TO_MOVE_HASH;
    }

    /**
     * Get the Zobrist hash for a piece on a square.
     * @param colour colour
     * @param pieceType piece type
     * @param sq square
     * @return piece square hash
     */
    static long pieceSquare(final boolean colour, final byte pieceType, final byte sq) {
        return colour ? WHITE_PIECE_SQUARE_HASH[pieceType][sq] : BLACK_PIECE_SQUARE_HASH[pieceType][sq];
    }

    /**
     * Get the Zobrist hash for castling rights.
     * @param castlingRights castling rights
     * @return castling rights hash
     */
    static long castlingRights(final byte castlingRights) {
        return CASTLING_RIGHTS_HASH[castlingRights];
    }

    /**
     * Get the Zobrist hash for an en passant target square, this is unique per file.
     * @param enPassantTarget en passant target
     * @return en passant target hash
     */
    static long enPassantTarget(final byte enPassantTarget) {
        return EN_PASSANT_FILE_HASH[enPassantTarget & 7];
    }


    // ====================================================================================================
    //                                  Hashes
    // ====================================================================================================

    private static final long     BLACK_TO_MOVE_HASH;
    private static final long[][] WHITE_PIECE_SQUARE_HASH = new long[7][64];
    private static final long[][] BLACK_PIECE_SQUARE_HASH = new long[7][64];
    private static final long[]   CASTLING_RIGHTS_HASH    = new long[16];
    private static final long[]   EN_PASSANT_FILE_HASH    = new long[8];

    static {
        final Random r = new Random(101L);

        BLACK_TO_MOVE_HASH = r.nextLong();
        for (int i = 1; i < 7; i++) {
            for (int j = 0; j < 64; j++) {
                WHITE_PIECE_SQUARE_HASH[i][j] = r.nextLong();
                BLACK_PIECE_SQUARE_HASH[i][j] = r.nextLong();
            }
        }
        for (int i = 0; i < 16; i++) CASTLING_RIGHTS_HASH[i] = r.nextLong();
        for (int i = 0; i < 8; i++)  EN_PASSANT_FILE_HASH[i] = r.nextLong();
    }


    private Zobrist() {}
}
