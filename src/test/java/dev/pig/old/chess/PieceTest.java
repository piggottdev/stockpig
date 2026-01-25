package dev.pig.old.chess;

import dev.pig.old.chess.Bitboard;
import dev.pig.old.chess.Piece;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceTest {

    @Test
    void pieces() {
        assertEquals(0, Piece.BLACK);
        assertEquals(1, Piece.BLACK | Piece.KING);
        assertEquals(2, Piece.BLACK | Piece.PAWN);
        assertEquals(3, Piece.BLACK | Piece.KNIGHT);
        assertEquals(4, Piece.BLACK | Piece.BISHOP);
        assertEquals(5, Piece.BLACK | Piece.ROOK);
        assertEquals(6, Piece.BLACK | Piece.QUEEN);

        assertEquals(7, Piece.UNOCCUPIED);

        assertEquals(8, Piece.WHITE);
        assertEquals(9, Piece.WHITE | Piece.KING);
        assertEquals(10, Piece.WHITE | Piece.PAWN);
        assertEquals(11, Piece.WHITE | Piece.KNIGHT);
        assertEquals(12, Piece.WHITE | Piece.BISHOP);
        assertEquals(13, Piece.WHITE | Piece.ROOK);
        assertEquals(14, Piece.WHITE | Piece.QUEEN);
    }

    @Test
    void getTeamOnly() {
        assertEquals(0, Piece.getTeamOnly(Piece.BLACK));
        assertEquals(0, Piece.getTeamOnly(Piece.BLACK | Piece.KING));
        assertEquals(0, Piece.getTeamOnly(Piece.BLACK | Piece.PAWN));
        assertEquals(0, Piece.getTeamOnly( Piece.BLACK | Piece.KNIGHT));
        assertEquals(0, Piece.getTeamOnly( Piece.BLACK | Piece.BISHOP));
        assertEquals(0, Piece.getTeamOnly( Piece.BLACK | Piece.ROOK));
        assertEquals(0, Piece.getTeamOnly( Piece.BLACK | Piece.QUEEN));

        assertEquals(0, Piece.getTeamOnly( Piece.UNOCCUPIED));

        assertEquals(8, Piece.getTeamOnly(Piece.WHITE));
        assertEquals(8, Piece.getTeamOnly(Piece.WHITE | Piece.KING));
        assertEquals(8, Piece.getTeamOnly(Piece.WHITE | Piece.PAWN));
        assertEquals(8, Piece.getTeamOnly( Piece.WHITE | Piece.KNIGHT));
        assertEquals(8, Piece.getTeamOnly( Piece.WHITE | Piece.BISHOP));
        assertEquals(8, Piece.getTeamOnly( Piece.WHITE | Piece.ROOK));
        assertEquals(8, Piece.getTeamOnly( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void getTeam() {
        assertEquals(0, Piece.getTeam(false));
        assertEquals(8, Piece.getTeam(true));
    }

    @Test
    void flipTeam() {
        assertEquals(8, Piece.flipTeam(Piece.BLACK));
        assertEquals(9, Piece.flipTeam(Piece.BLACK | Piece.KING));
        assertEquals(10, Piece.flipTeam(Piece.BLACK | Piece.PAWN));
        assertEquals(11, Piece.flipTeam( Piece.BLACK | Piece.KNIGHT));
        assertEquals(12, Piece.flipTeam( Piece.BLACK | Piece.BISHOP));
        assertEquals(13, Piece.flipTeam( Piece.BLACK | Piece.ROOK));
        assertEquals(14, Piece.flipTeam( Piece.BLACK | Piece.QUEEN));

        assertEquals(0, Piece.flipTeam(Piece.WHITE));
        assertEquals(1, Piece.flipTeam(Piece.WHITE | Piece.KING));
        assertEquals(2, Piece.flipTeam(Piece.WHITE | Piece.PAWN));
        assertEquals(3, Piece.flipTeam( Piece.WHITE | Piece.KNIGHT));
        assertEquals(4, Piece.flipTeam( Piece.WHITE | Piece.BISHOP));
        assertEquals(5, Piece.flipTeam( Piece.WHITE | Piece.ROOK));
        assertEquals(6, Piece.flipTeam( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isWhite() {
        assertFalse(Piece.isWhite(Piece.BLACK));
        assertFalse(Piece.isWhite(Piece.BLACK | Piece.KING));
        assertFalse(Piece.isWhite(Piece.BLACK | Piece.PAWN));
        assertFalse(Piece.isWhite( Piece.BLACK | Piece.KNIGHT));
        assertFalse(Piece.isWhite( Piece.BLACK | Piece.BISHOP));
        assertFalse(Piece.isWhite( Piece.BLACK | Piece.ROOK));
        assertFalse(Piece.isWhite( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isWhite( Piece.UNOCCUPIED));

        assertTrue(Piece.isWhite(Piece.WHITE));
        assertTrue(Piece.isWhite(Piece.WHITE | Piece.KING));
        assertTrue(Piece.isWhite(Piece.WHITE | Piece.PAWN));
        assertTrue(Piece.isWhite( Piece.WHITE | Piece.KNIGHT));
        assertTrue(Piece.isWhite( Piece.WHITE | Piece.BISHOP));
        assertTrue(Piece.isWhite( Piece.WHITE | Piece.ROOK));
        assertTrue(Piece.isWhite( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void getTypeOnly() {
        assertEquals(0, Piece.getTypeOnly(Piece.BLACK));
        assertEquals(1, Piece.getTypeOnly(Piece.BLACK | Piece.KING));
        assertEquals(2, Piece.getTypeOnly(Piece.BLACK | Piece.PAWN));
        assertEquals(3, Piece.getTypeOnly( Piece.BLACK | Piece.KNIGHT));
        assertEquals(4, Piece.getTypeOnly( Piece.BLACK | Piece.BISHOP));
        assertEquals(5, Piece.getTypeOnly( Piece.BLACK | Piece.ROOK));
        assertEquals(6, Piece.getTypeOnly( Piece.BLACK | Piece.QUEEN));

        assertEquals(7, Piece.getTypeOnly( Piece.UNOCCUPIED));

        assertEquals(0, Piece.getTypeOnly(Piece.WHITE));
        assertEquals(1, Piece.getTypeOnly(Piece.WHITE | Piece.KING));
        assertEquals(2, Piece.getTypeOnly(Piece.WHITE | Piece.PAWN));
        assertEquals(3, Piece.getTypeOnly( Piece.WHITE | Piece.KNIGHT));
        assertEquals(4, Piece.getTypeOnly( Piece.WHITE | Piece.BISHOP));
        assertEquals(5, Piece.getTypeOnly( Piece.WHITE | Piece.ROOK));
        assertEquals(6, Piece.getTypeOnly( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isSlider() {
        assertFalse(Piece.isSlider(Piece.BLACK));
        assertFalse(Piece.isSlider(Piece.BLACK | Piece.KING));
        assertFalse(Piece.isSlider(Piece.BLACK | Piece.PAWN));
        assertFalse(Piece.isSlider( Piece.BLACK | Piece.KNIGHT));
        assertTrue(Piece.isSlider( Piece.BLACK | Piece.BISHOP));
        assertTrue(Piece.isSlider( Piece.BLACK | Piece.ROOK));
        assertTrue(Piece.isSlider( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isSlider(Piece.WHITE));
        assertFalse(Piece.isSlider(Piece.WHITE | Piece.KING));
        assertFalse(Piece.isSlider(Piece.WHITE | Piece.PAWN));
        assertFalse(Piece.isSlider( Piece.WHITE | Piece.KNIGHT));
        assertTrue(Piece.isSlider( Piece.WHITE | Piece.BISHOP));
        assertTrue(Piece.isSlider( Piece.WHITE | Piece.ROOK));
        assertTrue(Piece.isSlider( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isKing() {
        assertFalse(Piece.isKing(Piece.BLACK));
        assertTrue(Piece.isKing(Piece.BLACK | Piece.KING));
        assertFalse(Piece.isKing(Piece.BLACK | Piece.PAWN));
        assertFalse(Piece.isKing( Piece.BLACK | Piece.KNIGHT));
        assertFalse(Piece.isKing( Piece.BLACK | Piece.BISHOP));
        assertFalse(Piece.isKing( Piece.BLACK | Piece.ROOK));
        assertFalse(Piece.isKing( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isKing( Piece.UNOCCUPIED));

        assertFalse(Piece.isKing(Piece.WHITE));
        assertTrue(Piece.isKing(Piece.WHITE | Piece.KING));
        assertFalse(Piece.isKing(Piece.WHITE | Piece.PAWN));
        assertFalse(Piece.isKing( Piece.WHITE | Piece.KNIGHT));
        assertFalse(Piece.isKing( Piece.WHITE | Piece.BISHOP));
        assertFalse(Piece.isKing( Piece.WHITE | Piece.ROOK));
        assertFalse(Piece.isKing( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isPawn() {
        assertFalse(Piece.isPawn(Piece.BLACK));
        assertFalse(Piece.isPawn(Piece.BLACK | Piece.KING));
        assertTrue(Piece.isPawn(Piece.BLACK | Piece.PAWN));
        assertFalse(Piece.isPawn( Piece.BLACK | Piece.KNIGHT));
        assertFalse(Piece.isPawn( Piece.BLACK | Piece.BISHOP));
        assertFalse(Piece.isPawn( Piece.BLACK | Piece.ROOK));
        assertFalse(Piece.isPawn( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isPawn( Piece.UNOCCUPIED));

        assertFalse(Piece.isPawn(Piece.WHITE));
        assertFalse(Piece.isPawn(Piece.WHITE | Piece.KING));
        assertTrue(Piece.isPawn(Piece.WHITE | Piece.PAWN));
        assertFalse(Piece.isPawn( Piece.WHITE | Piece.KNIGHT));
        assertFalse(Piece.isPawn( Piece.WHITE | Piece.BISHOP));
        assertFalse(Piece.isPawn( Piece.WHITE | Piece.ROOK));
        assertFalse(Piece.isPawn( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isKnight() {
        assertFalse(Piece.isKnight(Piece.BLACK));
        assertFalse(Piece.isKnight(Piece.BLACK | Piece.KING));
        assertFalse(Piece.isKnight(Piece.BLACK | Piece.PAWN));
        assertTrue(Piece.isKnight( Piece.BLACK | Piece.KNIGHT));
        assertFalse(Piece.isKnight( Piece.BLACK | Piece.BISHOP));
        assertFalse(Piece.isKnight( Piece.BLACK | Piece.ROOK));
        assertFalse(Piece.isKnight( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isKnight( Piece.UNOCCUPIED));

        assertFalse(Piece.isKnight(Piece.WHITE));
        assertFalse(Piece.isKnight(Piece.WHITE | Piece.KING));
        assertFalse(Piece.isKnight(Piece.WHITE | Piece.PAWN));
        assertTrue(Piece.isKnight( Piece.WHITE | Piece.KNIGHT));
        assertFalse(Piece.isKnight( Piece.WHITE | Piece.BISHOP));
        assertFalse(Piece.isKnight( Piece.WHITE | Piece.ROOK));
        assertFalse(Piece.isKnight( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isBishop() {
        assertFalse(Piece.isBishop(Piece.BLACK));
        assertFalse(Piece.isBishop(Piece.BLACK | Piece.KING));
        assertFalse(Piece.isBishop(Piece.BLACK | Piece.PAWN));
        assertFalse(Piece.isBishop( Piece.BLACK | Piece.KNIGHT));
        assertTrue(Piece.isBishop( Piece.BLACK | Piece.BISHOP));
        assertFalse(Piece.isBishop( Piece.BLACK | Piece.ROOK));
        assertFalse(Piece.isBishop( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isBishop( Piece.UNOCCUPIED));

        assertFalse(Piece.isBishop(Piece.WHITE));
        assertFalse(Piece.isBishop(Piece.WHITE | Piece.KING));
        assertFalse(Piece.isBishop(Piece.WHITE | Piece.PAWN));
        assertFalse(Piece.isBishop( Piece.WHITE | Piece.KNIGHT));
        assertTrue(Piece.isBishop( Piece.WHITE | Piece.BISHOP));
        assertFalse(Piece.isBishop( Piece.WHITE | Piece.ROOK));
        assertFalse(Piece.isBishop( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isRook() {
        assertFalse(Piece.isRook(Piece.BLACK));
        assertFalse(Piece.isRook(Piece.BLACK | Piece.KING));
        assertFalse(Piece.isRook(Piece.BLACK | Piece.PAWN));
        assertFalse(Piece.isRook( Piece.BLACK | Piece.KNIGHT));
        assertFalse(Piece.isRook( Piece.BLACK | Piece.BISHOP));
        assertTrue(Piece.isRook( Piece.BLACK | Piece.ROOK));
        assertFalse(Piece.isRook( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isRook( Piece.UNOCCUPIED));

        assertFalse(Piece.isRook(Piece.WHITE));
        assertFalse(Piece.isRook(Piece.WHITE | Piece.KING));
        assertFalse(Piece.isRook(Piece.WHITE | Piece.PAWN));
        assertFalse(Piece.isRook( Piece.WHITE | Piece.KNIGHT));
        assertFalse(Piece.isRook( Piece.WHITE | Piece.BISHOP));
        assertTrue(Piece.isRook( Piece.WHITE | Piece.ROOK));
        assertFalse(Piece.isRook( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void isQueen() {
        assertFalse(Piece.isQueen(Piece.BLACK));
        assertFalse(Piece.isQueen(Piece.BLACK | Piece.KING));
        assertFalse(Piece.isQueen(Piece.BLACK | Piece.PAWN));
        assertFalse(Piece.isQueen( Piece.BLACK | Piece.KNIGHT));
        assertFalse(Piece.isQueen( Piece.BLACK | Piece.BISHOP));
        assertFalse(Piece.isQueen( Piece.BLACK | Piece.ROOK));
        assertTrue(Piece.isQueen( Piece.BLACK | Piece.QUEEN));

        assertFalse(Piece.isQueen( Piece.UNOCCUPIED));

        assertFalse(Piece.isQueen(Piece.WHITE));
        assertFalse(Piece.isQueen(Piece.WHITE | Piece.KING));
        assertFalse(Piece.isQueen(Piece.WHITE | Piece.PAWN));
        assertFalse(Piece.isQueen( Piece.WHITE | Piece.KNIGHT));
        assertFalse(Piece.isQueen( Piece.WHITE | Piece.BISHOP));
        assertFalse(Piece.isQueen( Piece.WHITE | Piece.ROOK));
        assertTrue(Piece.isQueen( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void getSameTeamRook() {
        assertEquals(5, Piece.getSameTeamRook(Piece.BLACK));
        assertEquals(5, Piece.getSameTeamRook(Piece.BLACK | Piece.KING));
        assertEquals(5, Piece.getSameTeamRook(Piece.BLACK | Piece.PAWN));
        assertEquals(5, Piece.getSameTeamRook( Piece.BLACK | Piece.KNIGHT));
        assertEquals(5, Piece.getSameTeamRook( Piece.BLACK | Piece.BISHOP));
        assertEquals(5, Piece.getSameTeamRook( Piece.BLACK | Piece.ROOK));
        assertEquals(5, Piece.getSameTeamRook( Piece.BLACK | Piece.QUEEN));

        assertEquals(5, Piece.getSameTeamRook( Piece.UNOCCUPIED));

        assertEquals(13, Piece.getSameTeamRook(Piece.WHITE));
        assertEquals(13, Piece.getSameTeamRook(Piece.WHITE | Piece.KING));
        assertEquals(13, Piece.getSameTeamRook(Piece.WHITE | Piece.PAWN));
        assertEquals(13, Piece.getSameTeamRook( Piece.WHITE | Piece.KNIGHT));
        assertEquals(13, Piece.getSameTeamRook( Piece.WHITE | Piece.BISHOP));
        assertEquals(13, Piece.getSameTeamRook( Piece.WHITE | Piece.ROOK));
        assertEquals(13, Piece.getSameTeamRook( Piece.WHITE | Piece.QUEEN));
    }

    @Test
    void getForwardDirection() {
        assertEquals(Bitboard.NORTH, Piece.getForwardDirection(true));
        assertEquals(Bitboard.SOUTH, Piece.getForwardDirection(false));
    }

    @Test
    void getBackwardDirection() {
        assertEquals(Bitboard.SOUTH, Piece.getBackwardDirection(true));
        assertEquals(Bitboard.NORTH, Piece.getBackwardDirection(false));
    }

    @Test
    void getPawnStartingRank() {
        assertEquals(Bitboard.RANKS[1], Piece.getPawnStartingRank(true));
        assertEquals(Bitboard.RANKS[6], Piece.getPawnStartingRank(false));
    }

    @Test
    void getPawnPromotionRank() {
        assertEquals(Bitboard.RANKS[7], Piece.getPawnPromotionRank(true));
        assertEquals(Bitboard.RANKS[0], Piece.getPawnPromotionRank(false));
    }

    @Test
    void getPawnAttackingDirections() {
        assertEquals(Bitboard.WHITE_PAWN_ATTACK_DIRECTIONS, Piece.getPawnAttackingDirections(true));
        assertEquals(Bitboard.BLACK_PAWN_ATTACK_DIRECTIONS, Piece.getPawnAttackingDirections(false));
    }

    @Test
    void fromChar() {
        assertEquals(Piece.BLACK | Piece.KING, Piece.fromChar('k'));
        assertEquals(Piece.BLACK | Piece.PAWN, Piece.fromChar('p'));
        assertEquals(Piece.BLACK | Piece.KNIGHT, Piece.fromChar('n'));
        assertEquals(Piece.BLACK | Piece.BISHOP, Piece.fromChar('b'));
        assertEquals(Piece.BLACK | Piece.ROOK, Piece.fromChar('r'));
        assertEquals(Piece.BLACK | Piece.QUEEN, Piece.fromChar('q'));

        assertEquals(Piece.UNOCCUPIED, Piece.fromChar(' '));

        assertEquals(Piece.WHITE | Piece.KING, Piece.fromChar('K'));
        assertEquals(Piece.WHITE | Piece.PAWN, Piece.fromChar('P'));
        assertEquals(Piece.WHITE | Piece.KNIGHT, Piece.fromChar('N'));
        assertEquals(Piece.WHITE | Piece.BISHOP, Piece.fromChar('B'));
        assertEquals(Piece.WHITE | Piece.ROOK, Piece.fromChar('R'));
        assertEquals(Piece.WHITE | Piece.QUEEN, Piece.fromChar('Q'));
    }

    @Test
    void toChar() {
        assertEquals(' ', Piece.toChar(Piece.BLACK));
        assertEquals('k', Piece.toChar(Piece.BLACK | Piece.KING));
        assertEquals('p', Piece.toChar(Piece.BLACK | Piece.PAWN));
        assertEquals('n', Piece.toChar( Piece.BLACK | Piece.KNIGHT));
        assertEquals('b', Piece.toChar( Piece.BLACK | Piece.BISHOP));
        assertEquals('r', Piece.toChar( Piece.BLACK | Piece.ROOK));
        assertEquals('q', Piece.toChar( Piece.BLACK | Piece.QUEEN));

        assertEquals(' ', Piece.toChar( Piece.UNOCCUPIED));

        assertEquals(' ', Piece.toChar(Piece.WHITE));
        assertEquals('K', Piece.toChar(Piece.WHITE | Piece.KING));
        assertEquals('P', Piece.toChar(Piece.WHITE | Piece.PAWN));
        assertEquals('N', Piece.toChar( Piece.WHITE | Piece.KNIGHT));
        assertEquals('B', Piece.toChar( Piece.WHITE | Piece.BISHOP));
        assertEquals('R', Piece.toChar( Piece.WHITE | Piece.ROOK));
        assertEquals('Q', Piece.toChar( Piece.WHITE | Piece.QUEEN));
    }
}