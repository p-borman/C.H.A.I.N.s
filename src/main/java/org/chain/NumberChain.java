package org.chain;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Provides a fluent API to operate on a collection of {@link Number}s
 * @param <T> The kind of {@link Number}s that are contained in the collection.
 */
public final class NumberChain<T extends Number & Comparable<T>> extends ChainBuilderBase<T,NumberChainEngine<T>> implements NumberChainBuilder<T>
{
    /**
     *
     * @param collection The {@link Collection} that actions or modifications will be made on.
     */
    public NumberChain(Collection<T> collection){
        super(new NumberChainEngineImpl<T>(), collection);
    }

    /**
     * <br/>{ [ 2, 3, 4 ] } => { 2 }
     * @return The lowest valued {@link Number} in the collection.
     */
    @Override
    public T min() { return engine.min(collection); }

    /**
     * <br/>{ [ 2, 3, 4 ] } => { 4 }
     * @return The highest valued {@link Number} in the collection.
     */
    @Override
    public T max() { return engine.max(collection); }

    /**
     * <br/>{ [ 2, 3, 4 ] } => { 9 }
     * @return The sum of all the {@link Number} in the collection.
     */
    @Override
    public BigDecimal sum() {
        return engine.sum(collection);
    }

    /**
     * <br/>{ [ 2, 3, 4 ] } => { 3 }
     * @return The average for all the numbers in the collection.
     */
    @Override
    public BigDecimal average() {
        return engine.average(collection);
    }
}
