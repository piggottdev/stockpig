package dev.pig.gui;

import javax.swing.*;

public class ChessFenView extends JToolBar {

    public final JLabel label = new JLabel("FEN: ");
    public final JTextField fen = new JTextField("");

    public ChessFenView() {
        init();
    }

    private void init() {
        setFloatable(false);
        add(this.label);
        add(this.fen);
    }

}
