package dev.pig.stockpig.gui;

import javax.swing.*;

/**
 * Top level frame component of the stockpig GUI.
 */
final class StockpigFrame extends JFrame {

    StockpigFrame(final StockpigView view) {
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
