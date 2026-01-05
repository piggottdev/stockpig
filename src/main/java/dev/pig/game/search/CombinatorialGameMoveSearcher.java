package dev.pig.game.search;

import dev.pig.game.CombinatorialGame;

/**
 * Interface of a function that searches a combinatorial game for a move.
 *
 * @param <G> game type
 * @param <M> move type
 */
@FunctionalInterface
public interface CombinatorialGameMoveSearcher<G extends CombinatorialGame<M>, M extends CombinatorialGame.Move> extends Searcher<G, M> {

    /**
     * Search the game for a move.
     *
     * @param game game
     * @return move
     */
    @Override
    M search(final G game);

}
