package dev.pig.stockpig.gui.app;

import dev.pig.stockpig.gui.view.root.StockpigView;
import dev.pig.stockpig.gui.style.Look;

import javax.swing.*;

/**
 * Top level frame component of the stockpig GUI.
 */
public final class StockpigFrame extends JFrame {

    public StockpigFrame(final StockpigView view) {
        super("stockpig");

        // On close, exit the app
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setIconImage(Look.STOCKPIG_ICON);
        setBackground(Look.BACKGROUND_COLOUR);

        // Add the view to the frame
        add(view);

        // Size and make frame visible
        setLocationByPlatform(true);
        pack();
        setResizable(false);
        setVisible(true);
    }
}
