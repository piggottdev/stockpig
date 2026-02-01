package dev.pig.stockpig.gui;

import javax.swing.*;
import java.awt.*;

/**
 * View for the debug side panel.
 */
final class DebugPanelView extends JTabbedPane {

    private final BitboardEditorPanelView bitboardEditor = new BitboardEditorPanelView();

    DebugPanelView() {
        super();
        setPreferredSize(new Dimension(330, 0));
        add("Bitboard Editor", this.bitboardEditor);
    }

    /**
     * Registers any controller callbacks and forwards to tab views.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
    }

}
