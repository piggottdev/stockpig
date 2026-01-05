package dev.pig.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to analyse a given chess position and calculate legal moves.
 * For quicker legal move generation, the following are calculated before hand:
 * <ul>
 *     <li>Check, and the cause(s)</li>
 *     <li>Attacked, checked and threatened squares</li>
 *     <li>Pinned pieces</li>
 * </ul>
 */
class MoveGenerator {

    // Values needed from game for move generation
    private final boolean isWhiteTurn;
    private final Board board;
    private final int castlesAllowed;
    private final long enPassantTarget;

    // Common board values required
    private final long unoccupied;
    private final int team;
    private final long pieces;
    private final long king;
    private final int enemyTeam;
    private final long enemyPieces;

    // Check state
    private boolean isCheck = false;
    private boolean isDoubleCheck = false; // Whether there is check from more than one piece

    // Useful bitboards
    private long threatened = 0L; // The squares that the enemy team threatens
    private long movableSquares;

    // Pins include enemy piece but not the king
    private long allPin = 0L;
    private final long[] pins = new long[4];
    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int DIAGONAL_POSITIVE = 2;
    private static final int DIAGONAL_NEGATIVE = 3;

    // List for pushing moves to
    private final List<ChessMove> moves = new ArrayList<>(100);

    MoveGenerator(final Board board, final boolean isWhiteTurn, final int castlesAllowed, final long enPassantTarget) {
        this.board = board;
        this.isWhiteTurn = isWhiteTurn;
        this.castlesAllowed = castlesAllowed;
        this.enPassantTarget = enPassantTarget;

        unoccupied = board.getPieceBitboard(Piece.UNOCCUPIED);

        team = Piece.getTeam(isWhiteTurn);
        pieces = board.getPieceBitboard(team);
        king = board.getPieceBitboard(team | Piece.KING);

        enemyTeam = Piece.getTeam(!isWhiteTurn);
        enemyPieces = board.getPieceBitboard(enemyTeam);

        movableSquares = (enemyPieces | unoccupied);

        calculateThreatsAndPins();
    }

    /**
     * Whether the position is in check.
     *
     * @return check or not
     */
    boolean isCheck() {
        return isCheck;
    }

    /**
     * Bitboard showing all threatened squares by the none moving team.
     * Rays go through the king.
     *
     * @return bitboard showing checked/threatened squares
     */
    long getThreatened() {
        return threatened;
    }

    /**
     * Get the bitboard which none king pieces can move into.
     * If not check, this is any square that is not an ally.
     * If check, this shows squares that will block/remove check.
     *
     * @return bitboard showing squares none king pieces can move to
     */
    long getMovableSquares() {
        return movableSquares;
    }

    /**
     * Get bitboard showing all squares that form pins.
     * The pins include the enemy piece causing the pin but not the king.
     *
     * @return bitboard of pins
     */
    long getAllPin() {
        return allPin;
    }

    /**
     * Generate and get all legal moves for the position.
     *
     * @return all legal moves
     */
    List<ChessMove> generateLegalMoves() {

        if (isDoubleCheck) {
            kingMoves();
        } else if (isCheck) {
            pawnMoves();
            knightMoves();
            bishopMoves();
            rookMoves();
            kingMoves();
            queenMoves();
        } else {

            final ChessMove castleKsMove = Castling.getKingSideCastleIfPossible(castlesAllowed, isWhiteTurn, unoccupied, threatened);
            final ChessMove castleQsMove = Castling.getQueenSideCastleIfPossible(castlesAllowed, isWhiteTurn, unoccupied, threatened);
            if (castleKsMove != null) moves.add(castleKsMove);
            if (castleQsMove != null) moves.add(castleQsMove);

            pawnMoves();
            knightMoves();
            bishopMoves();
            queenMoves();
            rookMoves();

            kingMoves();
        }

        return moves;
    }

    // -- Move Generation --

    private void pawnMoves() {

        final int pawnPiece = team | Piece.PAWN;
        final long pawns = board.getPieceBitboard(pawnPiece);

        // Pushing (NORTH/SOUTH)
        final long pushablePawns = pawns & ~(allPin ^ pins[VERTICAL]);

        // -- Single Push
        final long oneBack = Bitboard.directionalShift(unoccupied & movableSquares, Piece.getBackwardDirection(isWhiteTurn));

        long onePushablePawns = oneBack & pushablePawns;
        while (onePushablePawns != Bitboard.EMPTY) {
            final long onePushablePawn = Long.lowestOneBit(onePushablePawns);

            final long to = Bitboard.directionalShift(onePushablePawn, Piece.getForwardDirection(isWhiteTurn));
            if (Bitboard.intersects(to, Piece.getPawnPromotionRank(isWhiteTurn))) {
                moves.add(ChessMove.pawnPromotion(onePushablePawn, to, pawnPiece, team | Piece.QUEEN));
                moves.add(ChessMove.pawnPromotion(onePushablePawn, to, pawnPiece, team | Piece.KNIGHT));
                moves.add(ChessMove.pawnPromotion(onePushablePawn, to, pawnPiece, team | Piece.ROOK));
                moves.add(ChessMove.pawnPromotion(onePushablePawn, to, pawnPiece, team | Piece.BISHOP));
            } else {
                moves.add(ChessMove.basicMove(onePushablePawn, to, pawnPiece));
            }
            onePushablePawns ^= onePushablePawn;
        }

        // -- Double Push
        long twoPushablePawns = pushablePawns & Piece.getPawnStartingRank(isWhiteTurn) & Bitboard.directionalShift(oneBack & unoccupied, Piece.getBackwardDirection((isWhiteTurn)));
        while (twoPushablePawns != Bitboard.EMPTY) {
            final long twoPushablePawn = Long.lowestOneBit(twoPushablePawns);

            final long emptySquare = Bitboard.directionalShift(twoPushablePawn, Piece.getForwardDirection(isWhiteTurn));
            final long to = Bitboard.directionalShift(emptySquare, Piece.getForwardDirection(isWhiteTurn));
            moves.add(ChessMove.doublePush(twoPushablePawn, to, pawnPiece, emptySquare));

            twoPushablePawns ^= twoPushablePawn;
        }

        // Captures
        // -- Direction one (Diagonal Positive)
        long pawnsWithPositiveAttack = Bitboard.directionalShiftBoundedWithinArea(enPassantTarget | (movableSquares & enemyPieces), Piece.getPawnAttackingDirections(!isWhiteTurn)[0], pawns & ~(allPin ^ pins[DIAGONAL_POSITIVE]));
        while (pawnsWithPositiveAttack != Bitboard.EMPTY) {
            final long pawnWithPositiveAttack = Long.lowestOneBit(pawnsWithPositiveAttack);

            final long to = Bitboard.directionalShift(pawnWithPositiveAttack, Piece.getPawnAttackingDirections(isWhiteTurn)[0]);
            if (Bitboard.intersects(to, enPassantTarget)) {
                final long capturedPawn = Bitboard.oppositeDirectionalShift(to, Piece.getForwardDirection(isWhiteTurn));
                if (!isIllegalEnPassantState(pawnWithPositiveAttack, capturedPawn)) {
                    moves.add(ChessMove.enPassantCapture(pawnWithPositiveAttack, to, pawnPiece, enemyTeam | Piece.PAWN, capturedPawn));
                }
            } else if (Bitboard.intersects(to, Piece.getPawnPromotionRank(isWhiteTurn))) {
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithPositiveAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.QUEEN));
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithPositiveAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.KNIGHT));
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithPositiveAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.ROOK));
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithPositiveAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.BISHOP));
            } else {
                moves.add(ChessMove.basicCapture(pawnWithPositiveAttack, to, pawnPiece, board.getPieceAtBit(to)));
            }
            pawnsWithPositiveAttack ^= pawnWithPositiveAttack;
        }

        // -- Direction two (Diagonal Negative)
        long pawnsWithNegativeAttack = Bitboard.directionalShiftBoundedWithinArea(enPassantTarget | (movableSquares & enemyPieces), Piece.getPawnAttackingDirections(!isWhiteTurn)[1], pawns & ~(allPin ^ pins[DIAGONAL_NEGATIVE]));
        while (pawnsWithNegativeAttack != Bitboard.EMPTY) {
            final long pawnWithNegativeAttack = Long.lowestOneBit(pawnsWithNegativeAttack);

            final long to = Bitboard.directionalShift(pawnWithNegativeAttack, Piece.getPawnAttackingDirections(isWhiteTurn)[1]);
            if (Bitboard.intersects(to, enPassantTarget)) {
                final long capturedPawn = Bitboard.oppositeDirectionalShift(to, Piece.getForwardDirection(isWhiteTurn));
                if (!isIllegalEnPassantState(pawnWithNegativeAttack, capturedPawn)) {
                    moves.add(ChessMove.enPassantCapture(pawnWithNegativeAttack, to, pawnPiece, enemyTeam | Piece.PAWN, capturedPawn));
                }
            } else if (Bitboard.intersects(to, Piece.getPawnPromotionRank(isWhiteTurn))) {
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithNegativeAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.QUEEN));
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithNegativeAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.KNIGHT));
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithNegativeAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.ROOK));
                moves.add(ChessMove.pawnPromotionWithCapture(pawnWithNegativeAttack, to, pawnPiece, board.getPieceAtBit(to), team | Piece.BISHOP));
            } else {
                moves.add(ChessMove.basicCapture(pawnWithNegativeAttack, to, pawnPiece, board.getPieceAtBit(to)));
            }
            pawnsWithNegativeAttack ^= pawnWithNegativeAttack;
        }
    }

    private void queenMoves() {

        final int queenPiece = team | Piece.QUEEN;
        long queens = board.getPieceBitboard(queenPiece);

        while (queens != Bitboard.EMPTY) {
            final long queen = Long.lowestOneBit(queens);

            if (Bitboard.intersects(allPin, queen)) {
                // Queen is pinned somehow
                // For each pin, the queen can only move in the direction of the pin
                if (Bitboard.intersects(queen, pins[HORIZONTAL] | pins[VERTICAL])) {
                    if (Bitboard.intersects(queen, pins[HORIZONTAL])) {
                        slidingPieceMoves(queen, queenPiece, Bitboard.HORIZONTAL);
                    } else {
                        slidingPieceMoves(queen, queenPiece, Bitboard.VERTICAL);
                    }
                } else {
                    if (Bitboard.intersects(queen, pins[DIAGONAL_POSITIVE])) {
                        slidingPieceMoves(queen, queenPiece, Bitboard.DIAGONAL_POSITIVE);
                    } else {
                        slidingPieceMoves(queen, queenPiece, Bitboard.DIAGONAL_NEGATIVE);
                    }
                }
            } else {
                slidingPieceMoves(queen, queenPiece, Bitboard.EVERY_DIRECTION);
            }
            queens ^= queen;
        }
    }

    private void bishopMoves() {

        final int bishopPiece = team | Piece.BISHOP;
        long bishops = board.getPieceBitboard(bishopPiece);

        while (bishops != Bitboard.EMPTY) {
            final long bishop = Long.lowestOneBit(bishops);

            if (Bitboard.intersects(allPin, bishop)) {
                // Bishop is pinned somehow
                if (!Bitboard.intersects(bishop, pins[HORIZONTAL] | pins[VERTICAL])) {
                    // Bishop can only move if the pin is NOT cardinal
                    if (Bitboard.intersects(bishop, pins[DIAGONAL_POSITIVE])) {
                        // If the pin is diagonal positive, the bishop can only move diagonal positive
                        slidingPieceMoves(bishop, bishopPiece, Bitboard.DIAGONAL_POSITIVE);
                    } else {
                        // If the pin is diagonal negative, the bishop can only move diagonal negative
                        slidingPieceMoves(bishop, bishopPiece, Bitboard.DIAGONAL_NEGATIVE);
                    }
                }
            } else {
                slidingPieceMoves(bishop, bishopPiece, Bitboard.DIAGONAL);
            }
            bishops ^= bishop;
        }
    }

    private void rookMoves() {

        final int rookPiece = team | Piece.ROOK;
        long rooks = board.getPieceBitboard(rookPiece);

        while (rooks != Bitboard.EMPTY) {
            final long rook = Long.lowestOneBit(rooks);

            if (Bitboard.intersects(allPin, rook)) {
                // Rook is pinned somehow
                if (!Bitboard.intersects(rook, pins[DIAGONAL_NEGATIVE] | pins[DIAGONAL_POSITIVE])) {
                    // Rook can only move if the pin is NOT diagonal
                    if (Bitboard.intersects(rook, pins[HORIZONTAL])) {
                        // If the pin is horizontal, rook can only move horizontally
                        slidingPieceMoves(rook, rookPiece, Bitboard.HORIZONTAL);
                    } else {
                        // If the pin is vertical, rook can only move vertically
                        slidingPieceMoves(rook, rookPiece, Bitboard.VERTICAL);
                    }
                }
            } else {
                slidingPieceMoves(rook, rookPiece, Bitboard.CARDINAL);
            }
            rooks ^= rook;
        }
    }

    private void knightMoves() {

        final int knightPiece = team | Piece.KNIGHT;
        long knights = board.getPieceBitboard(knightPiece) &~ allPin;

        while (knights != Bitboard.EMPTY) {
            final long knight = Long.lowestOneBit(knights);

            for (int direction : Bitboard.L_SHAPES) {

                final long to = Bitboard.directionalShiftBoundedWithinArea(knight, direction, movableSquares);

                if (to != Bitboard.EMPTY) {
                    if (Bitboard.intersects(unoccupied, to)) {
                        moves.add(ChessMove.basicMove(knight, to, knightPiece));
                    } else {
                        moves.add(ChessMove.basicCapture(knight, to, knightPiece, board.getPieceAtBit(to)));
                    }
                }
            }
            knights ^= knight;
        }
    }

    private void kingMoves() {
        final long targetSquares = (unoccupied | enemyPieces) & ~threatened;

        for (int direction : Bitboard.EVERY_DIRECTION) {

            final long to = Bitboard.directionalShiftBoundedWithinArea(king, direction, targetSquares);
            if (Bitboard.EMPTY != to) { // Move is possible
                if (Bitboard.intersects(to, unoccupied)) {
                    moves.add(ChessMove.basicMove(king, to, team | Piece.KING));
                } else {
                    moves.add(ChessMove.basicCapture(king, to, team | Piece.KING, board.getPieceAtBit(to)));
                }
            }
        }
    }

    private void slidingPieceMoveInDirection(final long piece, final int pieceType, final int direction) {

        long previousTo = piece;
        long to = Bitboard.directionalShiftBoundedWithinArea(piece, direction, unoccupied);

        while (to != Bitboard.EMPTY) {

            if (Bitboard.intersects(to, movableSquares)) {
                moves.add(ChessMove.basicMove(piece, to, pieceType));
            }

            previousTo = to;
            to = Bitboard.directionalShiftBoundedWithinArea(to, direction, unoccupied);
        }

        to = Bitboard.directionalShiftBoundedWithinArea(previousTo, direction, enemyPieces);
        if (Bitboard.intersects(to, movableSquares)) {
            moves.add(ChessMove.basicCapture(piece, to, pieceType, board.getPieceAtBit(to)));
        }
    }

    private void slidingPieceMoves(final long piece, final int pieceType, final int[] directions) {
        for (int direction : directions) {
            slidingPieceMoveInDirection(piece, pieceType, direction);
        }
    }

    // This is needed to catch a very sneaky illegal en passant move (en passant reveals horizontal check)
    private boolean isIllegalEnPassantState(final long movingPawn, final long capturedPawn) {

        final long cardinalEnemies = board.getPieceBitboard(enemyTeam | Piece.ROOK) | board.getPieceBitboard(enemyTeam | Piece.QUEEN);

        long line = Bitboard.fill(king, Bitboard.EAST, unoccupied | movingPawn | capturedPawn);
        line = Bitboard.fill(line, Bitboard.WEST, unoccupied | movingPawn | capturedPawn);
        line |= Bitboard.directionalShiftBoundedWithinArea(line, Bitboard.EAST, cardinalEnemies);
        line |= Bitboard.directionalShiftBoundedWithinArea(line, Bitboard.WEST, cardinalEnemies);

        return Bitboard.intersects(line, cardinalEnemies);
    }

    // -- Calculating Threats And Pins --

    private void calculateThreatsAndPins() {

        // Pawns
        singleMoveThreatsInMultipleDirections(board.getPieceBitboard(enemyTeam | Piece.PAWN), Piece.getPawnAttackingDirections(!isWhiteTurn));

        // Knights
        singleMoveThreatsInMultipleDirections(board.getPieceBitboard(enemyTeam | Piece.KNIGHT), Bitboard.L_SHAPES);

        // King
        singleMoveThreatsInMultipleDirections(board.getPieceBitboard(enemyTeam | Piece.KING), Bitboard.EVERY_DIRECTION);

        final long enemyQueens = board.getPieceBitboard(enemyTeam | Piece.QUEEN);
        final long enemyDiagonals =  board.getPieceBitboard(enemyTeam | Piece.BISHOP) | enemyQueens;
        final long enemyCardinals = board.getPieceBitboard(enemyTeam | Piece.ROOK) | enemyQueens;

        slidingMoveThreatsInMultipleDirections(enemyDiagonals, Bitboard.DIAGONAL);
        slidingMoveThreatsInMultipleDirections(enemyCardinals, Bitboard.CARDINAL);

        calculatePins(enemyCardinals, enemyDiagonals);
    }

    private void calculatePins(final long cardinals, final long diagonals) {
        calculatePinInAxis(cardinals, Bitboard.HORIZONTAL, HORIZONTAL);
        calculatePinInAxis(cardinals, Bitboard.VERTICAL, VERTICAL);

        calculatePinInAxis(diagonals, Bitboard.DIAGONAL_POSITIVE, DIAGONAL_POSITIVE);
        calculatePinInAxis(diagonals, Bitboard.DIAGONAL_NEGATIVE, DIAGONAL_NEGATIVE);
    }

    private void calculatePinInAxis(final long enemies, final int[] directions, final int pinIndex) {
        for (int direction : directions) {
            calculatePin(enemies, direction, pinIndex);
        }
    }

    private void calculatePin(final long enemies, final int direction, final int pinIndex) {

        long line = Bitboard.fill(king, direction, unoccupied);  // Fill from the king in a given direction in the unoccupied spaces
        line = Bitboard.directionalShiftBounded(line, direction); // Move one again (regardless of occupation) in the same direction - removes the king space from the line

        if (Bitboard.intersects(line, enemies)) { // We have found a check ray
            addCheckRay(line);
            return;
        }

        if (Bitboard.intersects(line, pieces)) { // We now have a line originating from the king, and ending at a defending piece (possible pin)

            line = Bitboard.fill(line, direction, unoccupied);  // Fill again unoccupied squares
            line |= Bitboard.directionalShiftBounded(line, direction); // Move one again (regardless of occupation) in the same direction

            if (Bitboard.intersects(enemies, line)) { // Found a pin
                pins[pinIndex] |= line;
                allPin |= line;
            }
        }
    }

    private void addCheckRay(final long checkRay) {
        if (isCheck) {
            isDoubleCheck = true;
        } else {
            this.movableSquares = checkRay;
            isCheck = true;
        }
    }

    private void slidingMoveThreatsInMultipleDirections(final long pieces, final int[] directions) {
        for (int direction : directions) {
            slidingMoveThreat(pieces, direction);
        }
    }

    private void slidingMoveThreat(final long pieces, final int direction) {
        threatened |= Bitboard.directionalShiftBounded(Bitboard.fill(pieces, direction, unoccupied | king), direction);
    }

    private void singleMoveThreatsInMultipleDirections(final long pieces, final int[] directions) {
        for (int direction : directions) {
            singleMoveThreat(pieces, direction);
        }
    }

    private void singleMoveThreat(final long pieces, final int direction) {
        final long threats = Bitboard.directionalShiftBounded(pieces, direction);
        if (Bitboard.intersects(king, threats)) {
            // When calculating threats, we found the king...
            isCheck = true;
            movableSquares = Bitboard.oppositeDirectionalShift(king, direction);
        }
        threatened |= threats;
    }

}
