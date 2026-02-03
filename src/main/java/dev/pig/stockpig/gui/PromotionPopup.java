package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.PieceType;

import javax.swing.*;

/**
 * Popup dialog box for selecting which piece to promote to.
 */
final class PromotionPopup {

    private static final String[] OPTIONS = new String[]{"Queen", "Knight", "Rook", "Bishop"};

    /**
     * Open an option dialog box and get which piece to promote to.
     * @param view view
     * @return piece to promote
     */
    static PieceType getPromoteToPiece(final StockpigView view) {
        final int option = JOptionPane.showOptionDialog(view,
                "Promote to:", "Promotion",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null,
                OPTIONS, OPTIONS[0]
        );
        return switch (option)
        {
            case 0 -> PieceType.QUEEN;
            case 1 -> PieceType.KNIGHT;
            case 2 -> PieceType.ROOK;
            case 3 -> PieceType.BISHOP;
            default -> PieceType.EMPTY;
        };
    }
}
