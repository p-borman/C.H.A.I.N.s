package org.chain.filters;

public interface Selector<T,TD>{
    TD select(final T obj);
}
