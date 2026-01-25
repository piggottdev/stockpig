package dev.pig.old.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ChessSquareView extends JButton {

    private final Color color;

    public ChessSquareView(final int rank, final int file) {
        this.color = (rank + file) % 2 == 0 ? Look.WHITE_SQUARE_COLOR : Look.BLACK_SQUARE_COLOR;
        init();
    }

    private void init() {
        reset();
    }

    public void reset() {
        setBackground(this.color);
        setIcon(Look.BLANK_ICON);
        setMargin(new Insets(0, 0, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setText("");
    }

    public void select() {
        setBackground(Look.SELECTED_SQUARE_COLOR);
    }

    public void highlightMovable() {
        setBorder(new LineBorder(Look.MOVABLE_SQUARE_BORDER_COLOR, 4));
        setBackground(Look.MOVABLE_SQUARE_COLOR);
    }

    public void check() {
        setBackground(Look.CHECK_SQUARE_COLOR);
    }

    public void debug() {
        setBackground(Look.DEBUG_SQUARE_COLOR);
    }

}
