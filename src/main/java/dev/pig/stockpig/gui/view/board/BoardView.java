package dev.pig.stockpig.gui.view.board;

import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Square;
import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.model.StockpigModel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * View for a chess board.
 */
public final class BoardView extends JPanel {

    private final SquareView[] squares = new SquareView[64];

    public BoardView() {
        super(new GridLayout(0, 9));

        for (int rank = 7; rank >= 0; rank--) {
            add(new JLabel(Integer.toString(rank+1), SwingConstants.CENTER));

            for (int file = 0; file < 8; file++) {
                final int square = rank*8+file;
                final SquareView sqview = new SquareView(square);
                this.squares[square] = sqview;
                add(sqview);
            }
        }
        for (int file = 0; file <= 8; file++) {
            add(new JLabel(new String[]{"", "A", "B", "C", "D", "E", "F", "G", "H"}[file], SwingConstants.CENTER));
        }
    }

    /**
     * Forward controller to all square views to register square presses.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        for (final SquareView squareView : this.squares) {
            squareView.addController(controller);
        }
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Redraw the board from the model.
     * @param model model
     */
    public void redraw(final StockpigModel model) {
        clearColours();
        for (int i = 0; i < this.squares.length; i++) {
            this.squares[i].setPiece(model.chess.pieceAt(i));
        }

        if (model.isBitboardMode) {
            Bitboard.forEachSquare(model.bitboardEditor.bitboard(), sq -> this.squares[sq].highlight());
            return;
        }

        doIfNotEmpty(model.chess.from(), SquareView::select);
        doIfNotEmpty(model.chess.to(), SquareView::select);
        doIfNotEmpty(model.chess.selected(), SquareView::select);

        tintSquares(model.chess.destinations());
    }

    /**
     * Helper for colouring a single square any colour.
     * @param sq square
     * @param c square colour method.
     */
    private void doIfNotEmpty(final int sq, final Consumer<SquareView> c) {
        if (sq == Square.EMPTY) return;
        c.accept(this.squares[sq]);
    }

    /**
     * Reset all squares to their default colour.
     */
    private void clearColours() {
        for (final SquareView square : this.squares) {
            square.clearColour();
        }
    }

    /**
     * Tint squares, only indexes within the list will be tinted.
     * @param squares squares
     */
    private void tintSquares(final List<Integer> squares) {
        for (final Integer i : squares) {
            this.squares[i].tint();
        }
    }

    /**
     * Select squares, only indexes within the list will be selected.
     * @param squares squares
     */
    private void selectSquares(final List<Integer> squares) {
        for (final Integer i : squares) {
            this.squares[i].select();
        }
    }

    /**
     * Highlight squares, only indexes within the list will be highlighted.
     * @param squares squares
     */
    private void highlightSquares(final List<Integer> squares) {
        for (final Integer i : squares) {
            this.squares[i].highlight();
        }
    }
}
