package dev.pig.stockpig.gui.view.side;

import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.model.BitboardEditorModel;
import dev.pig.stockpig.gui.model.StockpigModel;
import dev.pig.stockpig.gui.style.Look;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for the bitboard editor panel.
 */
public final class BitboardEditorPanelView extends JPanel {

    private final JTextField hex = new JTextField("0x0");
    private final JButton reset = new JButton("Reset");
    private final JButton[] bitboardSetters = new JButton[BitboardEditorModel.BitboardSelector.values().length];
    private final JButton[] bitboardAdders = new JButton[BitboardEditorModel.BitboardSelector.values().length];

    public BitboardEditorPanelView() {
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
        for (final BitboardEditorModel.BitboardSelector selector: BitboardEditorModel.BitboardSelector.values()) {
            final JPanel panel = new JPanel(new GridLayout(0, 3, 15, 0));
            panel.add(new JLabel(selector.toString() + ": "));

            final JButton setButton = new JButton("Set");
            this.bitboardSetters[selector.ordinal()] = setButton;

            final JButton addButton = new JButton("Add");
            this.bitboardAdders[selector.ordinal()] = addButton;

            panel.add(setButton);
            panel.add(addButton);
            add(panel);
        }
    }

    /**
     * Register bitboard selectors with the controller.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        this.reset.addActionListener(e -> controller.resetBitboard());

        for (final BitboardEditorModel.BitboardSelector selector: BitboardEditorModel.BitboardSelector.values()) {
            this.bitboardSetters[selector.ordinal()].addActionListener(e -> controller.setBitboard(selector));
            this.bitboardAdders[selector.ordinal()].addActionListener(e -> controller.addBitboard(selector));
        }
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Redraw the bitboard hex string from the model.
     * @param model model
     */
    public void redraw(final StockpigModel model) {
        this.hex.setText(String.format("0x%016X", model.bitboardEditor.bitboard()));
    }
}
