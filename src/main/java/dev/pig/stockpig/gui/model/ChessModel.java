package dev.pig.stockpig.gui.model;

import dev.pig.stockpig.chess.*;
import dev.pig.stockpig.chess.bitboard.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Chess model for storing and interacting with a chess position from the GUI.
 */
public final class ChessModel {

    private Position position = Position.starting();

    private final MoveList legalMoves = new MoveList();
    private Square selected = Square.EMPTY;
    private Square from     = Square.EMPTY;
    private Square to       = Square.EMPTY;
    private int promotionMove;

    /**
     * Get the position.
     * @return position
     */
    public Position position() {
        return this.position;
    }

    /**
     * Clear temporary state, called after most game operations.
     */
    public void clear() {
        this.legalMoves.clear();
        this.selected = Square.EMPTY;
        this.from = Square.EMPTY;
        this.to = Square.EMPTY;
    }


    // ====================================================================================================
    //                                  Game Operations
    // ====================================================================================================

    /**
     * Start a new game from the starting position.
     */
    public void startingPosition() {
        this.position = Position.starting();
        clear();
    }

    /**
     * Load the position from the FEN string.
     * @param fen FEN string
     */
    public void fromFen(final String fen) {
        this.position = Position.fromFen(fen);
        clear();
    }

    /**
     * Undo the last move.
     */
    public void undo() {
        this.position.undo();
        clear();
    }

    /**
     * Get the current position's FEN string.
     * @return FEN string
     */
    public String fen() {
        return this.position.toFen();
    }

    /**
     * Attempt to select the square. If a square is already selected, attempt to make the move.
     * If a move is not made, select the square.
     * @param si square index
     * @return type of game event triggered by the selection
     */
    public GameEvent selectSquare(final int si) {
        final Square sq = Square.of(si);

        if (this.selected != Square.EMPTY) {

            for (int i = 0; i < this.legalMoves.size(); i++) {
                final int move = this.legalMoves.get(i);

                if (Move.to(move) != sq) {
                    continue;
                }

                if (Move.isPromotion(move)) {
                    this.promotionMove = move;
                    return GameEvent.PROMOTION;
                }

                this.position.makeMove(move);
                this.legalMoves.clear();
                this.selected = Square.EMPTY;
                this.from = Move.from(move);
                this.to = Move.to(move);
                return GameEvent.MOVE;
            }
        }

        this.selected = Square.EMPTY;
        this.legalMoves.clear();

        for (int i = 0; i < this.position.moves().size(); i++) {
            final int move = this.position.moves().get(i);
            if (Move.from(move) == sq) this.legalMoves.add(move);
        }

        if (!this.legalMoves.isEmpty()) this.selected = sq;
        return GameEvent.SELECTION_CHANGE;
    }

    /**
     * Different events triggered from selecting a square.
     */
    public enum GameEvent {
        SELECTION_CHANGE,
        MOVE,
        PROMOTION
    }

    /**
     * Finish the promotion move once a piece type has been selected.
     * @param pt promotion piece type
     */
    public void finishPromotion(final PieceType pt) {
        final int move = Move.overwritePromotion(this.promotionMove, pt);
        this.position.makeMove(move);
        this.legalMoves.clear();
        this.selected = Square.EMPTY;
        this.from = Move.from(move);
        this.to = Move.to(move);
    }


    // ====================================================================================================
    //                                  Square / Piece Accessors
    // ====================================================================================================

    /**
     * Get the piece at the square index.
     * @param i square index
     * @return piece
     */
    public Piece pieceAt(final int i) {
        return this.position.pieceAt(Square.of(i));
    }

    /**
     * Get the currently selected square.
     * @return selected square
     */
    public Square selected() {
        return this.selected;
    }

    /**
     * Get the previous moves from square.
     * @return from square
     */
    public Square from() {
        return this.from;
    }

    /**
     * Get the previous moves to square.
     * @return to square
     */
    public Square to() {
        return this.to;
    }

    /**
     * Get the destination squares of all legal moves from the selected square.
     * @return destination square indexes
     */
    public List<Integer> destinations() {
        final List<Integer> destinations = new ArrayList<>(this.legalMoves.size());
        this.legalMoves.forEach(move -> destinations.add(Move.to(move).ordinal()));
        return destinations;
    }
}
