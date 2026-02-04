package dev.pig.stockpig.gui;

import dev.pig.stockpig.chess.bitboard.Bitboard;
import dev.pig.stockpig.chess.bitboard.Square;

/**
 * Controller for the stockpig GUI.
 * All event callbacks are registered by the view on startup.
 */
final class StockpigController {

    private final StockpigView view;
    private final StockpigModel model;

    StockpigController(final StockpigView view) {
        this.view = view;
        this.model = new StockpigModel();
        this.view.addController(this);
        redraw();
    }

    /**
     * Redraw everything.
     */
    void redraw() {
        this.view.drawPieces(this.model.pieces());

        this.view.clearColours();
        this.view.selectSquares(this.model.previousFrom());
        this.view.selectSquares(this.model.previousTo());
        this.view.tintSquares(this.model.destinations());
        this.view.selectSquares(this.model.selected());

        this.view.setFen(this.model.fen());
    }


    // ====================================================================================================
    //                                  Event Callbacks
    // ====================================================================================================

    /**
     * Event callback for starting a new game
     */
    void newGame() {
        this.model.newGame();
        redraw();
    }

    /**
     * Event callback for undoing the last move
     */
    void undo() {
        this.model.undo();
        redraw();
    }

    /**
     * Event call back for submitting a new FEN string.
     * @param fen FEN string
     */
    void setFen(final String fen) {
        this.model.loadFen(fen);
        redraw();
    }

    /**
     * Event callback for clicking a board square at given index.
     * @param i index
     */
    void onSquarePressed(final int i) {
        if (this.bitboardMode) {
            setBit(i);
            return;
        }

         if (this.model.select(i, this.view)) {
             MoveSoundPlayer.play();
         }
         redraw();
    }


    // ====================================================================================================
    //                                  Bitboard Editor Mode
    // ====================================================================================================

    // This is horrible - if another mode appears, abstract this into a mode interface

    private boolean bitboardMode = false;
    private long bitboard = 0L;

    /**
     * Set whether the controller is in bitboard editor mode.
     * @param isBitboardMode is bitboard editor mode
     */
    void setBitboardEditorMode(final boolean isBitboardMode) {
        if (this.bitboardMode == isBitboardMode) return; // Didn't change
        this.bitboardMode = isBitboardMode;

        // Switched back to play mode
        if (!this.bitboardMode) {
            redraw();
            return;
        }

        // Switch to bitboard mode
        drawBits();
    }

    /**
     * Set or add the bitboard value to the bitboard in bitboard editor mode.
     * @param isSet whether to set the bitboard to the value, if not then add it
     * @param label the bitboard value type
     */
    void addOrSetBitboard(final boolean isSet, final String label) {
        final long bitboard = switch (label)
        {
            case "Attacked"   -> this.model.attacked();
            case "Target"     -> this.model.target();
            case "Pinned"     -> this.model.pinned();
            case "Pins"       -> this.model.pins();
            case "Checkers"   -> this.model.checkers();
            case "Checks"     -> this.model.checks();
            case "Unoccupied" -> this.model.unoccupied();
            case "Occupied"   -> this.model.occupied();
            default           -> Bitboard.EMPTY;
        };
        this.bitboard = isSet ? bitboard : this.bitboard | bitboard;
        drawBits();
    }

    /**
     * Reset the bitboard to the empty bitboard.
     */
    void resetBitboard() {
        this.bitboard = Bitboard.EMPTY;
        drawBits();
    }

    /**
     * Toggle the bit at square index.
     * @param i square index
     */
    void setBit(final int i) {
        this.bitboard ^= Square.of(i).bitboard();
        drawBits();
    }

    /**
     * Highlight and set the bitboard panel.
     */
    void drawBits() {
        this.view.setBitboard(this.bitboard);
        this.view.clearColours();
        this.view.highlightSquares(Bitboard.toSquareIndexList(this.bitboard));
    }
}
