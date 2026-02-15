package dev.pig.stockpig.chess.core;

import dev.pig.stockpig.chess.core.bitboard.Attack;
import dev.pig.stockpig.chess.core.bitboard.Bitboard;
import dev.pig.stockpig.chess.core.bitboard.Direction;
import dev.pig.stockpig.chess.core.bitboard.Square;

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
    private long pinners;
    private long target;


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
     * Get the bitboard of currently pinned pieces.
     * @return pinned pieces bitboard
     */
    public long pinned() {
        return this.pinned;
    }

    /**
     * Get the bitboard of currently pinning pieces.
     * @return pinning pieces bitboard
     */
    public long pinners() {
        return this.pinners;
    }

    /**
     * Get the current side's target square bitboard. This is the checkray/checkers if
     * the position is in check or all enemy and unoccupied squares if not.
     * @return target bitboard
     */
    public long target() {
        return this.target;
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
        this.pinners  = Bitboard.EMPTY;
    }

    /**
     * Analyse checks, attacks and pins, caching results for move generation.
     * @param pos position
     */
    public void attackAnalysis(final Position pos) {
        reset();

        // Side to move variables
        final boolean us                = pos.sideToMove();
        final long team                 = pos.board().pieces(us);
        final long king                 = pos.board().pieces(PieceType.KING) & team;
        final byte kingSq               = Square.ofBitboard(king);

        // Enemy team variables
        final boolean them              = Colour.flip(us);
        final long enemies              = pos.board().pieces(them);
        final long eKing                = pos.board().pieces(PieceType.KING) & enemies;
        final long ePawns               = pos.board().pieces(PieceType.PAWN) & enemies;
        final long eKnights             = pos.board().pieces(PieceType.KNIGHT) & enemies;
        final long eBishops             = pos.board().pieces(PieceType.BISHOP) & enemies;
        final long eRooks               = pos.board().pieces(PieceType.ROOK) & enemies;
        final long eQueens              = pos.board().pieces(PieceType.QUEEN) & enemies;
        final long eDiagonals           = eQueens | eBishops;
        final long eOrthogonals         = eQueens | eRooks;
        final Direction pawnAttackDir1  = Colour.pawnAttackDirection1(them);
        final Direction pawnAttackDir2  = Colour.pawnAttackDirection2(them);

        // Board variables
        final long unoccupied           = pos.board().unoccupied();
        final long occupied             = ~unoccupied;
        final long occupiedWithoutKing  = occupied ^ king;


        // Attacks

        // King attacks
        this.attacked = Attack.king(Square.ofBitboard(eKing));

        // Pawn attacks
        final long p1Attacks = Bitboard.shift(ePawns, pawnAttackDir1);
        final long p2Attacks = Bitboard.shift(ePawns, pawnAttackDir2);
        this.attacked |= p1Attacks;
        this.attacked |= p2Attacks;
        if (Bitboard.intersects(p1Attacks, king)) this.checkers |= Bitboard.shiftRev(king, pawnAttackDir1);
        if (Bitboard.intersects(p2Attacks, king)) this.checkers |= Bitboard.shiftRev(king, pawnAttackDir2);

        // Knight attacks
        long knights = eKnights;
        while (knights != 0L) {
            final long knight = Bitboard.pop(knights);
            final long attacks = Attack.knight(Square.ofBitboard(knight));
            this.attacked |= attacks;
            if (Bitboard.intersects(attacks, king)) this.checkers |= knight;
            knights ^= knight;
        }

        // Sliding Attacks
        long sliders = eDiagonals;
        while (sliders != 0L) {
            final long slider = Bitboard.pop(sliders);
            this.attacked |= Attack.bishop(Square.ofBitboard(slider), occupiedWithoutKing);
            sliders ^= slider;
        }
        sliders = eOrthogonals;
        while (sliders != 0L) {
            final long slider = Bitboard.pop(sliders);
            this.attacked |= Attack.rook(Square.ofBitboard(slider), occupiedWithoutKing);
            sliders ^= slider;
        }

        // Sliding pins and checks
        long pinners = (Attack.bishop(kingSq, enemies) & eDiagonals) | (Attack.rook(kingSq, enemies) & eOrthogonals);
        while (pinners != 0L) {
            final long pinner = Bitboard.pop(pinners);
            pinners ^= pinner;

            final long pin = Bitboard.ray(kingSq, Square.ofBitboard(pinner));
            final long pinned = pin & team;

            // No blockers - this is check
            if (Bitboard.isEmpty(pinned)) {
                this.checkers |= pinner;
                this.checkRay |= pin;
                continue;
            }

            // Single blocker - this is pin
            if (Bitboard.isSingle(pinned)) {
                this.pinners |= pinner;
                this.pinned |= pinned;
            }
        }

        this.isCheck = !Bitboard.isEmpty(this.checkers);
        this.isDoubleCheck = this.isCheck && !Bitboard.isSingle(this.checkers);

        this.target = this.isCheck ? this.checkRay | this.checkers : enemies | unoccupied;
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

        final boolean us            = pos.sideToMove();
        final long unoccupied       = pos.board().unoccupied();
        final long occupied         = ~unoccupied;
        final long team             = pos.board().pieces(us);
        final long enemies          = pos.board().pieces(Colour.flip(us));
        final long king             = pos.board().pieces(PieceType.KING) & team;


        // King moves - only king moves can get out of double check

        final byte kFrom = Square.ofBitboard(king);
        Bitboard.forEach(Attack.king(kFrom) & ~this.attacked & (unoccupied | enemies), (final long attack) -> {
            final byte to = Square.ofBitboard(attack);
            final int basic = Move.basic(kFrom, to, PieceType.KING);
            moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceAt(to)));
        });

        if (this.isDoubleCheck) return;


        // All other moves (except castles)

        final long pawns          = pos.board().pieces(PieceType.PAWN) & team;
        final long queens         = pos.board().pieces(PieceType.QUEEN) & team;
        final long knights        = pos.board().pieces(PieceType.KNIGHT) & team;
        final long rooks          = pos.board().pieces(PieceType.ROOK) & team;
        final long bishops        = pos.board().pieces(PieceType.BISHOP) & team;

        final Direction forward     = Colour.forward(us);
        final Direction attackDir1  = Colour.pawnAttackDirection1(us);
        final Direction attackDir2  = Colour.pawnAttackDirection2(us);
        final long thirdRank        = Colour.rank3(us);
        final long promotionRank    = Colour.rank8(us);
        final long enPassantTarget  = pos.enPassantTarget() == Square.EMPTY ? Bitboard.EMPTY : Bitboard.ofSquare(pos.enPassantTarget());

        // Pawns that can push forward one
        final long onePushedPawns = Bitboard.shiftInto(pawns & (~this.pinned | Bitboard.fileOf(kFrom)), forward, unoccupied);
        Bitboard.forEach(onePushedPawns & this.target, (final long destination) ->
                explodePawnPromotions(moves, Move.basic(Square.ofBitboard(Bitboard.shiftRev(destination, forward)), Square.ofBitboard(destination), PieceType.PAWN), destination, promotionRank));

        // Pawns that can double push
        final long twoPushedPawns = Bitboard.shiftInto(onePushedPawns & thirdRank, forward, unoccupied);
        Bitboard.forEach(twoPushedPawns & this.target, (final long destination) ->
                moves.add(Move.doublePush(Square.ofBitboard(Bitboard.shift(destination, forward.offset()*-2)), Square.ofBitboard(destination))));

        // Pawns that can attack in the diagonal direction
        final long pawnAttacks1 = Bitboard.shiftInto(pawns & (~this.pinned | Bitboard.diagonalOf(kFrom)), attackDir1, (enemies & this.target) | enPassantTarget);
        Bitboard.forEach(pawnAttacks1, (final long attack) ->
                explodePawnCapture(pos, moves, attackDir1, attack, enPassantTarget, promotionRank));

        // Pawns that can attack in the anti-diagonal direction
        final long pawnAttacks2 = Bitboard.shiftInto(pawns & (~this.pinned | Bitboard.antiDiagonalOf(kFrom)), attackDir2, (enemies & this.target) | enPassantTarget);
        Bitboard.forEach(pawnAttacks2, (final long attack) ->
                explodePawnCapture(pos, moves, attackDir2, attack, enPassantTarget, promotionRank));

        Bitboard.forEach(knights, (final long knight) -> {
            final byte from = Square.ofBitboard(knight);
            Bitboard.forEach(Attack.knight(from) & legalTargetsOf(knight, from, kFrom), (final long attack) -> {
                final byte to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.KNIGHT);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceAt(to)));
            });
        });

        Bitboard.forEach(queens, (final long queen) -> {
            final byte from = Square.ofBitboard(queen);
            Bitboard.forEach(Attack.queen(from, occupied) & legalTargetsOf(queen, from, kFrom), (final long attack) -> {
                final byte to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.QUEEN);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceAt(to)));
            });
        });

        Bitboard.forEach(rooks, (final long rook) -> {
            final byte from = Square.ofBitboard(rook);
            Bitboard.forEach(Attack.rook(from, occupied) & legalTargetsOf(rook, from, kFrom), (final long attack) -> {
                final byte to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.ROOK);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceAt(to)));
            });
        });

        Bitboard.forEach(bishops, (final long bishop) -> {
            final byte from = Square.ofBitboard(bishop);
            Bitboard.forEach(Attack.bishop(from, occupied) & legalTargetsOf(bishop, from, kFrom), (final long attack) -> {
                final byte to = Square.ofBitboard(attack);
                final int basic = Move.basic(from, to, PieceType.BISHOP);
                moves.add(Bitboard.intersects(unoccupied, attack) ? basic : Move.addCapture(basic, pos.board().pieceAt(to)));
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
     * Calculate the legal target squares for a given piece square.
     * If a piece is pinned, it may only move in the direction of the pin.
     * @param piece single occupancy piece bitboard
     * @param pieceSq piece square
     * @param kingSq king square
     * @return legal piece targets
     */
    public long legalTargetsOf(final long piece, final byte pieceSq, final byte kingSq) {
        return Bitboard.disjoint(this.pinned, piece) ? this.target : this.target & Bitboard.line(kingSq, pieceSq);
    }


    // ====================================================================================================
    //                                  Move Exploders
    // ====================================================================================================

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
        final byte from = Square.ofBitboard(pawn);
        final byte to = Square.ofBitboard(attack);
        if (attack == enPassantTarget) {
            if (isPinnedEnPassant(pos, pawn)) return;
            moves.add(Move.enPassant(from, to));
            return;
        }
        explodePawnPromotions(moves, Move.capture(from, to, PieceType.PAWN, pos.board().pieceAt(to)), attack, promotionRank);
    }

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
        final boolean us            = pos.sideToMove();
        final long king             = pos.board().pieces(us, PieceType.KING);
        final Direction backward    = Colour.backward(us);
        final long occupied         = pos.board().occupied();
        final long enPassantTarget  = Bitboard.ofSquare(pos.enPassantTarget());
        final long capturedPawn     = Bitboard.shift(enPassantTarget, backward.offset());

        return Bitboard.intersects(Attack.rook(Square.ofBitboard(king), occupied ^ enPassantTarget ^ pawn ^ capturedPawn),
                pos.board().pieces(Colour.flip(us), PieceType.ROOK) | pos.board().pieces(Colour.flip(us), PieceType.QUEEN));
    }
}
