package dev.pig.game.search;

/**
 * Search an object for a target object.
 *
 * @param <S> searchable type
 * @param <T> target type
 */
@FunctionalInterface
public interface Searcher<S, T> {

    /**
     * Search the searchable object for the target.
     *
     * @param searchable searchable object
     * @return target
     */
    T search(final S searchable);
}
