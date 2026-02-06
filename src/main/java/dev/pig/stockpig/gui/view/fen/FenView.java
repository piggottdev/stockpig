package dev.pig.stockpig.gui.view.fen;

import dev.pig.stockpig.gui.controller.StockpigController;
import dev.pig.stockpig.gui.model.StockpigModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * View for the FEN toolbar.
 */
public final class FenView extends JToolBar {

    private final JTextField fen = new JTextField();

    public FenView() {
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
     * Register submitting FEN string with controller.
     * @param controller controller
     */
    public void addController(final StockpigController controller) {
        this.fen.addActionListener(e -> controller.fenSubmitted(this.fen.getText()));
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Redraw the FEN string from the model.
     * @param model model
     */
    public void redraw(final StockpigModel model) {
        this.fen.setText(model.chess.fen());
    }
}
