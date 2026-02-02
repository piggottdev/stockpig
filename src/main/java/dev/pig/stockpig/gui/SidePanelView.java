package dev.pig.stockpig.gui;

import javax.swing.*;
import java.awt.*;

/**
 * View for the side panel.
 */
final class SidePanelView extends JTabbedPane {

    private final GamePanelView gamePanel = new GamePanelView();
    private final BitboardEditorPanelView bitboardEditor = new BitboardEditorPanelView();

    SidePanelView() {
        super();
        setPreferredSize(new Dimension(330, 0));
        add("Game Details", this.gamePanel);
        add("Bitboard Editor", this.bitboardEditor);
    }

    /**
     * Registers any controller callbacks and forwards to tab views.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
        this.gamePanel.addController(controller);
        this.bitboardEditor.addController(controller);
        addChangeListener(e -> controller.setBitboardEditorMode(getSelectedIndex() == 1));
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Update bitboard editor with the bitboard.
     * @param bitboard bitboard
     */
    void setBitboard(final long bitboard) {
        this.bitboardEditor.setBitboard(bitboard);
    }
}
