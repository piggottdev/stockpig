package dev.pig.stockpig.gui.controller;

import dev.pig.stockpig.chess.core.PieceType;
import dev.pig.stockpig.chess.notation.Fen;
import dev.pig.stockpig.gui.model.BitboardEditorModel;
import dev.pig.stockpig.gui.model.ChessModel;
import dev.pig.stockpig.gui.model.StockpigModel;
import dev.pig.stockpig.gui.view.popup.PromotionPopup;
import dev.pig.stockpig.gui.view.root.StockpigView;
import dev.pig.stockpig.gui.sound.MoveSoundPlayer;

/**
 * Controller for the stockpig GUI.
 * All event callbacks are registered by the view on initialisation.
 */
public final class StockpigController {

    private final StockpigView view;
    private final StockpigModel model;

    public StockpigController(final StockpigView view) {
        this.view = view;
        this.model = new StockpigModel();
        this.view.addController(this);
        redraw();
    }

    /**
     * Redraw everything.
     */
    public void redraw() {
        this.view.redraw(this.model);
    }


    // ====================================================================================================
    //                                  Event Callbacks
    // ====================================================================================================

    /**
     * Event callback for pressing new game.
     */
    public void newGamePressed() {
        this.model.chess.startingPosition();
        redraw();
    }

    /**
     * Event callback for submitting a new FEN string.
     * @param fen FEN string
     */
    public void fenSubmitted(final String fen) {
        try {
            this.model.chess.fromFen(fen);
        } catch (final Fen.ParseException ignored) {}
        redraw();
    }

    /**
     * Event callback for pressing undo.
     */
    public void undoPressed() {
        this.model.chess.undo();
        redraw();
    }

    /**
     * Event callback for clicking a board square at given index.
     * @param i index
     */
    public void onSquarePressed(final int i) {
        if (this.model.isBitboardMode) {
            this.model.bitboardEditor.toggleBit(i);
            redraw();
            return;
        }

        final ChessModel.GameEvent event = this.model.chess.selectSquare(i);
        switch (event) {
            case MOVE ->  MoveSoundPlayer.play();
            case PROMOTION -> {
                final byte pt = PromotionPopup.getPromoteToPiece(this.view);
                if (pt != PieceType.EMPTY) {
                    this.model.chess.finishPromotion(pt);
                    MoveSoundPlayer.play();
                }
            }
        }
        redraw();
    }

    /**
     * Event callback for clicking generate an AI move.
     */
    public void aiMove() {
        final ChessModel.GameEvent event = this.model.chess.botMove();
        if (event == ChessModel.GameEvent.MOVE) MoveSoundPlayer.play();
        redraw();
    }

    /**
     * Event callback for clicking add for a bitboard.
     * @param selector bitboard selector
     */
    public void addBitboard(final BitboardEditorModel.BitboardSelector selector) {
        this.model.addBitboard(selector);
        redraw();
    }

    /**
     * Event callback for clicking set for a bitboard.
     * @param selector bitboard selector
     */
    public void setBitboard(final BitboardEditorModel.BitboardSelector selector) {
        this.model.setBitboard(selector);
        redraw();
    }

    /**
     * Event callback for clicking reset bitboard.
     */
    public void resetBitboard() {
        this.model.bitboardEditor.reset();
        redraw();
    }

    /**
     * Event callback for switching between bitboard editor mode.
     * @param bitboardEditorMode is bitboard editor mode
     */
    public void setBitboardEditorMode(final boolean bitboardEditorMode) {
        this.model.isBitboardMode = bitboardEditorMode;
        redraw();
    }
}
