package dev.pig.old.gui;

import javax.swing.*;

public class StockpigGUI {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockpigGUI::run);
    }

    public static void run() {
        final ChessGameView chessGameView = new ChessGameView();
        final ChessGameController controller = new ChessGameController(chessGameView);

        final JFrame f = new JFrame("Stockpig");
        f.add(chessGameView);

        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Set sizing and location
        f.setLocationByPlatform(true);
        f.pack();
        f.setMaximumSize(f.getSize());
        f.setResizable(false);
        f.setVisible(true);
    }
}
