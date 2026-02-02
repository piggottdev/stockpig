package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.Move;
import dev.pig.stockpig.chess.MoveList;
import dev.pig.stockpig.chess.Piece;
import dev.pig.stockpig.chess.Position;
import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Square;

import java.util.ArrayList;
import java.util.List;

/**
 * Chess model is an adapter between a chess position and the GUI.
 * Manages interactions on a chess board such as selecting a piece.
 */
final class StockpigModel {

    private Position position         = Position.starting();
    private final MoveList legalMoves = new MoveList();
    private Square selected           = Square.EMPTY;
    private Square previousFrom       = Square.EMPTY;
    private Square previousTo         = Square.EMPTY;


    // ====================================================================================================
    //                                  Helpers
    // ====================================================================================================

    /**
     * Clear the legal moves and selected squares, state that must be reset on most
     * operations.
     */
    void clear() {
        this.legalMoves.clear();
        this.selected = Square.EMPTY;
        this.previousFrom = Square.EMPTY;
        this.previousTo = Square.EMPTY;
    }

    /**
     * Get the square as a square index list, empty list if square is empty.
     * @param sq square
     * @return square index list
     */
    private List<Integer> squareToSquareIndexList(final Square sq) {
        return sq == Square.EMPTY ? new ArrayList<>(2) : new ArrayList<>(List.of(sq.ordinal()));
    }


    // ====================================================================================================
    //                                  Game Operations
    // ====================================================================================================

    /**
     * Start a new game from the starting position.
     */
    void newGame() {
        this.position = Position.starting();
        clear();
    }

    /**
     * Load a position from a fen string.
     * @param fen fen string
     */
    void loadFen(final String fen) {
        this.position = Position.fromFen(fen);
        clear();
    }

    /**
     * Undo the last move.
     */
    void undo() {
        this.position.undo();
        clear();
    }


    // ====================================================================================================
    //                                  Square Selection and Move Making
    // ====================================================================================================

    /**
     * Attempt to select a square. This will select a piece if it is able to move.
     * If a piece is already selected it will attempt to make the move if legal.
     * @param sqi selected square index
     * @return whether a move has been made (pieces must be redrawn)
     */
    boolean select(final int sqi) {
        final Square sq = Square.of(sqi);

        // Piece already selected, attempt to make move
        if (this.selected != Square.EMPTY) {

            for (int i = 0; i < this.legalMoves.size(); i++) {
                final int move = this.legalMoves.get(i);

                if (Move.to(move) == sq) {
                    this.position.makeMove(move);
                    clear();
                    this.previousFrom = Move.from(move);
                    this.previousTo = Move.to(move);
                    return true;
                }
            }
        }

        // Either new select or square selected did not result in legal move,
        // in either case, try and select that new square
        this.selected = Square.EMPTY;
        this.legalMoves.clear();

        for (int i = 0; i < this.position.moves().size(); i++) {
            final int move = this.position.moves().get(i);

            if (Move.from(move) == sq) {
                this.legalMoves.add(move);
            }
        }

        if (!this.legalMoves.isEmpty()) this.selected = sq;
        return false;
    }


    // ====================================================================================================
    //                                  Accessors
    // ====================================================================================================

    /**
     * Get the pieces on the board in square index order. Empty squares will be {@link Piece#EMPTY} within
     * the list.
     * @return pieces in square order
     */
    List<Piece> pieces() {
        final List<Piece> pieces = new ArrayList<>(64);
        for (int i = 0; i < 64; i++) {
            pieces.add(this.position.pieceAt(Square.of(i)));
        }
        return pieces;
    }

    /**
     * Get the currently selected square as a square index list. Empty list if not selected.
     * @return selected square index list
     */
    List<Integer> selected() {
        return squareToSquareIndexList(this.selected);
    }

    /**
     * Get the origin square of the last move as a square index list. Empty list if not selected.
     * @return origin square index list
     */
    List<Integer> previousFrom() {
        return squareToSquareIndexList(this.previousFrom);
    }

    /**
     * Get the destination square of the last move as a square index list. Empty list if not selected.
     * @return destination square index list
     */
    List<Integer> previousTo() {
        return squareToSquareIndexList(this.previousTo);
    }

    /**
     * Get the currently selected piece's possible destination squares as a square index list.
     * @return destinations square index list
     */
    List<Integer> destinations() {
        final List<Integer> squares = new ArrayList<>();
        for (int i = 0; i < this.legalMoves.size(); i++) {
            final int move = this.legalMoves.get(i);
            squares.add(Move.to(move).ordinal());
        }
        return squares;
    }

    /**
     * Get the currently attacked squares as a square index list.
     * @return attacked square index list
     */
    List<Integer> attacked() {
        return Bitboard.toSquareIndexList(this.position.attacked());
    }

    /**
     * Get the target squares for the current team as a square index list.
     * @return targets square index list
     */
    List<Integer> target() {
        return Bitboard.toSquareIndexList(this.position.target());
    }

    /**
     * Get the pin lines as a square index list.
     * @return pins square index list
     */
    List<Integer> pins() {
        return Bitboard.toSquareIndexList(this.position.pins());
    }
}
