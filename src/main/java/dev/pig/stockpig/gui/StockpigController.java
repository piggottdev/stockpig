package dev.pig.stockpig.gui;

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
         this.model.select(i);
         redraw();
    }
}
