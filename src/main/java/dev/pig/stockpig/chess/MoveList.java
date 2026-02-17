package dev.pig.stockpig.chess;

import java.util.function.IntConsumer;

/**
 * A MoveList is a lightweight wrapper around an integer array that provides
 * list-like functionality without boxing. Instances can be reused via {@link #clear()}
 * and cloned when necessary.
 */
public final class MoveList {

    private static final int MAX_MOVES = 256;


    private final int[] moves = new int[MAX_MOVES];
    private int size = 0;

    /**
     * Adds a move to the list.
     * @param move move
     */
    public void add(final int move) {
        this.moves[this.size++] = move;
    }

    /**
     * Returns the move at the given index.
     * @param i index
     * @return move
     */
    public int get(final int i) {
        return this.moves[i];
    }

    /**
     * Returns the number of moves in the list.
     * @return size
     */
    public int size() {
        return this.size;
    }

    /**
     * Returns whether the list is empty.
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
     * Creates a copy of this move list.
     * @return clone
     */
    @Override
    public MoveList clone() {
        final MoveList clone = new MoveList();
        cloneInto(clone);
        return clone;
    }

    /**
     * Copies the contents of this move list into the provided list.
     * @param clone move list to clone into
     */
    public void cloneInto(final MoveList clone) {
        clone.size = this.size;
        System.arraycopy(this.moves, 0, clone.moves, 0, this.size);
    }
}
