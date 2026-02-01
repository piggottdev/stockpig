package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.Piece;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * View for a chess board square.
 * Registers square press event with controller.
 */
final class SquareView extends JButton {

    private final int index;
    private final boolean isWhiteSquare; ;

    SquareView(final int index) {
        this.index = index;
        this.isWhiteSquare = ((index/8) + (index%8)) % 2 == 0;

        setBorder(new EmptyBorder(5, 5, 5, 5));
        setFocusPainted(false);
        clearColour();
    }

    /**
     * Register square press with the controller.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
        addActionListener(e -> controller.onSquarePressed(this.index));
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Set the piece for the square, draws the piece icon on the square, allows {@link Piece#EMPTY}.
     * @param piece piece
     */
    void setPiece(final Piece piece) {
        setIcon(Look.ICONS[piece.ordinal()]);
    }

    /**
     * Reset the colour of the square back to the default.
     */
    void clearColour() {
        setBackground(this.isWhiteSquare ? Look.WHITE_SQUARE_COLOUR : Look.BLACK_SQUARE_COLOUR);
    }

    /**
     * Set the square as tinted, and colour it appropriately.
     */
    void tint() {
        setBackground(this.isWhiteSquare ? Look.TINTED_WHITE_SQUARE_COLOUR : Look.TINTED_BLACK_SQUARE_COLOUR);
    }

    /**
     * Set the square as selected, and colour it appropriately.
     */
    void select() {
        setBackground(Look.SELECTED_SQUARE_COLOUR);
    }

    /**
     * Set the square as highlighted, and colour it appropriately.
     */
    void highlight() {
        setBackground(Look.HIGHLIGHTED_SQUARE_COLOUR);
    }
}
