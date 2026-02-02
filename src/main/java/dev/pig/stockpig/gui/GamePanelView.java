package dev.pig.stockpig.gui;

import javax.swing.*;
import java.awt.*;

/**
 * View for the game info and side panel.
 */
class GamePanelView extends JPanel {

    private final JButton newGame = new JButton("New Game");
    private final JButton undo    = new JButton("Undo");

    GamePanelView() {
        super(new GridLayout(8, 1));

        // Put things in panels to space and keep in line
        final JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15,0));
        buttonPanel.add(this.newGame);
        buttonPanel.add(this.undo);
        add(buttonPanel);
    }

    /**
     * Register square press with the controller.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
        this.newGame.addActionListener(e -> controller.newGame());
        this.undo.addActionListener(e -> controller.undo());
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

}
