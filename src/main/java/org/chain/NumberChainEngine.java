package org.chain;

import java.math.BigDecimal;
import java.util.Collection;

interface NumberChainEngine<T extends Number & Comparable> extends ChainEngine<T>
{
    //access
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 2 }
     * @param collection The collection to operate on.
     * @return The minimum value of all the {@link Number}s in the {@link Collection}.
     */
    T min(final Collection<T> collection);
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 4 }
     * @param collection The collection to operate on.
     * @return The maximum value of all the {@link Number}s in the {@link Collection}.
     */
    T max(final Collection<T> collection);
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 9 }
     * @param collection The collection to operate on.
     * @return The sum of all the {@link Number}s in the {@link Collection}.
     */
    BigDecimal sum(final Collection<T> collection);
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 3 }
     * @param collection The collection to operate on.
     * @return The average of all the {@link Number}s in the {@link Collection}.
     */
    BigDecimal average(final Collection<T> collection);
}
