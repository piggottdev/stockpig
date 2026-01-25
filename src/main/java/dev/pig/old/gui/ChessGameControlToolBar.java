package dev.pig.old.gui;

import javax.swing.*;

public class ChessGameControlToolBar extends JToolBar {

    public final JButton newGame = new JButton("New");
    public final JButton undo = new JButton("Undo");
    public final JLabel turn = new JLabel();

    public final JLabel debug = new JLabel("Debug: ");
    public final ButtonGroup debugButtonGroup = new ButtonGroup();
    public final JRadioButton none = new JRadioButton("None");
    public final JRadioButton threatened = new JRadioButton("Threatened");
    public final JRadioButton movable = new JRadioButton("Movable");
    public final JRadioButton pins = new JRadioButton("Pins");

    public ChessGameControlToolBar() {
        init();
    }

    private void init() {
        setFloatable(false);
        add(this.newGame);
        addSeparator();
        add(this.undo);
        addSeparator();
        add(this.turn);
        addSeparator();
        addSeparator();
        add(this.debug);
        initDebugButtons();
    }

    private void initDebugButtons() {
        this.debugButtonGroup.add(this.none);
        this.debugButtonGroup.add(this.threatened);
        this.debugButtonGroup.add(this.movable);
        this.debugButtonGroup.add(this.pins);
        add(this.none);
        add(this.threatened);
        add(this.movable);
        add(this.pins);
        this.none.setSelected(true);
    }

}
