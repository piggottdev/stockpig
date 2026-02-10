package dev.pig.stockpig.chess;

import dev.pig.stockpig.chess.bitboard.Attack;
import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Direction;
import dev.pig.stockpig.chess.bitboard.Square;

/**
 * Move Generator generates legal moves for a given chess position into a
 * move list. Also, calculates and caches check, attack and pin information.
 */
public final class MoveGenerator {

    private boolean isCheck;
    private boolean isDoubleCheck;

    private long attacked;
    private long checkers;
    private long checkRay;
    private long pinned;
    private long target;
    private final long[] pins = new long[5];

    // Pin Axis'
    private static final int ALL            = 0;
    private static final int HORIZONTAL     = 1;
    private static final int VERTICAL       = 2;
    private static final int DIAGONAL       = 3;
    private static final int ANTI_DIAGONAL  = 4;


    // ====================================================================================================
    //                                  Accessors
    // ====================================================================================================

    /**
     * Get whether the position is currently in check.
     * @return is in check
     */
    public boolean isCheck () {
        return this.isCheck;
    }

    /**
     * Get the bitboard of squares attacked/threatened by the other side.
     * @return attacked bitboard
     */
    public long attacked() {
        return this.attacked;
    }

    /**
     * Get the bitboard of all currently checking pieces.
     * @return checking pieces bitboard
     */
    public long checkers() {
        return this.checkers;
    }

    /**
     * Get the check ray bitboard, this is the line from a sliding piece causing check.
     * @return check ray bitboard
     */
    public long checkray() {
        return this.checkRay;
    }

    /**
     * Get the current side's target square bitboard. This is the checkray/checkers if
     * the position is in check or all enemy and unoccupied squares if not.
     * @return target bitboard
     */
    public long target() {
        return this.target;
    }

    /**
     * Get the bitboard of currently pinned pieces.
     * @return pinned pieces bitboard
     */
    public long pinned() {
        return this.pinned;
    }

    /**
     * Get all current pins.
     * @return pins bitboard
     */
    public long pins() {
        return this.pins[ALL];
    }


    // ====================================================================================================
    //                                  Checks, Attacks and Pins
    // ====================================================================================================

    /**
     * Set is check to false.
     */
    public void resetCheck() {
        this.isCheck = false;
    }

    /**
     * Reset all state that won't get reset during normal move generation.
     */
    public void reset() {
        this.checkers = Bitboard.EMPTY;
        this.checkRay = Bitboard.EMPTY;
        this.pinned   = Bitboard.EMPTY;

        this.pins[ALL]           = Bitboard.EMPTY;
        this.pins[HORIZONTAL]    = Bitboard.EMPTY;
        this.pins[VERTICAL]      = Bitboard.EMPTY;
        this.pins[DIAGONAL]      = Bitboard.EMPTY;
        this.pins[ANTI_DIAGONAL] = Bitboard.EMPTY;
    }

    /**
     * Analyse checks, attacks and pins, caching results for move generation.
     * @param pos position
     */
    public void attackAnalysis(final Position pos) {
        reset();

        final long unoccupied = pos.board().unoccupied();

        final Colour us = pos.sideToMove();
        final long friends = pos.board().pieces(us);
        final long king = pos.board().pieces(PieceType.KING) & friends;

        final Colour them = us.flip();
        final long enemies = pos.board().pieces(them);
        final long eKing = pos.board().pieces(PieceType.KING) & enemies;
        final long ePawns = pos.board().pieces(PieceType.PAWN) & enemies;
        final long eKnights = pos.board().pieces(PieceType.KNIGHT) & enemies;
        final long eBishops = pos.board().pieces(PieceType.BISHOP) & enemies;
        final long eRooks = pos.board().pieces(PieceType.ROOK) & enemies;
        final long eQueens = pos.board().pieces(PieceType.QUEEN) & enemies;

        // Attacks

        // TODO: Candidate optimisation: Move towards attack maps to calculate checks threats and pins
        // TODO: Candidate optimisation: Adding a line[from][king] should remove the need to calculate pins in different directions

        // King attacks
        this.attacked = Attack.king(Square.ofBitboard(eKing));
        // Pawn attacks
        stepAttacks(ePawns, king, new Direction[]{
                them.pawnAttackDirection1(), them.pawnAttackDirection2()
        });
        // Knight attacks
        stepAttacks(eKnights, king, new Direction[]{
                Direction.NNE, Direction.NEE, Direction.SEE, Direction.SSE,
                Direction.SSW, Direction.SWW, Direction.NWW, Direction.NNW
        });
        // Diagonal attacks
        slidingAttacks(eQueens | eBishops, unoccupied | king,  new Direction[]{
                Direction.NE, Direction.SE, Direction.SW, Direction.NW
        });
        // Orthogonal attacks
        slidingAttacks(eQueens | eRooks, unoccupied | king,  new Direction[]{
                Direction.N, Direction.E, Direction.S, Direction.W
        });

        // Pins
        pins(king, friends, enemies, eQueens | eRooks, VERTICAL, new Direction[]{
                Direction.N, Direction.S
        });
        pins(king, friends, enemies, eQueens | eRooks, HORIZONTAL, new Direction[]{
                Direction.E, Direction.W
        });
        pins(king, friends, enemies, eQueens | eBishops, DIAGONAL, new Direction[]{
                Direction.NE, Direction.SW
        });
        pins(king, friends, enemies, eQueens | eBishops, ANTI_DIAGONAL, new Direction[]{
                Direction.NW, Direction.SE
        });

        this.isCheck = !Bitboard.isEmpty(this.checkers);
        this.isDoubleCheck = this.isCheck && !Bitboard.isSingle(this.checkers);

        this.target = this.isCheck ? this.checkRay | this.checkers : enemies | unoccupied;
    }

    /**
     * Calculate any sliding checks or pins in given directions. This is done filling out from the king.
     * @param king king
     * @param friends current teams pieces
     * @param enemies other teams pieces
     * @param eSliders other teams pieces that can slide in the direction
     * @param axis axis of the directions
     * @param ds directions
     */
    private void pins(final long king, final long friends, final long enemies, final long eSliders, final int axis, final Direction[] ds) {
        for (final Direction d : ds) {
            pin(king, friends, enemies, eSliders, axis, d);
        }
    }

    /**
     * Calculate any sliding attacks in given directions.
     * @param pieces enemy sliders
     * @param unoccupied unoccupied bitboard
     * @param ds directions
     */
    private void slidingAttacks(final long pieces, final long unoccupied, final Direction[] ds) {
        for (final Direction d : ds) {
            slidingAttack(pieces, unoccupied, d);
        }
    }

    /**
     * Calculate any step attacks (single moves) in given directions.
     * @param pieces enemy step attackers
     * @param king king
     * @param ds directions
     */
    private void stepAttacks(final long pieces, final long king, final Direction[] ds) {
        for (final Direction d : ds) {
            stepAttack(pieces, king, d);
        }
    }

    /**
     * Calculate any sliding checks or pins in given direction.
     * @param king king
     * @param friends current teams pieces
     * @param enemies other teams pieces
     * @param eSliders other teams pieces that can slide in the direction
     * @param axis axis of the direction
     * @param d direction
     */
    private void pin(final long king, final long friends, final long enemies, final long eSliders, final int axis, final Direction d) {
        final long line = Bitboard.slide(king, ~enemies, d);
        final long pinner = line & eSliders;

        if (pinner == Bitboard.EMPTY) return; // No enemies that can pin

        final long pinned = line & friends;
        final long pinnedCount = Bitboard.count(pinned);

        if (pinnedCount > 1) return; // More than one blocker

        if (pinnedCount == 0) {
            // No blockers, this is check
            this.checkers |= pinner;
            this.checkRay |= line;
            return;
        }

        this.pins[ALL]  |= line;
        this.pins[axis] |= line;
        this.pinned     |= pinned;
    }

    /**
     * Calculate any sliding attacks in given direction.
     * @param pieces enemy sliders
     * @param unoccupied unoccupied bitboard
     * @param d direction
     */
    private void slidingAttack(final long pieces, final long unoccupied, final Direction d) {
        this.attacked |= Bitboard.slide(pieces, unoccupied, d);
    }

    /**
     * Calculate any step attacks (single moves) in given direction.
     * @param pieces enemy step attackers
     * @param king king
     * @param d direction
     */
    private void stepAttack(final long pieces, final long king, final Direction d) {
        final long attacks = Bitboard.shift(pieces, d);
        if (Bitboard.intersects(king, attacks)) {
            this.checkers |= Bitboard.shiftRev(king, d);
        }
        this.attacked |= attacks;
    }


    // ====================================================================================================
    //                                  Move Generation
    // ====================================================================================================

    /**
     * Generate all legal moves into move list.
     * @param pos current position
     * @param moves move list
     */
    public void generate(final Position pos, final MoveList moves) {
        attackAnalysis(pos);

        final Colour us             = pos.sideToMove();
        final long unoccupied       = pos.board().unoccupied();
        final long occupied         = ~unoccupied;
        final long team             = pos.board().pieces(us);
        final long enemies          = pos.board().pieces(us.flip());
        final long king             = pos.board().pieces(PieceType.KING) & team;


        // King moves - only king moves can get out of double check

        final Square kFrom = Square.ofBitboard(king);
        Bitboard.forEach(Attack.king(kFrom) & ~this.attacked & (unoccupied | enemies), (final long attack) -> {
            final Square to = Square.ofBitboard(attack);
            final int basic = Move.basic(kFrom, to, PieceType.KING);
            moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceType(to)));
        });

        if (this.isDoubleCheck) return;


        // All other moves (except castles)

        final long pawns          = pos.board().pieces(PieceType.PAWN) & team;
        final long queens         = pos.board().pieces(PieceType.QUEEN) & team;
        final long knights        = pos.board().pieces(PieceType.KNIGHT) & team;
        final long rooks          = pos.board().pieces(PieceType.ROOK) & team;
        final long bishops        = pos.board().pieces(PieceType.BISHOP) & team;

        final Direction forward     = us.forward();
        final Direction attackDir1  = us.pawnAttackDirection1();
        final Direction attackDir2  = us.pawnAttackDirection2();
        final long thirdRank        = us.rank3().bitboard();
        final long promotionRank    = us.rank8().bitboard();
        final long enPassantTarget  = pos.enPassantTarget().bitboard();

        // Pawns that can push forward one
        final long onePushedPawns = Bitboard.shiftInto(pawns & ~(this.pins[ALL] ^ this.pins[VERTICAL]), forward, unoccupied);
        Bitboard.forEach(onePushedPawns & this.target, (final long destination) ->
                explodePawnPromotions(moves, Move.basic(Square.ofBitboard(Bitboard.shiftRev(destination, forward)), Square.ofBitboard(destination), PieceType.PAWN), destination, promotionRank));

        // Pawns that can double push
        final long twoPushedPawns = Bitboard.shiftInto(onePushedPawns & thirdRank, forward, unoccupied);
        Bitboard.forEach(twoPushedPawns & this.target, (final long destination) ->
                moves.add(Move.doublePush(Square.ofBitboard(Bitboard.shift(destination, forward.offset()*-2)), Square.ofBitboard(destination))));

        // Pawns that can attack in the diagonal direction
        final long pawnAttacks1 = Bitboard.shiftInto(pawns & ~(this.pins[ALL] ^ this.pins[DIAGONAL]), attackDir1, (enemies & this.target) | enPassantTarget);
        Bitboard.forEach(pawnAttacks1, (final long attack) ->
                explodePawnCapture(pos, moves, attackDir1, attack, enPassantTarget, promotionRank));

        // Pawns that can attack in the anti-diagonal direction
        final long pawnAttacks2 = Bitboard.shiftInto(pawns & ~(this.pins[ALL] ^ this.pins[ANTI_DIAGONAL]), attackDir2, (enemies & this.target) | enPassantTarget);
        Bitboard.forEach(pawnAttacks2, (final long attack) ->
                explodePawnCapture(pos, moves, attackDir2, attack, enPassantTarget, promotionRank));

        Bitboard.forEach(knights, (final long knight) -> {
            final Square from = Square.ofBitboard(knight);
            Bitboard.forEach(Attack.knight(from) & legalTargetsOf(knight), (final long attack) -> {
                final Square to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.KNIGHT);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceType(to)));
            });
        });

        Bitboard.forEach(queens, (final long queen) -> {
            final Square from = Square.ofBitboard(queen);
            Bitboard.forEach(Attack.queen(from, occupied) & legalTargetsOf(queen), (final long attack) -> {
                final Square to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.QUEEN);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceType(to)));
            });
        });

        Bitboard.forEach(rooks, (final long rook) -> {
            final Square from = Square.ofBitboard(rook);
            Bitboard.forEach(Attack.rook(from, occupied) & legalTargetsOf(rook), (final long attack) -> {
                final Square to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.ROOK);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceType(to)));
            });
        });

        Bitboard.forEach(bishops, (final long bishop) -> {
            final Square from = Square.ofBitboard(bishop);
            Bitboard.forEach(Attack.bishop(from, occupied) & legalTargetsOf(bishop), (final long attack) -> {
                final Square to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.BISHOP);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceType(to)));
            });
        });

        if (this.isCheck) return;

        // Castle moves - only add if not in check

        if (Castling.isKingSideAllowed(pos.sideToMove(), pos.castlingRights(), unoccupied, this.attacked)) moves.add(Castling.getKingSideMove(pos.sideToMove()));
        if (Castling.isQueenSideAllowed(pos.sideToMove(), pos.castlingRights(), unoccupied, this.attacked)) moves.add(Castling.getQueenSideMove(pos.sideToMove()));
    }


    // ====================================================================================================
    //                                  Pin Helpers
    // ====================================================================================================

    /**
     * Calculate the legal target squares for a given single occupancy piece bitboard.
     * If a piece is pinned, it may only move in the direction of the pin.
     * @param piece single occupancy piece bitboard
     * @return legal piece targets
     */
    public long legalTargetsOf(final long piece) {
        if (Bitboard.disjoint(this.pins[ALL], piece))           return this.target;
        if (Bitboard.contains(this.pins[HORIZONTAL], piece))    return this.target & this.pins[HORIZONTAL];
        if (Bitboard.contains(this.pins[VERTICAL], piece))      return this.target & this.pins[VERTICAL];
        if (Bitboard.contains(this.pins[DIAGONAL], piece))      return this.target & this.pins[DIAGONAL];
        if (Bitboard.contains(this.pins[ANTI_DIAGONAL], piece)) return this.target & this.pins[ANTI_DIAGONAL];
        return Bitboard.EMPTY;
    }


    // ====================================================================================================
    //                                  Move Builders
    // ====================================================================================================

    /**
     * If the pawn move ends in the promotion rank, then generate a move per promotion piece type
     * into the move list, otherwise just add the passed move.
     * @param moves move list
     * @param move basic move
     * @param to destination bitboard
     * @param promotionRank promotion rank bitboard
     */
    private void explodePawnPromotions(final MoveList moves, final int move, final long to, final long promotionRank) {
        if (Bitboard.contains(promotionRank, to)) {
            moves.add(Move.addPromotion(move, PieceType.QUEEN));
            moves.add(Move.addPromotion(move, PieceType.KNIGHT));
            moves.add(Move.addPromotion(move, PieceType.ROOK));
            moves.add(Move.addPromotion(move, PieceType.BISHOP));
            return;
        }
        moves.add(move);
    }

    /**
     * If the pawn capture is to the en passant target then add an en passant move to the list, otherwise
     * check for promotions and add the capture to the move list.
     * @param pos current position
     * @param moves move list
     * @param attackDir attack direction
     * @param attack attack bitboard
     * @param enPassantTarget en passant target bitboard
     * @param promotionRank promotion rank bitboard.
     */
    private void explodePawnCapture(final Position pos, final MoveList moves, final Direction attackDir, final long attack, final long enPassantTarget, final long promotionRank) {
        final long pawn = Bitboard.shiftRev(attack, attackDir);
        final Square from = Square.ofBitboard(pawn);
        final Square to = Square.ofBitboard(attack);
        if (attack == enPassantTarget) {
            if (isPinnedEnPassant(pos, pawn)) return;
            moves.add(Move.enPassant(from, to));
            return;
        }
        explodePawnPromotions(moves, Move.capture(from, to, PieceType.PAWN, pos.board().pieceType(to)), attack, promotionRank);
    }


    // ====================================================================================================
    //                                   En Passant Helper
    // ====================================================================================================

    /**
     * Check whether the en passant is pinned. This is a unique kind of move where an en
     * passant reveals check that was previously blocked by both the moving pawn and the
     * captured pawn.
     * @param pos current position
     * @param pawn moving pawn
     * @return is en passant illegal/pinned
     */
    private boolean isPinnedEnPassant(final Position pos, final long pawn) {
        final Colour us             = pos.sideToMove();
        final long king             = pos.board().pieces(us, PieceType.KING);
        final Direction backward    = us.backward();
        final long occupied         = pos.board().occupied();
        final long enPassantTarget  = pos.enPassantTarget().bitboard();
        final long capturedPawn     = Bitboard.shift(enPassantTarget, backward.offset());

        return Bitboard.intersects(Attack.rook(Square.ofBitboard(king), occupied ^ enPassantTarget ^ pawn ^ capturedPawn),
                pos.board().pieces(us.flip(), PieceType.ROOK) | pos.board().pieces(us.flip(), PieceType.QUEEN));
    }
}
