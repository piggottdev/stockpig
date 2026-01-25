package dev.pig.stockpig.chess;

/**
 * Move Generator generates legal moves for a given chess position into a
 * move list.
 */
public final class MoveGenerator {


    // =================================================================================
    //  Checks, Attacks and Pins (required before move gen)
    // =================================================================================

    long attacked;
    long checkers;
    long checkRay;
    long pinned;

    boolean isCheck;
    boolean isDoubleCheck;

    long target;

    final long[] pins = new long[5];
    static final int ALL            = 0;
    static final int HORIZONTAL     = 1;
    static final int VERTICAL       = 2;
    static final int DIAGONAL       = 3;
    static final int ANTI_DIAGONAL  = 4;

    /**
     * Reset all state that won't get reset during normal move generation.
     */
    public void reset() {
        this.attacked = Bitboard.EMPTY;
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

        final long unoccupied = pos.board.unoccupied();

        final Colour us = pos.sideToMove;
        final long friends = pos.board.pieces(us);
        final long king = pos.board.pieces(PieceType.KING) & friends;

        final Colour them = us.flip();
        final long enemies = pos.board.pieces(them);
        final long eKing = pos.board.pieces(PieceType.KING) & enemies;
        final long ePawns = pos.board.pieces(PieceType.PAWN) & enemies;
        final long eKnights = pos.board.pieces(PieceType.KNIGHT) & enemies;
        final long eBishops = pos.board.pieces(PieceType.BISHOP) & enemies;
        final long eRooks = pos.board.pieces(PieceType.ROOK) & enemies;
        final long eQueens = pos.board.pieces(PieceType.QUEEN) & enemies;

        // Attacks

        // TODO: Candidate optimisation: Move towards attack maps to calculate checks threats and pins
        // TODO: Candidate optimisation: Adding a line[from][king] should remove the need to calculate pins in different directions

        // TODO: Candidate optimisation: Kings cant check each other so this could just be an attack map
        // King attacks
        stepAttacks(eKing, king, new Direction[]{
                Direction.N, Direction.NE, Direction.E, Direction.SE,
                Direction.S, Direction.SW, Direction.W, Direction.NW
        });
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

        final int checkCount = Bitboard.count(this.checkers);
        this.isCheck = checkCount > 0;
        this.isDoubleCheck = checkCount > 1;

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
    public void pins(final long king, final long friends, final long enemies, final long eSliders, final int axis, final Direction[] ds) {
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
    public void slidingAttacks(final long pieces, final long unoccupied, final Direction[] ds) {
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
    public void stepAttacks(final long pieces, final long king, final Direction[] ds) {
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
    public void pin(final long king, final long friends, final long enemies, final long eSliders, final int axis, final Direction d) {
        final long line = Attacks.slide(king, ~enemies, d);
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
    public void slidingAttack(final long pieces, final long unoccupied, final Direction d) {
        this.attacked |= Attacks.slide(pieces, unoccupied, d);
    }

    /**
     * Calculate any step attacks (single moves) in given direction.
     * @param pieces enemy step attackers
     * @param king king
     * @param d direction
     */
    public void stepAttack(final long pieces, final long king, final Direction d) {
        final long attacks = Bitboard.shift(pieces, d);
        if (Bitboard.intersects(king, attacks)) {
            this.checkers |= Bitboard.shiftRev(king, d);
        }
        this.attacked |= attacks;
    }


    // =================================================================================
    //  Move Generation
    // =================================================================================

    /**
     * Generate all legal moves into moves given current checks, attacks and pins.
     * @param pos current position
     * @param moves move list
     */
    public void generate(final Position pos, final MoveList moves) {
        attackAnalysis(pos);

        kingMoves(pos, moves);

        if (this.isDoubleCheck) return; // Only king moves get out of double check

        pawnMoves   (pos, moves);
        knightMoves (pos, moves);
        bishopMoves (pos, moves);
        rookMoves   (pos, moves);
        queenMoves  (pos, moves);

        if (this.isCheck) return; // Any other moves are dependent on not being in check

        castleMoves(pos, moves);
    }


    // =================================================================================
    //  King Moves
    // =================================================================================

    /**
     * Generate all legal king moves (except castles) into move list.
     * @param pos current position
     * @param moves move list
     */
    public void kingMoves(final Position pos, final MoveList moves) {
        final Colour us             = pos.sideToMove;
        final long king             = pos.board.pieces(us, PieceType.KING);
        final long unoccupied       = pos.board.unoccupied();
        final long enemies          = pos.board.pieces(us.flip());

        Bitboard.forEachBit(Attacks.king(king) & ~this.attacked & (unoccupied | enemies), (final long destination) ->
                basicOrCapture(pos, moves, king, destination, PieceType.KING, unoccupied));
    }

    /**
     * Generate any legal castle moves into move list.
     * @param pos current position
     * @param moves move list
     */
    public void castleMoves(final Position pos, final MoveList moves) {
        final long unoccupied = pos.board.unoccupied();
        if (Castling.isKingSideAllowed(pos.sideToMove, pos.castlingRights, unoccupied, this.attacked)) moves.add(Castling.getKingSideMove(pos.sideToMove));
        if (Castling.isQueenSideAllowed(pos.sideToMove, pos.castlingRights, unoccupied, this.attacked)) moves.add(Castling.getQueenSideMove(pos.sideToMove));
    }


    // =================================================================================
    //  Queen Moves
    // =================================================================================

    /**
     * Generate all legal queen moves into move list.
     * @param pos current position
     * @param moves move list
     */
    public void queenMoves(final Position pos, final MoveList moves) {
        final Colour us             = pos.sideToMove;
        final long queens           = pos.board.pieces(us, PieceType.QUEEN);
        final long unoccupied       = pos.board.unoccupied();

        // For each queen
        Bitboard.forEachBit(queens, (final long queen) -> {
            long targets = this.target;
            // If the queen is pinned horizontally, it must remain in that pin
            if (Bitboard.contains(this.pins[HORIZONTAL], queen)) {
                targets &= this.pins[HORIZONTAL];
            }
            // If the queen is pinned vertically, it must remain in that pin
            else if (Bitboard.contains(this.pins[VERTICAL], queen)) {
                targets &= this.pins[VERTICAL];
            }
            // If the queen is pinned diagonally, it must remain in that pin
            else if (Bitboard.contains(this.pins[DIAGONAL], queen)) {
                targets &= this.pins[DIAGONAL];
            }
            // If the queen is pinned anti-diagonally, it must remain in that pin
            else if (Bitboard.contains(this.pins[ANTI_DIAGONAL], queen)) {
                targets &= this.pins[ANTI_DIAGONAL];
            }
            // For each legal destination
            Bitboard.forEachBit(Attacks.queen(queen, unoccupied) & targets, (final long destination) ->
                    basicOrCapture(pos, moves, queen, destination, PieceType.QUEEN, unoccupied));
        });
    }


    // =================================================================================
    //  Rook Moves
    // =================================================================================

    /**
     * Generate all legal rook moves into move list.
     * @param pos current position
     * @param moves move list
     */
    public void rookMoves(final Position pos, final MoveList moves) {
        final Colour us             = pos.sideToMove;
        final long rooks            = pos.board.pieces(us, PieceType.ROOK);
        final long unoccupied       = pos.board.unoccupied();

        // For each rook
        Bitboard.forEachBit(rooks & ~(this.pins[ALL] ^ this.pins[HORIZONTAL] ^ this.pins[VERTICAL]), (final long rook) -> {
            long targets = this.target;
            // If the rook is pinned horizontally, it must remain in that pin
            if (Bitboard.contains(this.pins[HORIZONTAL], rook)) {
                targets &= this.pins[HORIZONTAL];
            }
            // If the rook is pinned vertically, it must remain in that pin
            else if (Bitboard.contains(this.pins[VERTICAL], rook)) {
                targets &= this.pins[VERTICAL];
            }
            // For each legal destination
            Bitboard.forEachBit(Attacks.rook(rook, unoccupied) & targets, (final long destination) ->
                    basicOrCapture(pos, moves, rook, destination, PieceType.ROOK, unoccupied));
        });
    }


    // =================================================================================
    //  Bishop Moves
    // =================================================================================

    /**
     * Generate all legal bishop moves into move list.
     * @param pos current position
     * @param moves move list
     */
    public void bishopMoves(final Position pos, final MoveList moves) {
        final Colour us             = pos.sideToMove;
        final long bishops          = pos.board.pieces(us, PieceType.BISHOP);
        final long unoccupied       = pos.board.unoccupied();

        // For each bishop
        Bitboard.forEachBit(bishops & ~(this.pins[ALL] ^ this.pins[DIAGONAL] ^ this.pins[ANTI_DIAGONAL]), (final long bishop) -> {
            long targets = this.target;
            // If the bishop is pinned diagonally, it must remain in that pin
            if (Bitboard.contains(this.pins[DIAGONAL], bishop)) {
                targets &= this.pins[DIAGONAL];
            }
            // If the bishop is pinned anti-diagonally, it must remain in that pin
            else if (Bitboard.contains(this.pins[ANTI_DIAGONAL], bishop)) {
                targets &= this.pins[ANTI_DIAGONAL];
            }
            // For each legal destination
            Bitboard.forEachBit(Attacks.bishop(bishop, unoccupied) & targets, (final long destination) ->
                    basicOrCapture(pos, moves, bishop, destination, PieceType.BISHOP, unoccupied));
        });
    }


    // =================================================================================
    //  Knight Moves
    // =================================================================================

    /**
     * Generate all legal knight moves into move list.
     * @param pos current position
     * @param moves move list
     */
    public void knightMoves(final Position pos, final MoveList moves) {
        final Colour us             = pos.sideToMove;
        final long knights          = pos.board.pieces(us, PieceType.KNIGHT);
        final long unoccupied       = pos.board.unoccupied();

        // For each knight
        Bitboard.forEachBit(knights & ~this.pins[ALL], (final long knight) ->
                Bitboard.forEachBit(Attacks.knight(knight) & this.target, (final long destination) ->
                        basicOrCapture(pos, moves, knight, destination, PieceType.KNIGHT, unoccupied)));
    }


    // =================================================================================
    //  Pawn Moves
    // =================================================================================

    /**
     * Generate all legal pawn moves into move list.
     * @param pos current position
     * @param moves move list
     */
    public void pawnMoves(final Position pos, final MoveList moves) {
        final Colour us             = pos.sideToMove;
        final long pawns            = pos.board.pieces(us, PieceType.PAWN);
        final long unoccupied       = pos.board.unoccupied();
        final long enemies          = pos.board.pieces(us.flip());
        final Direction forward     = us.forward();
        final long thirdRank        = us.rank3().bitboard();
        final long promotionRank    = us.rank8().bitboard();
        final long enPassantTarget  = pos.enPassantTarget.bitboard();
        final Direction attackDir1  = us.pawnAttackDirection1();
        final Direction attackDir2  = us.pawnAttackDirection2();

        // Pawns that can push forward one
        final long onePushedPawns = Bitboard.shiftInto(pawns & ~(this.pins[ALL] ^ this.pins[VERTICAL]), forward, unoccupied);
        Bitboard.forEachBit(onePushedPawns & this.target, (final long destination) ->
                explodePawnPromotions(moves, Move.basic(Square.fromBitboard(Bitboard.shiftRev(destination, forward)), Square.fromBitboard(destination), PieceType.PAWN), destination, promotionRank));

        // Pawns that can double push
        final long twoPushedPawns = Bitboard.shiftInto(onePushedPawns & thirdRank, forward, unoccupied);
        Bitboard.forEachBit(twoPushedPawns & this.target, (final long destination) ->
                moves.add(Move.doublePush(Square.fromBitboard(Bitboard.shift(destination, forward.offset()*-2)), Square.fromBitboard(destination))));

        // Pawns that can attack in the diagonal direction
        final long pawnAttacks1 = Bitboard.shiftInto(pawns & ~(this.pins[ALL] ^ this.pins[DIAGONAL]), attackDir1, (enemies & this.target) | enPassantTarget);
        Bitboard.forEachBit(pawnAttacks1, (final long attack) ->
                explodePawnCapture(pos, moves, attackDir1, attack, enPassantTarget, promotionRank));

        // Pawns that can attack in the anti-diagonal direction
        final long pawnAttacks2 = Bitboard.shiftInto(pawns & ~(this.pins[ALL] ^ this.pins[ANTI_DIAGONAL]), attackDir2, (enemies & this.target) | enPassantTarget);
        Bitboard.forEachBit(pawnAttacks2, (final long attack) ->
                explodePawnCapture(pos, moves, attackDir2, attack, enPassantTarget, promotionRank));
    }

    /**
     * If the pawn move ends in the promotion rank, then generate a move per promotion piece type
     * into the move list, otherwise just add the passed move.
     * @param moves move list
     * @param move basic move
     * @param to destination bitboard
     * @param promotionRank promotion rank bitboard
     */
    public void explodePawnPromotions(final MoveList moves, final int move, final long to, final long promotionRank) {
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
    public void explodePawnCapture(final Position pos, final MoveList moves, final Direction attackDir, final long attack, final long enPassantTarget, final long promotionRank) {
        final long pawn = Bitboard.shiftRev(attack, attackDir);
        final Square from = Square.fromBitboard(pawn);
        final Square to = Square.fromBitboard(attack);
        if (attack == enPassantTarget) {
            if (isPinnedEnPassant(pos, pawn)) return;
            moves.add(Move.enPassant(from, to));
            return;
        }
        explodePawnPromotions(moves, Move.capture(from, to, PieceType.PAWN, pos.board.pieceType(attack)), attack, promotionRank);
    }

    /**
     * Check whether the en passant is pinned. This is a unique kind of move where an en
     * passant reveals check that was previously blocked by both the moving pawn and the
     * captured pawn.
     * @param pos current position
     * @param pawn moving pawn
     * @return is en passant illegal/pinned
     */
    public boolean isPinnedEnPassant(final Position pos, final long pawn) {
        final Colour us             = pos.sideToMove;
        final long king             = pos.board.pieces(us, PieceType.KING);
        final Direction backward    = us.backward();
        final long unoccupied       = pos.board.unoccupied();
        final long enPassantTarget  = pos.enPassantTarget.bitboard();
        final long capturedPawn     = Bitboard.shift(enPassantTarget, backward.offset());

        return Bitboard.intersects(Attacks.rook(king, (unoccupied | pawn | capturedPawn) ^ enPassantTarget),
                pos.board.pieces(us.flip(), PieceType.ROOK) | pos.board.pieces(us.flip(), PieceType.QUEEN));
    }


    // =================================================================================
    //  Helpers
    // =================================================================================

    /**
     * If the destination square of the given move is occupied, find which piece type occupies it
     * and add it as a capture to the move, then add the move (capture or not) into the move list.
     * @param pos current position
     * @param moves move list
     * @param from from bitboard
     * @param to to bitboard
     * @param pt moving piece type
     * @param unoccupied unoccupied bitboard
     */
    public void basicOrCapture(final Position pos, final MoveList moves, final long from, final long to, final PieceType pt, final long unoccupied) {
        final int basicMove = Move.basic(Square.fromBitboard(from), Square.fromBitboard(to), pt);
        moves.add(Bitboard.intersects(unoccupied, to) ?
                basicMove :
                Move.addCapture(basicMove, pos.board.pieceType(to))
        );
    }
}
