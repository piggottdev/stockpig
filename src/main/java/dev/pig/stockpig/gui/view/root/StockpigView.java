package dev.pig.stockpig.gui.view.root;

import dev.pig.stockpig.gui.model.StockpigModel;
import dev.pig.stockpig.gui.view.fen.FenView;
import dev.pig.stockpig.gui.view.side.SidePanelView;
import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.view.board.BoardView;

import javax.swing.*;
import java.awt.*;

/**
 * Top level view of stockpig GUI defining overall layout.
 * Entry point for adding controllers or sub-views.
 */
public final class StockpigView extends JPanel {

    private final BoardView board = new BoardView();
    private final SidePanelView sidePanel = new SidePanelView();
    private final FenView fen = new FenView();

    public StockpigView() {
        super(new BorderLayout(0, 0));
        add(this.board);
        add(this.sidePanel, BorderLayout.LINE_END);
        add(this.fen, BorderLayout.PAGE_END);
    }

    /**
     * Forward controller to sub-views.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        this.board.addController(controller);
        this.sidePanel.addController(controller);
        this.fen.addController(controller);
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Forward the model to sub-views to redraw themselves.
     * @param model model
     */
    public void redraw(final StockpigModel model) {
        this.board.redraw(model);
        this.sidePanel.redraw(model);
        this.fen.redraw(model);
    }
}
