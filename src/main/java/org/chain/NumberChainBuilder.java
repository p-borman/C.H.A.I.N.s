package org.chain;

import java.math.BigDecimal;

public interface NumberChainBuilder<T extends Number & Comparable> extends ChainBuilder<T>
{
    //access
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 2 }
     * @return The lowest valued {@link Number} in the collection.
     */
    T min();
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 4 }
     * @return The highest valued {@link Number} in the collection.
     */
    T max();
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 9 }
     * @return The sum of all the {@link Number} in the collection.
     */
    BigDecimal sum();
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 3 }
     * @return The average for all the numbers in the collection.
     */
    BigDecimal average();
}
