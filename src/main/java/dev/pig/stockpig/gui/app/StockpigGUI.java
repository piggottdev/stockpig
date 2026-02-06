package dev.pig.stockpig.gui.app;

import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.view.root.StockpigView;
import dev.pig.stockpig.gui.style.Look;

import javax.swing.*;

/**
 * Stockpig general purpose GUI for debugging and playing chess/stockpig.
 */
public final class StockpigGUI {

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            Look.init();
            // Create a view
            final StockpigView stockpigView = new StockpigView();
            // Create a controller
            final StockpigController controller = new StockpigController(stockpigView);
            // Add the view and start a frame
            new StockpigFrame(stockpigView);
        });
    }


    private StockpigGUI() {}
}
