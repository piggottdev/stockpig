package dev.pig.stockpig.gui.view.side;

import dev.pig.stockpig.gui.controller.StockpigController;

import javax.swing.*;
import java.awt.*;

/**
 * View for the game info and buttons panel.
 */
public final class GamePanelView extends JPanel {

    private final JButton newGame = new JButton("New Game");
    private final JButton undo    = new JButton("Undo");

    public GamePanelView() {
        super(new GridLayout(8, 1));

        // Put things in panels to space and keep in line
        final JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15,0));
        buttonPanel.add(this.newGame);
        buttonPanel.add(this.undo);
        add(buttonPanel);
    }

    /**
     * Register game buttons with the controller.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        this.newGame.addActionListener(e -> controller.newGamePressed());
        this.undo.addActionListener(e -> controller.undoPressed());
    }
}
