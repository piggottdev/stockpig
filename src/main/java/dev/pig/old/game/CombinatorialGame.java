package dev.pig.old.game;

import java.util.List;

/**
 * A combinatorial game is a game where moves are sequentially made to some
 * position or state until the end of the game.
 * Game trees can be built from combinatorial games where the edges are legal moves
 * and the nodes are positions.
 *
 * @param <M> move implementation class
 */
public interface CombinatorialGame<M extends CombinatorialGame.Move> extends Game {

    /**
     * Is the game over - no more legal moves.
     *
     * @return game over
     */
    boolean isGameOver();

    /**
     * Get list of legal moves.
     *
     * @return legal moves
     */
    List<M> getLegalMoves();

    /**
     * Regenerates legal moves.
     * Use after a move has been undone with {@link #undoMoveWithoutMoveGen()}
     * and the list of legal moves is required.
     */
    void generateLegalMoves();

    /**
     * Apply a move to the game.
     *
     * @param move move
     */
    void applyMove(final M move);

    /**
     * Undo last move.
     */
    void undoMove();

    /**
     * Undo last move.
     * Do not regenerate the list of legal moves after undoing.
     * Good for saving time when searching, where the list of moves is already stored.
     * To return to legal state use {@link #generateLegalMoves()}.
     */
    void undoMoveWithoutMoveGen();

    /**
     * Move interface for the game.
     * A good toString method is beneficial when debugging when dividing during
     * move enumeration.
     */
    interface Move {}

}
