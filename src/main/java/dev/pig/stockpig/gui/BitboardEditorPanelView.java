package dev.pig.stockpig.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for the bitboard editor panel.
 */
final class BitboardEditorPanelView extends JPanel {

    private static final String[] BITBOARD_SELECTORS = new String[]{
            "Attacked",
            "Target",
            "Pins",
            "Pinned",
            "Checks",
            "Checkers",
            "Unoccupied",
            "Occupied"
    };


    private final JTextField hex = new JTextField("0x0");
    private final JButton reset = new JButton("Reset");
    private final JButton[] bitboardSetters = new JButton[BITBOARD_SELECTORS.length];
    private final JButton[] bitboardAdders = new JButton[BITBOARD_SELECTORS.length];

    BitboardEditorPanelView() {
        super(new GridLayout(10, 1, 0, 15));

        this.hex.setBackground(Look.BACKGROUND_COLOUR);
        this.hex.setEditable(false);
        // Clicking the field selects it all
        this.hex.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent e) {
                hex.selectAll();
            }
        });
        add(this.hex);

        add(this.reset);

        for (int i = 0; i < BITBOARD_SELECTORS.length; i++) {
            add(panel(i));
        }
    }

    /**
     * Register bitboard selectors with the controller.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
        this.reset.addActionListener(e -> controller.resetBitboard());

        for (int i = 0; i < BITBOARD_SELECTORS.length; i++) {
            final int index = i;
            this.bitboardSetters[i].addActionListener(e ->
                    controller.addOrSetBitboard(true, BITBOARD_SELECTORS[index])
            );
            this.bitboardAdders[i].addActionListener(e ->
                    controller.addOrSetBitboard(false, BITBOARD_SELECTORS[index])
            );
        }
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

    // ====================================================================================================
    //                                  Helpers
    // ====================================================================================================

    /**
     * Helper for creating a bitboard selector panel.
     * @param i selector index
     * @return panel
     */
    private JPanel panel(final int i) {
        final JPanel panel = new JPanel(new GridLayout(0, 3, 15, 0));
        panel.add(new JLabel( BITBOARD_SELECTORS[i] + ": "));

        final JButton setButton = new JButton("Set");
        this.bitboardSetters[i] = setButton;

        final JButton addButton = new JButton("Add");
        this.bitboardAdders[i] = addButton;

        panel.add(setButton);
        panel.add(addButton);
        return panel;
    }
}
