package org.chain;

import java.util.Collection;

/**
 * Provides a fluent API to operate on a any kind of collection.
 * @param <T> The type found in the {@link Collection} in the chain.
 */
public final class Chain<T> extends ChainBuilderBase<T,ChainEngine<T>> {

    /**
     * @param collection The {@link Collection} that actions or modifications will be made on.
     */
    public Chain(Collection<T> collection){
        super(new ChainEngineImpl<T>(), collection);
    }
}
