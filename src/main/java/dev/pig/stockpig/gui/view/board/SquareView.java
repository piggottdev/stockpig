package dev.pig.stockpig.gui.view.board;

import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.style.Look;

import javax.swing.*;

/**
 * View for a chess board square.
 */
public final class SquareView extends JButton {

    private final int index;
    private final boolean isWhiteSquare;

    public SquareView(final int index) {
        super();
        this.index = index;
        this.isWhiteSquare = ((index/8) + (index%8)) % 2 == 0;

        setBorder(null); // Let the icons determine the size
        clearColour();
    }

    /**
     * Register square press with the controller.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        addActionListener(e -> controller.onSquarePressed(this.index));
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Draw the piece icon on the square.
     * @param piece piece
     */
    public void setPiece(final int piece) {
        setIcon(Look.PIECE_ICONS[piece]);
    }

    /**
     * Reset the colour of the square back to the default.
     */
    public void clearColour() {
        setBackground(this.isWhiteSquare ? Look.WHITE_SQUARE_COLOUR : Look.BLACK_SQUARE_COLOUR);
    }

    /**
     * Tint colour the square.
     */
    public void tint() {
        setBackground(this.isWhiteSquare ? Look.TINTED_WHITE_SQUARE_COLOUR : Look.TINTED_BLACK_SQUARE_COLOUR);
    }

    /**
     * Select colour the square.
     */
    public void select() {
        setBackground(Look.SELECTED_SQUARE_COLOUR);
    }

    /**
     * Highlight colour the square.
     */
    public void highlight() {
        setBackground(Look.HIGHLIGHTED_SQUARE_COLOUR);
    }
}
