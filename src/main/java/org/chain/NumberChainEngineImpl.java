package org.chain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

/**
 * Provides all the logic that is used to operate on a {@link Collection} of {@link Number}s.
 * @param <T> The type of {@link Number} that is contained in the {@link Collection} to be operated on.
 */
class NumberChainEngineImpl<T extends Number & Comparable<T>> extends ChainEngineImpl<T> implements NumberChainEngine<T> {
    /**
     * <br/>{ [ 2, 3, 4 ] } => { 2 }
     * @param collection The collection to operate on.
     * @return The minimum value of all the {@link Number}s in the {@link Collection}.
     */
    @Override
    public T min(Collection<T> collection) {
        return new ArrayList<T>(sort(collection, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        })).get(0);
    }

    /**
     * <br/>{ [ 2, 3, 4 ] } => { 4 }
     * @param collection The collection to operate on.
     * @return The maximum value of all the {@link Number}s in the {@link Collection}.
     */
    @Override
    public T max(Collection<T> collection) {
        return new ArrayList<T>(sort(collection, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o2.compareTo(o1);
            }
        })).get(0);
    }

    /**
     * <br/>{ [ 2, 3, 4 ] } => { 9 }
     * @param collection The collection to operate on.
     * @return The sum of all the {@link Number}s in the {@link Collection}.
     */
    @Override
    public BigDecimal sum(Collection<T> collection) {
        BigDecimal sum = new BigDecimal(0D);
        for (T t : collection) {
            sum = sum.add(new BigDecimal(t.toString()));
        }
        return sum;
    }

    /**
     * <br/>{ [ 2, 3, 4 ] } => { 3 }
     * @param collection The collection to operate on.
     * @return The average of all the {@link Number}s in the {@link Collection}.
     */
    @Override
    public BigDecimal average(Collection<T> collection) {
        return sum(collection).divide(new BigDecimal(collection.size()));
    }
}
