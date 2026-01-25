package dev.pig.old.chess;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a game of chess implemented using bitboards.
 * Also stores history so moves can be undone.
 *
 * @see Bitboard
 */
public class ChessGameImpl implements ChessGame {

    private final Board board;
    private boolean isWhiteTurn;
    private int castlesPossible;
    private long enPassantTarget;
    private int turnsSincePushOrCapture;
    private int turnNumber;

    private final List<ChessMove> previousMoves = new ArrayList<>(100);
    private final List<Integer> previousCastlesPossible = new ArrayList<>(100);
    private final List<Long> previousEnPassantTargets = new ArrayList<>(100);
    private final List<Integer> previousTurnSincePushOrCapture = new ArrayList<>(100);

    private List<ChessMove> legalMoves;
    private MoveGenerator moveGenerator;

    ChessGameImpl(final Board board, final boolean isWhiteTurn, final int castlesPossible, final long enPassantTarget, final int turnsSincePushOrCapture, final int turnNumber) {
        this.board = board;
        this.isWhiteTurn = isWhiteTurn;
        this.castlesPossible = castlesPossible;
        this.enPassantTarget = enPassantTarget;
        this.turnsSincePushOrCapture = turnsSincePushOrCapture;
        this.turnNumber = turnNumber;
        generateLegalMoves();
    }

    static ChessGameImpl standard() {
        return new ChessGameImpl(Board.standard(), true, Castling.ALL_ALLOWED, Bitboard.EMPTY, 0, 1);
    }

    // -- Getters --

    /**
     * Is the game over for any reason:
     * <ul>
     *     <li>Checkmate</li>
     *     <li>Stalemate</li>
     *     <li>Dead position</li>
     *     <li>50 move rule</li>
     * </ul>
     *
     * @return whether the game is over
     */
    @Override
    public boolean isGameOver() {
        return this.legalMoves.isEmpty();
    }

    @Override
    public int getWinner() {
        if (!isGameOver()) return 0; // Game isn't over
        if (isCheck()) return this.isWhiteTurn ? -1 : 1; // If check, loser is move maker
        return 0; //Stalemate
    }

    @Override
    public boolean isCheckMate() {
        return (this.legalMoves.isEmpty() && isCheck());
    }

    @Override
    public boolean isCheck() {
        return this.moveGenerator.isCheck();
    }

    @Override
    public boolean isWhiteTurn() {
        return this.isWhiteTurn;
    }

    @Override
    public boolean isDeadPosition() {
        return this.board.isDeadPosition();
    }

    @Override
    public long getPieceBitboard(final int piece) {
        return this.board.getPieceBitboard(piece);
    }

    @Override
    public int getPieceAtBit(final long bitboard) {
        return this.board.getPieceAtBit(bitboard);
    }

    @Override
    public int getPieceAtIndex(final int index) {
        return this.board.getPieceAtIndex(index);
    }

    @Override
    public int getPieceAtAlgebraNotation(final String algebra) {
        return getPieceAtIndex(AlgebraNotation.toIndex(algebra));
    }

    @Override
    public int getTurnNumber() {
        return this.turnNumber;
    }

    @Override
    public int getTurnsSincePushOrCapture() {
        return this.turnsSincePushOrCapture;
    }

    @Override
    public int getCastlesPossible() {
        return this.castlesPossible;
    }

    @Override
    public long getEnPassantTarget() {
        return this.enPassantTarget;
    }

    @Override
    public long getThreatenedSquares() {
        return this.moveGenerator.getThreatened();
    }

    @Override
    public long getMovableSquares() {
        return this.moveGenerator.getMovableSquares();
    }

    @Override
    public long getPinSquares() {
        return this.moveGenerator.getAllPin();
    }

    // -- Move --

    @Override
    public List<ChessMove> getLegalMoves() {
        return this.legalMoves;
    }

    @Override
    public void generateLegalMoves() {
        moveGenerator = new MoveGenerator(board, isWhiteTurn, castlesPossible, enPassantTarget);
        if (turnsSincePushOrCapture > 49 || isDeadPosition()) {
            legalMoves = new ArrayList<>();
        } else {
            legalMoves = moveGenerator.generateLegalMoves();
        }
    }

    @Override
    public void applyMove(final ChessMove move) {
        // Save previous state
        previousMoves.add(move);
        previousCastlesPossible.add(castlesPossible);
        previousEnPassantTargets.add(enPassantTarget);
        previousTurnSincePushOrCapture.add(turnsSincePushOrCapture);

        // Apply move
        board.applyMove(move);
        castlesPossible = Castling.getCastlesAllowedAfterMove(castlesPossible, move, isWhiteTurn);
        isWhiteTurn = !isWhiteTurn;
        enPassantTarget = move.getEnPassantTarget();
        turnsSincePushOrCapture = (move.isPawnMove() || move.isCapture()) ? 0 : turnsSincePushOrCapture + 1;
        if (isWhiteTurn) turnNumber++;

        // Generate possible moves
        generateLegalMoves();
    }

    @Override
    public void undoMove() {
        undoMove(true);
    }

    @Override
    public void undoMoveWithoutMoveGen() {
        undoMove(false);
    }

    private void undoMove(final boolean regenMoves) {
        final int moveNumber = previousMoves.size() - 1;
        if (moveNumber < 0) return;

        // Undo last move
        board.undoMove(previousMoves.get(moveNumber));
        castlesPossible = previousCastlesPossible.get(moveNumber);
        enPassantTarget = previousEnPassantTargets.get(moveNumber);
        turnsSincePushOrCapture = previousTurnSincePushOrCapture.get(moveNumber);
        if (isWhiteTurn) turnNumber--;
        isWhiteTurn = !isWhiteTurn;

        // Remove the un-done move from the history
        previousMoves.remove(moveNumber);
        previousCastlesPossible.remove(moveNumber);
        previousEnPassantTargets.remove(moveNumber);
        previousTurnSincePushOrCapture.remove(moveNumber);

        // Generate possible moves
         if (regenMoves) generateLegalMoves();
    }

    // -- Fen --

    @Override
    public String toFen() {
        return Fen.fromGame(this);
    }

    // -- Debug String --

    public String debugString() {
        String str = board.debugString();

        if (isGameOver()) str = str + "\n\tWinner: " + getWinner();
        if (isCheck()) str = str + "\n\tCheck";
        str = str + (isWhiteTurn ? "\n\tWhite Turn" : "\n\tBlack Turn");
        str = str + "\n\t\tCastles: " + Castling.toString(castlesPossible);
        str = str + "\n\t\tEnPassant Target: " + AlgebraNotation.fromBitboard(enPassantTarget);
        str = str + "\n\t\tTurns Since Push/Cap: " + turnsSincePushOrCapture;
        str = str + "\n\t\tTurn Number: "+ turnNumber;

        return str;
    }

}
