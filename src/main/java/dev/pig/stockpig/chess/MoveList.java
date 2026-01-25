package dev.pig.stockpig.chess;

import java.util.function.IntConsumer;

/**
 * Move list is a wrapper around an integer array providing list like
 * functionality without boxing the int. Can be cloned and re-used with clear.
 */
public final class MoveList {

    private static final int MAX_MOVES = 256;

    private final int[] moves = new int[MAX_MOVES];
    private int size = 0;

    /**
     * Add a move to the list.
     * @param move move
     */
    public void add(final int move) {
        this.moves[this.size++] = move;
    }

    /**
     * Get move at index.
     * @param i index
     * @return move
     */
    public int get(final int i) {
        return this.moves[i];
    }

    /**
     * Get the size of the move list.
     * @return size
     */
    public int size() {
        return this.size;
    }

    /**
     * Get whether the list is empty.
     * @return is empty
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Clear the move list.
     */
    public void clear() {
        this.size = 0;
    }

    /**
     * Call the consumer with each move in the list.
     * @param c move consumer
     */
    public void forEach(final IntConsumer c) {
        for (int i = 0; i < this.size; i++)
            c.accept(this.moves[i]);
    }

    /**
     * Clone/copy this move list.
     * @return clone
     */
    @Override
    public MoveList clone() {
        final MoveList clone = new MoveList();
        cloneInto(clone);
        return clone;
    }

    /**
     * Clone/copy this move list into another move list.
     * @param clone move list to clone into
     */
    public void cloneInto(final MoveList clone) {
        clone.size = this.size;
        System.arraycopy(this.moves, 0, clone.moves, 0, this.size);
    }
}
