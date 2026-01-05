package dev.pig.gui;

import javax.swing.*;
import java.awt.*;

public class ChessAiView extends JPanel {

    public final JLabel depthLabel = new JLabel("Depth");
    public final JSlider depth = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 4);
    public final JLabel qDepthLabel = new JLabel("Quiescence Depth");
    public final JSlider qDepth = new JSlider(SwingConstants.HORIZONTAL, 0, 10, 10);
    public final JButton generate = new JButton("Generate");

    public ChessAiView() {
        super(new GridLayout(10, 1));
        init();
    }

    private void init() {
        add(this.depthLabel);
        add(this.depth);
        this.depth.setMajorTickSpacing(1);
        this.depth.setPaintTicks(true);
        this.depth.setPaintLabels(true);
        add(this.qDepthLabel);
        add(this.qDepth);
        this.qDepth.setMajorTickSpacing(1);
        this.qDepth.setPaintTicks(true);
        this.qDepth.setPaintLabels(true);
        add(this.generate);
    }

}
