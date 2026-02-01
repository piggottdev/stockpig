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

        // Add the view to the frame
        add(view);
        setBackground(Look.BACKGROUND_COLOUR);

        // Size and make frame visible
        setLocationByPlatform(true);
        pack();
        setResizable(false);
        setVisible(true);
    }
}
