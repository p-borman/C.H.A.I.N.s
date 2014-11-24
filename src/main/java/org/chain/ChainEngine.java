package org.chain;

import org.chain.filters.Action;
import org.chain.filters.ManySelector;
import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.NoSuchElementException;

interface ChainEngine<T>
{
    //mod
    void each(final Collection<T> collection, final Action<T> action);
    Collection<T> sort(final Collection<T> collection, final Comparator<T> comparator);
    Collection<T> reverse(final Collection<T> collection);
    Collection<T> concatenate(final Collection<T> collection1, final Collection<T> collection2);
    Collection<T> union(final Collection<T> collection1, final Collection<T> collection2, final Comparator<T> comparator);
    Collection<T> intersect(final Collection<T> collection1, final Collection<T> collection2, final Comparator<T> comparator);
    Collection<T> diverge(Collection<T> collection1, Collection<T> collection2, Comparator<T> comparator);
    Collection<T> distinct(final Collection<T> collection, final Comparator<T> comparator);

    //query
    <TD> Collection<TD> select(final Collection<T> collection, final Selector<T, TD> selector);
    <TD> Collection<TD> selectMany(final Collection<T> collection, final ManySelector<T, TD> selector);
    Collection<T> where(final Collection<T> collection, final WhereComparator<T> comparator);
    Boolean any(final Collection<T> collection, final WhereComparator<T> comparator);
    Boolean any(final Collection<T> collection);
    Boolean none(final Collection<T> collection, final WhereComparator<T> comparator);
    Boolean none(final Collection<T> collection);
    Integer count(final Collection<T> collection, final WhereComparator<T> comparator);
    Integer count(final Collection<T> collection);

    //access
    T first(final Collection<T> collection);
    T firstOrNull(final Collection<T> collection);
    T first(final Collection<T> collection, final WhereComparator<T> comparator) throws NoSuchElementException;
    T firstOrNull(final Collection<T> collection, final WhereComparator<T> comparator);
    T last(final Collection<T> collection);
    T lastOrNull(final Collection<T> collection);
    T last(final Collection<T> collection, final WhereComparator<T> comparator) throws NoSuchElementException;
    T lastOrNull(final Collection<T> collection, final WhereComparator<T> comparator);
    T at(final Collection<T> collection, final int index);
    Collection<T> skip(final Collection<T> collection, final int numberToSkip);
    Collection<T> take(final Collection<T> collection, final int numberToTake);
}
