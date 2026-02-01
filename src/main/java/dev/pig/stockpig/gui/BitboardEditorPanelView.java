package dev.pig.stockpig.gui;

import javax.swing.*;

/**
 * View for the bitboard editor panel.
 */
final class BitboardEditorPanelView extends JPanel {

    private final JLabel label = new JLabel("0x0");

    BitboardEditorPanelView() {
        super();
        add(this.label);
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Update the label with the hex of the selected bitboard.
     * @param bitboard bitboard
     */
    void setBitboard(final long bitboard) {
        this.label.setText(String.format("0x%016X", bitboard));
    }
}
