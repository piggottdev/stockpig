package dev.pig.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ChessBoardView extends JPanel {

    private static final String[] FILE_LABELS = {"", "A", "B", "C", "D", "E", "F", "G", "H"};

    public final ChessSquareView[] squares = new ChessSquareView[64];

    public ChessBoardView() {
        super(new GridLayout(0, 9));
        init();
    }

    private void init() {
        setBorder(new LineBorder(Look.BOARD_BORDER_COLOR));

        // Square buttons

        // For each rank starting at the top (rank 8)
        for (int rank = 7; rank >= 0; rank--) {
            // Add the rank label down the left hand side
            add(new JLabel(String.valueOf(rank + 1), SwingConstants.CENTER));
            // Then for each file
            for (int file = 0; file < 8; file++) {
                // Add a square button for each chess square
                final ChessSquareView square = new ChessSquareView(rank, file);
                add(square);
                this.squares[(rank * 8) + file] = square;
            }
        }
        for (final String fileLabel : FILE_LABELS) {
            add(new JLabel(fileLabel, SwingConstants.CENTER));
        }

    }

}
