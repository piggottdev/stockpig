package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.bitboard.Square;

/**
 * Controller for the stockpig GUI.
 * All event callbacks are registered by the view on startup.
 */
final class StockpigController {

    private final StockpigView view;
    private final StockpigModel model;

    StockpigController(final StockpigView view) {
        this.view = view;
        this.model = new StockpigModel();
        this.view.addController(this);
        redraw();
    }

    /**
     * Redraw everything.
     */
    void redraw() {
        this.view.redrawPieces(this.model.pieces());
        this.view.clearColours();
        this.view.tintSquares(this.model.destinations());
        this.view.selectSquares(this.model.selected());
    }


    // ====================================================================================================
    //                                  Event Callbacks
    // ====================================================================================================

    /**
     * Event callback for clicking a board square at given index.
     * @param i index
     */
    void onSquarePressed(final int i) {
        if (this.bitboardMode) {
            setBit(i);
            return;
        }

         this.model.select(i);
         redraw();
    }


    // ====================================================================================================
    //                                  Bitboard Editor Mode
    // ====================================================================================================

    // This is horrible - if another mode appears, abstract this into a mode interface

    private boolean bitboardMode = false;
    private long bitboard;

    /**
     * Set whether the controller is in bitboard editor mode.
     * @param isBitboardMode is bitboard editor mode
     */
    void setBitboardEditorMode(final boolean isBitboardMode) {
        if (this.bitboardMode == isBitboardMode) return; // Didn't change
        this.bitboardMode = isBitboardMode;

        // Switched back to play mode
        if (!this.bitboardMode) {
            redraw();
            return;
        }

        // Switch to bitboard mode
        drawBits();
    }

    /**
     * Toggle the bit at square index.
     * @param i square index
     */
    void setBit(final int i) {
        this.bitboard ^= Square.of(i).bitboard();
        drawBits();
    }

    /**
     * Highlight and set the bitboard panel.
     */
    void drawBits() {
        this.view.setBitboard(this.bitboard);
        this.view.clearColours();
        this.view.highlightSquares(StockpigModel.bitboardToSquareIndexList(this.bitboard));
    }
}
