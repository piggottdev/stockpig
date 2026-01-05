package dev.pig.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class Look {

    public static final Color WHITE_SQUARE_COLOR = new Color(255, 206, 164);
    public static final Color BLACK_SQUARE_COLOR = new Color(190, 113, 46);

    public static final Color SELECTED_SQUARE_COLOR = Color.LIGHT_GRAY;

    public static final Color MOVABLE_SQUARE_COLOR = Color.LIGHT_GRAY;
    public static final Color MOVABLE_SQUARE_BORDER_COLOR = new Color(100, 100, 100);

    public static final Color CHECK_SQUARE_COLOR = new Color(250, 100, 100);

    public static final Color DEBUG_SQUARE_COLOR = new Color(250, 100, 100);

    public static final ImageIcon BLANK_ICON = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));

    public static final Color BOARD_BORDER_COLOR = Color.BLACK;

    public static final ImageIcon[] WHITE_ICONS = loadWhiteIcons();
    public static final ImageIcon[] BLACK_ICONS = loadBlackIcons();

    private Look() {}

    private static ImageIcon[] loadWhiteIcons() {
        final ImageIcon[] icons = new ImageIcon[6];
        icons[0] = loadIcon("wKing.svg.png");
        icons[1] = loadIcon("wPawn.svg.png");
        icons[2] = loadIcon("wKnight.svg.png");
        icons[3] = loadIcon("wBishop.svg.png");
        icons[4] = loadIcon("wRook.svg.png");
        icons[5] = loadIcon("wQueen.svg.png");

        return icons;
    }

    private static ImageIcon[] loadBlackIcons() {
        final ImageIcon[] icons = new ImageIcon[6];
        icons[0] = loadIcon("bKing.svg.png");
        icons[1] = loadIcon("bPawn.svg.png");
        icons[2] = loadIcon("bKnight.svg.png");
        icons[3] = loadIcon("bBishop.svg.png");
        icons[4] = loadIcon("bRook.svg.png");
        icons[5] = loadIcon("bQueen.svg.png");
        return icons;
    }
    
    private static ImageIcon loadIcon(final String path) {
        try {
            final ImageIcon icon = new ImageIcon(ImageIO.read(Look.class.getClassLoader().getResourceAsStream(path)));
            return new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        } catch (final IOException ioException) {
            throw new RuntimeException("Error loading image icon " + path, ioException);
        }
    }
}
