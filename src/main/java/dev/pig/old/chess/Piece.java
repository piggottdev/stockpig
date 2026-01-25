package dev.pig.old.chess;

/**
 * Chess pieces are represented with a single int bitmap.
 * This class has static methods to extract data from the int.
 * <p>
 * The 3 least significant bits are used as a bitmap to show the type of piece.
 * The 4th least significant bit represents the team of the piece.
 * <p>
 * If the piece bits are empty then the int represents the whole team.
 * <p>
 * As there are 6 chess pieces we reserve int 7 to represent 'Unoccupied' squares.
 * <p>
 * 0 (0000) Black Team
 * 1 (0001) Black King
 * 2 (0010) Black Pawn
 * 3 (0011) Black Knight
 * 4 (0100) Black Bishop
 * 5 (0101) Black Rook
 * 6 (0110) Black Queen
 * 7 (0111) Unoccupied
 * 8 (1000) White Team
 * 9 (1001) White King
 * 10 (1010) White Pawn
 * 11 (1011) White Knight
 * 12 (1100) White Bishop
 * 13 (1101) White Rook
 * 14 (1110) White Queen
 */
public class Piece {

    // The 3 least significant bits store piece type
    private static final int TYPE_BITMASK = 0b00000111;
    public static final int EMPTY = 0;
    public static final int KING = 1;
    public static final int PAWN = 2;
    public static final int KNIGHT = 3;
    public static final int BISHOP = 4;
    public static final int ROOK = 5;
    public static final int QUEEN = 6;

    // The 4th least significant bit stores team
    private static final int TEAM_BITMASK = 0b00001000;
    public static final int BLACK = 0;
    public static final int WHITE = 8;
    public static final int UNOCCUPIED = 7;

    // Characters of chess pieces
    private static final char K = 'K';
    private static final char P = 'P';
    private static final char N = 'N';
    private static final char B = 'B';
    private static final char R = 'R';
    private static final char Q = 'Q';

    private Piece() {}

    public static int getTeamOnly(final int piece) {
        return (TEAM_BITMASK & piece);
    }

    public static int getTeam(final boolean isWhite) {
        return isWhite ? WHITE : BLACK;
    }

    public static int flipTeam(final int piece) {
        return (WHITE ^ piece);
    }

    public static boolean isWhite(final int piece) {
        return (piece >= 8);
    }

    public static int getTypeOnly(final int piece) {
        return (TYPE_BITMASK & piece);
    }

    public static boolean isSlider(final int piece) {
        return (getTypeOnly(piece) > KNIGHT);
    }

    public static boolean isKing(final int piece) {
        return (getTypeOnly(piece) == KING);
    }

    public static boolean isPawn(final int piece) {
        return (getTypeOnly(piece) == PAWN);
    }

    public static boolean isKnight(final int piece) {
        return (getTypeOnly(piece) == KNIGHT);
    }

    public static boolean isBishop(final int piece) {
        return (getTypeOnly(piece) == BISHOP);
    }

    public static boolean isRook(final int piece) {
        return (getTypeOnly(piece) == ROOK);
    }

    public static boolean isQueen(final int piece) {
        return (getTypeOnly(piece) == QUEEN);
    }

    public static int getSameTeamRook(final int piece) {
        return (getTeamOnly(piece) | ROOK);
    }

    public static int getForwardDirection(final boolean isWhite) {
        return isWhite ? Bitboard.NORTH : Bitboard.SOUTH;
    }

    public static int getBackwardDirection(final boolean isWhite) {
        return isWhite ? Bitboard.SOUTH : Bitboard.NORTH;
    }

    public static long getPawnStartingRank(final boolean isWhite) {
        return isWhite ? Bitboard.RANKS[1] : Bitboard.RANKS[6];
    }

    public static long getPawnPromotionRank(final boolean isWhite) {
        return isWhite ? Bitboard.RANKS[7] : Bitboard.RANKS[0];
    }

    public static int[] getPawnAttackingDirections(final boolean isWhite) {
        return isWhite ? Bitboard.WHITE_PAWN_ATTACK_DIRECTIONS : Bitboard.BLACK_PAWN_ATTACK_DIRECTIONS;
    }

    public static int fromChar(final char c) {
        final char upper = Character.toUpperCase(c);
        int piece = switch (upper) {
            case K -> KING;
            case P -> PAWN;
            case N -> KNIGHT;
            case B -> BISHOP;
            case R -> ROOK;
            case Q -> QUEEN;
            default -> 7;
        };
        if (Character.isUpperCase(c)) piece |= WHITE;
        return piece;
    }

    public static char toChar(final int piece) {
        char c = switch (getTypeOnly(piece)) {
            case KING -> K;
            case PAWN -> P;
            case KNIGHT -> N;
            case BISHOP -> B;
            case ROOK -> R;
            case QUEEN -> Q;
            default -> ' ';
        };
        if (c != ' ' && !isWhite(piece)) c = Character.toLowerCase(c);
        return c;
    }

}
