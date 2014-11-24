package org.chain.filters;

import java.util.Collection;

public interface ManySelector<T,TD>{
    Collection<TD> select(final T obj);
}
