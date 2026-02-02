package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Top level view of stockpig GUI defining overall layout.
 * Entry point for adding controllers or sub-views.
 */
final class StockpigView extends JPanel {

    private final BoardView board = new BoardView();
    private final SidePanelView sidePanel = new SidePanelView();
    private final FenView fen = new FenView();

    StockpigView() {
        super(new BorderLayout(3, 3));
        add(this.board);
        add(this.sidePanel, BorderLayout.LINE_END);
        add(this.fen, BorderLayout.PAGE_END);
    }

    /**
     * Registers any controller callbacks and forwards to child views.
     * @param controller controller
     */
    void addController(final StockpigController controller) {
        this.board.addController(controller);
        this.sidePanel.addController(controller);
        this.fen.addController(controller);
    }


    // ====================================================================================================
    //                                  Draw Functions
    // ====================================================================================================

    /**
     * Reset and draw all pieces in the list, list should have length of 64,
     * empty squares should use {@link Piece#EMPTY}.
     * @param pieces pieces
     */
    void drawPieces(final List<Piece> pieces) {
        this.board.drawPieces(pieces);
    }

    /**
     * Reset all squares with extra colouring to their defaults.
     */
    void clearColours() {
        this.board.clearColours();
    }

    /**
     * Mark and colour the squares as tinted, only indexes within the list will be tinted.
     * @param squares squares
     */
    void tintSquares(final List<Integer> squares) {
        this.board.tintSquares(squares);
    }

    /**
     * Mark and colour the squares as selected, only indexes within the list will be selected.
     * @param squares squares
     */
    void selectSquares(final List<Integer> squares) {
        this.board.selectSquares(squares);
    }

    /**
     * Mark and colour the squares as selected, only indexes within the list will be highlighted.
     * @param squares squares
     */
    void highlightSquares(final List<Integer> squares) {
        this.board.highlightSquares(squares);
    }

    /**
     * Set the fen string for the game details panel.
     * @param fen FEN string
     */
    void setFen(final String fen) {
        this.fen.setFen(fen);
    }

    /**
     * Set the bitboard for the bitboard editor panel.
     * @param bitboard bitboard
     */
    void setBitboard(final long bitboard) {
        this.sidePanel.setBitboard(bitboard);
    }
}
