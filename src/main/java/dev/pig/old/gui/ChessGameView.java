package dev.pig.old.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ChessGameView extends JPanel {

    public final ChessGameControlToolBar toolBar = new ChessGameControlToolBar();
    public final ChessBoardView board = new ChessBoardView();
    public final ChessFenView fen = new ChessFenView();
    public final ChessAiView ai = new ChessAiView();

    public ChessGameView() {
        super(new BorderLayout(3, 3));
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        add(this.toolBar, BorderLayout.PAGE_START);
        add(this.board);
        add(this.fen, BorderLayout.PAGE_END);
        add(this.ai, BorderLayout.LINE_END);
    }
}
