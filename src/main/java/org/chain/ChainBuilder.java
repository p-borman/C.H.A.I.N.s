package org.chain;

import org.chain.filters.Action;
import org.chain.filters.ManySelector;
import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public interface ChainBuilder<T> {
    List<T> toList();

    //mod
    void each(final Action<T> action);
    ChainBuilder<T> sort(final Comparator<T> comparator);
    ChainBuilder<T> reverse();
    ChainBuilder<T> concatenate(Collection<T> collection2);
    ChainBuilder<T> union(Collection<T> collection2, final Comparator<T> comparator);
    ChainBuilder<T> intersect(Collection<T> collection2, final Comparator<T> comparator);
    ChainBuilder<T> diverge(Collection<T> collection2, Comparator<T> comparator);
    ChainBuilder<T> distinct(final Comparator<T> comparator);

    //query
    <TD> ChainBuilder<TD> select(final Selector<T, TD> selector);
    <TD> ChainBuilder<TD> selectMany(final ManySelector<T, TD> selector);
    ChainBuilder<T> where(final WhereComparator<T> comparator);
    Boolean any(final WhereComparator<T> comparator);
    Boolean any();
    Boolean none(final WhereComparator<T> comparator);
    Boolean none();
    Integer count(final WhereComparator<T> comparator);
    Integer count();

    //access
    T first();
    T firstOrNull();
    T first(final WhereComparator<T> comparator) throws NoSuchElementException;
    T firstOrNull(final WhereComparator<T> comparator);
    T last();
    T lastOrNull();
    T last(final WhereComparator<T> comparator) throws NoSuchElementException;
    T lastOrNull(final WhereComparator<T> comparator);
    T at(final int index);
    ChainBuilder<T> skip(final int numberToSkip);
    ChainBuilder<T> take(final int numberToTake);
}
