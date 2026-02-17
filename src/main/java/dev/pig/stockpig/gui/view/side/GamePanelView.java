package dev.pig.stockpig.gui.view.side;

import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.model.StockpigModel;

import javax.swing.*;
import java.awt.*;

/**
 * View for the game info and buttons panel.
 */
public final class GamePanelView extends JPanel {

    private final JButton newGame = new JButton("New Game");
    private final JButton undo    = new JButton("Undo");
    private final JLabel  score   = new JLabel("", SwingConstants.CENTER);
    private final JButton aiMove  = new JButton("AI Move");
    private final JLabel  hash    = new JLabel("Hash: ");

    public GamePanelView() {
        super(new GridLayout(8, 1, 0, 15));

        // Put things in panels to space and keep in line
        final JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15,0));
        buttonPanel.add(this.newGame);
        buttonPanel.add(this.undo);
        add(buttonPanel);

        final JPanel scorePanel = new JPanel(new GridLayout(1, 2, 15,0));
        scorePanel.add(this.score);
        scorePanel.add(this.aiMove);
        add(scorePanel);

        final JPanel hashPanel = new JPanel();
        hashPanel.add(this.hash);
        add(hashPanel);
    }

    /**
     * Register game buttons with the controller.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        this.newGame.addActionListener(e -> controller.newGamePressed());
        this.undo.addActionListener(e -> controller.undoPressed());
        this.aiMove.addActionListener(e -> controller.aiMove());
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Redraw the evaluation score from the model.
     * @param model model
     */
    public void redraw(final StockpigModel model) {
        this.score.setText(String.format("%d", model.chess.score()));
        this.hash.setText(String.format("Hash: %d", model.chess.zhash()));
    }
}
