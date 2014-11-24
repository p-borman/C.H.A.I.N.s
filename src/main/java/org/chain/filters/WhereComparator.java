package org.chain.filters;

public interface WhereComparator<T>{
    boolean meetsCondition(final T obj);
}
