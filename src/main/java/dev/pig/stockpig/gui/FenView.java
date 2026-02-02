package dev.pig.stockpig.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for the FEN toolbar.
 */
class FenView extends JToolBar {

    private final JTextField fen = new JTextField();

    FenView() {
        super();
        setFloatable(false);

        final JLabel label =  new JLabel("FEN: ");
        label.setBorder(new EmptyBorder(4,4,4,4));
        add(label);

        // Clicking the field selects it all
        this.fen.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent e) {
                fen.selectAll();
            }
        });
        add(this.fen);
    }

    /**
     * Register submitting fen string with controller.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
        this.fen.addActionListener(e -> controller.setFen(fen.getText()));
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Set the fen string for the game details panel.
     * @param fen FEN string
     */
    void setFen(final String fen) {
        this.fen.setText(fen);
    }
}
