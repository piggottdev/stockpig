package dev.pig.stockpig.gui.view.side;

import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.model.StockpigModel;

import javax.swing.*;
import java.awt.*;

/**
 * View for the side panel.
 */
public final class SidePanelView extends JTabbedPane {

    private final GamePanelView gamePanel = new GamePanelView();
    private final BitboardEditorPanelView bitboardEditor = new BitboardEditorPanelView();

    public SidePanelView() {
        super();
        setPreferredSize(new Dimension(330, 0));
        add("Game Details", this.gamePanel);
        add("Bitboard Editor", this.bitboardEditor);
    }

    /**
     * Registers tab selection with the controller and forward controller to tab views.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        addChangeListener(e -> controller.setBitboardEditorMode(getSelectedIndex() == 1));
        this.gamePanel.addController(controller);
        this.bitboardEditor.addController(controller);
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Forward the model to tabs to redraw themselves.
     * @param model model
     */
    public void redraw(final StockpigModel model) {
        this.gamePanel.redraw(model);
        this.bitboardEditor.redraw(model);
    }
}
