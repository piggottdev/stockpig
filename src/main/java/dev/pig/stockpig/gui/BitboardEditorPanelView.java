package dev.pig.stockpig.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for the bitboard editor panel.
 */
final class BitboardEditorPanelView extends JPanel {

    private final JTextField hex = new JTextField("0x0");

    BitboardEditorPanelView() {
        super(new BorderLayout(3, 3));
        this.hex.setEditable(false);
        this.hex.setBorder(null);
        add(this.hex);
        // Clicking the field selects it all
        this.hex.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent e) {
                hex.selectAll();
            }
        });
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Update the text field with the hex of the selected bitboard.
     * @param bitboard bitboard
     */
    void setBitboard(final long bitboard) {
        this.hex.setText(String.format("0x%016X", bitboard));
    }
}
