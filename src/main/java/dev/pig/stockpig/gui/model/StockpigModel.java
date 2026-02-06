package dev.pig.stockpig.gui.model;

/**
 * Top level model for storing all state to be rendered and interacting with sub-models.
 */
public final class StockpigModel {

    public final ChessModel chess = new ChessModel();
    public final BitboardEditorModel bitboardEditor = new BitboardEditorModel();

    public boolean isBitboardMode = false;

    /**
     * Add the selected bitboard to the bitboard editor.
     * @param selector bitboard selector
     */
    public void addBitboard(final BitboardEditorModel.BitboardSelector selector) {
        this.bitboardEditor.add(selector, this.chess.position());
    }

    /**
     * Set the selected bitboard to the bitboard editor.
     * @param selector bitboard selector
     */
    public void setBitboard(final BitboardEditorModel.BitboardSelector selector) {
        this.bitboardEditor.set(selector, this.chess.position());
    }
}
