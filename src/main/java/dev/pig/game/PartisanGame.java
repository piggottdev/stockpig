package dev.pig.game;

/**
 * Partisan games are a subset of combinatorial games where two competing players take turns
 * sequentially, the moves available are unique to each player.
 * We assume when solving the game that player one is trying to maximise the evaluation of
 * the position and player two is trying to minimise.
 *
 * @param <M> move implementation class
 */
public interface PartisanGame<M extends PartisanGame.Move> extends CombinatorialGame<M> {

    /**
     * Is it player ones turn.
     *
     * @return is player ones turn
     */
    boolean isPlayerOneTurn();

    /**
     * Is quiet position.
     *
     * @return is quiet
     */
    default boolean isQuiet() {
        return true;
    }

    interface Move extends CombinatorialGame.Move {

        /**
         * Is quiet move.
         *
         * @return is quiet
         */
        default boolean isQuiet() {
            return true;
        }

    }

}
