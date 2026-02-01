package dev.pig.stockpig.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Look holds all the static look/theme variables.
 * Most colours and visuals can be modified in this file.
 */
public final class Look {

    public static final Color BACKGROUND_COLOUR               = new Color(0x252526);
    public static final Color HIGHLIGHTED_BACKGROUND_COLOUR   = new Color(90, 90, 90);
    public static final Color TEXT_COLOUR                     = new Color(0xD4D4D4);
    public static final Color WHITE_SQUARE_COLOUR             = new Color(205, 180, 174);
    public static final Color BLACK_SQUARE_COLOUR             = new Color(140, 93, 56);
    public static final Color TINTED_WHITE_SQUARE_COLOUR      = new Color(90, 90, 90);
    public static final Color TINTED_BLACK_SQUARE_COLOUR      = new Color(70, 70, 70);
    public static final Color SELECTED_SQUARE_COLOUR          = new Color(140, 150, 0);
    public static final Color HIGHLIGHTED_SQUARE_COLOUR       = Color.RED.brighter();

    public static final ImageIcon BLANK_ICON = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    public static final ImageIcon[] ICONS = loadPieceIcons();

    public static final Image STOCKPIG_ICON = loadImage("ico.png");

    /**
     * Initialise UI manager constants for all components to avoid doing it in each
     * component.
     */
    static void init() {
        final Color transparent = new Color(0,0,0,0);

        UIManager.put("Panel.foreground", Look.TEXT_COLOUR);
        UIManager.put("Panel.background", Look.BACKGROUND_COLOUR);

        UIManager.put("Label.foreground", Look.TEXT_COLOUR);
        UIManager.put("Label.background", Look.BACKGROUND_COLOUR);

        UIManager.put("TabbedPane.foreground", Look.TEXT_COLOUR);
        UIManager.put("TabbedPane.selected", Look.HIGHLIGHTED_BACKGROUND_COLOUR);
        UIManager.put("TabbedPane.background", Look.BACKGROUND_COLOUR);

        UIManager.put("TabbedPane.contentAreaColor", transparent);
        UIManager.put("TabbedPane.borderHightlightColor", transparent);
        UIManager.put("TabbedPane.light", transparent);
        UIManager.put("TabbedPane.highlight", transparent);
        UIManager.put("TabbedPane.shadow", transparent);
        UIManager.put("TabbedPane.darkShadow", transparent);
        UIManager.put("TabbedPane.focus", transparent);

        UIManager.put("Button.focus", transparent);
    }

    /**
     * Load all piece icons.
     * @return piece icons
     */
    private static ImageIcon[] loadPieceIcons() {
        final ImageIcon[] icons = new ImageIcon[13];
        icons[0] =  BLANK_ICON;
        icons[1] =  loadIcon("wKing.svg.png");
        icons[2] =  loadIcon("wPawn.svg.png");
        icons[3] =  loadIcon("wKnight.svg.png");
        icons[4] =  loadIcon("wBishop.svg.png");
        icons[5] =  loadIcon("wRook.svg.png");
        icons[6] =  loadIcon("wQueen.svg.png");
        icons[7] =  loadIcon("bKing.svg.png");
        icons[8] =  loadIcon("bPawn.svg.png");
        icons[9] =  loadIcon("bKnight.svg.png");
        icons[10] = loadIcon("bBishop.svg.png");
        icons[11] = loadIcon("bRook.svg.png");
        icons[12] = loadIcon("bQueen.svg.png");
        return icons;
    }

    /**
     * Load a 96x96 image icon from a resource path.
     * @param path resource path
     * @return image icon
     */
    private static ImageIcon loadIcon(final String path) {
        final ImageIcon icon = new ImageIcon(loadImage(path));
        return new ImageIcon(icon.getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
    }

    /**
     * Load an image from a resource path
     * @param path resource path
     * @return image
     */
    private static Image loadImage(final String path) {
        try {
            return ImageIO.read(Look.class.getClassLoader().getResourceAsStream(path));
        } catch (final IOException ioException) {
            throw new RuntimeException("Error loading image " + path, ioException);
        }
    }


    private Look() {}
}
