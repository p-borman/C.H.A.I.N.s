package org.chain;

import org.chain.filters.Action;
import org.chain.filters.ManySelector;
import org.chain.filters.Selector;
import org.chain.filters.WhereComparator;

import java.util.*;

/**
 * Provides all the logic that is used to operate on a {@link Collection}.
 * @param <T> The type that is contained in the {@link Collection} to be operated on.
 */
class ChainEngineImpl<T> implements ChainEngine<T> {

    ChainEngineImpl(){}

    /**
     * Performs an action using each element in the {@link Collection}.
     * @param collection The collection to operate on.
     * @param action The action or modification to perform on each element.
     */
    @Override
    public void each(final Collection<T> collection, final Action<T> action) {
        for (T t : collection) {
            action.perform(t);
        }
    }

    /**
     * Sorts the {@link Collection} based on the {@link Comparator} that is passed in.
     * <br/>{ [ 3, 1, 4, 2 ] } => { [ 1, 2, 3, 4 ] }
     * @param collection The collection to operate on.
     * @param comparator Defines the order to sort the list by.
     * @return A new sorted version of the {@link Collection}.
     */
    @Override
    public Collection<T> sort(Collection<T> collection, Comparator<T> comparator) {
        ArrayList<T> ts = new ArrayList<T>(collection);
        Collections.sort(ts, comparator);
        return ts;
    }

    /**
     * Reverses the order of the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } => { [ 4, 3, 2, 1 ] }
     * @param collection The collection to operate on.
     * @return A new reversed order version of the {@link Collection}.
     */
    @Override
    public Collection<T> reverse(Collection<T> collection) {
        ArrayList<T> ts = new ArrayList<T>(collection);
        Collections.reverse(ts);
        return ts;
    }

    /**
     * Appends a {@link Collection} to the end of the contained {@link Collection}.
     * <br/>{ [ 1, 2 ] } concatenate { [ 3, 4 ] } => { [ 1, 2, 3, 4 ] }
     * @param collection1 The collection to operate on.
     * @param collection2 The {@link Collection} to append to the end of the contained {@link Collection}.
     * @return A new {@link Collection} that is a concatenated version of the contained {@link Collection} and the {@link Collection} passed in.
     */
    @Override
    public Collection<T> concatenate(Collection<T> collection1, Collection<T> collection2) {
        ArrayList<T> ts = new ArrayList<T>(collection1);
        ts.addAll(collection2);
        return ts;
    }

    /**
     * Builds a new {@link Collection} that contains all distinct elements from both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } union { [ 2, 3, 4 ] } => { [ 1, 2, 3, 4 ] }
     * @param collection1 The collection to operate on.
     * @param collection2 The {@link Collection} to union with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is distinct.
     * @return A new {@link Collection} containing all distinct elements from both {@link Collection}s.
     */
    @Override
    public Collection<T> union(final Collection<T> collection1, final Collection<T> collection2, final Comparator<T> comparator) {
        return distinct(concatenate(collection1, collection2), comparator);
    }

    /**
     * Builds a new {@link Collection} that contains all elements found in both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } intersect { [ 2, 3, 4 ] } => { [ 2, 3 ] }
     * @param collection1 The collection to operate on.
     * @param collection2 The {@link Collection} to intersect with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is found in both {@link Collection}s.
     * @return A new {@link Collection} containing all elements found in both {@link Collection}s.
     */
    @Override
    public Collection<T> intersect(final Collection<T> collection1, final Collection<T> collection2, final Comparator<T> comparator) {
        final ArrayList<T> ts = new ArrayList<T>();

        each(collection1, new Action<T>()
        {
            @Override
            public void perform(final T t1)
            {
                Boolean any = any(collection2, new WhereComparator<T>()
                {
                    @Override
                    public boolean meetsCondition(final T t2)
                    {
                        return comparator.compare(t1, t2) == 0;
                    }
                });
                if (any)
                {
                    ts.add(t1);
                }
            }
        });

        return ts;
    }

    /**
     * Builds a new {@link Collection} that contains all elements not found in both {@link Collection}s.
     * <br/>{ [ 1, 2, 3 ] } diverge { [ 2, 3, 4 ] } => { [ 1, 4 ] }
     * @param collection1 The collection to operate on.
     * @param collection2 The {@link Collection} to diverge with the contained {@link Collection}.
     * @param comparator Defines how to check if an element is found in both {@link Collection}s.
     * @return A new {@link Collection} containing all elements not found in both {@link Collection}s.
     */
    @Override
    public Collection<T> diverge(final Collection<T> collection1, final Collection<T> collection2, final Comparator<T> comparator) {
        final ArrayList<T> ts = new ArrayList<T>();

        each(collection1, new Action<T>()
        {
            @Override
            public void perform(final T t1)
            {
                Boolean none = none(collection2, new WhereComparator<T>()
                {
                    @Override
                    public boolean meetsCondition(final T t2)
                    {
                        return comparator.compare(t1, t2) == 0;
                    }
                });
                if (none)
                {
                    ts.add(t1);
                }
            }
        });
        each(collection2,new Action<T>() {
            @Override
            public void perform(final T t2) {
                Boolean none = none(collection1, new WhereComparator<T>()
                {
                    @Override
                    public boolean meetsCondition(final T t1)
                    {
                        return comparator.compare(t1, t2) == 0;
                    }
                });
                if (none){
                    ts.add(t2);
                }
            }
        });

        return ts;
    }

    /**
     * Removes all elements found in the {@link Collection} already.
     * <br/>{ [ 1, 1, 2, 2, 3 ] } => { [ 1, 2, 3 ] }
     * @param collection The collection to operate on.
     * @param comparator Defines how to check if an element is found in the {@link Collection} already.
     * @return A new {@link Collection} containing all elements not found in the {@link Collection} already.
     */
    @Override
    public Collection<T> distinct(final Collection<T> collection, final Comparator<T> comparator) {
        final ArrayList<T> ts = new ArrayList<T>();

        each(collection, new Action<T>() {
            @Override
            public void perform(final T t1) {
                Boolean none = none(ts, new WhereComparator<T>()
                {
                    @Override
                    public boolean meetsCondition(final T t2)
                    {
                        return comparator.compare(t1, t2) == 0;
                    }
                });
                if (none){
                    ts.add(t1);
                }
            }
        });

        return ts;
    }

    /**
     * Selects a field from each element and concatenates them all into a new {@link Collection}.
     * <br/>{ [ {a:1}, {a:2}, {a:3}, {a:4} ]  } select(a) => { [ 1, 2, 3, 4 ] }
     * @param collection The collection to operate on.
     * @param selector Defines which field to select from each element.
     * @param <TD> The type of the field to be selected from each element.
     * @return A new {@link Collection} containing all of the fields that were selected from each element.
     */
    @Override
    public <TD> Collection<TD> select(final Collection<T> collection, final Selector<T, TD> selector) {
        final ArrayList<TD> tds = new ArrayList<TD>();

        each(collection,new Action<T>() {
            public void perform(final T obj) {
                tds.add(selector.select(obj));
            }
        });

        return tds;
    }

    /**
     * Selects a field containing a {@link Collection} from each element and concatenates all subelements into a new {@link Collection}.
     * <br/>{ [ {a:[1,  2]}, {a:[3, 4]} ] } select(a) => { [ 1, 2, 3, 4 ] }
     * @param collection The collection to operate on.
     * @param selector Defines which field to select from each element.
     * @param <TD> The type of the field to be selected from each element.
     * @return A new {@link Collection} containing all of the fields that were selected from each element.
     */
    @Override
    public <TD> Collection<TD> selectMany(final Collection<T> collection, final ManySelector<T, TD> selector) {
        final ArrayList<TD> tds = new ArrayList<TD>();

        each(collection, new Action<T>() {
            public void perform(T obj) {
                final Collection<TD> select = selector.select(obj);
                for (TD td : select) {
                    tds.add(td);
                }
            }
        });

        return tds;
    }

    /**
     * Selects all elements that meet the condition specified into a new {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } where(a >= 2) => { [ 2, 3, 4 ] }
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for each element.
     * @return A new {@link Collection} containing all elements that meet the condition specified.
     */
    @Override
    public Collection<T> where(final Collection<T> collection, final WhereComparator<T> comparator) {
        final ArrayList<T> ts = new ArrayList<T>();

        each(collection, new Action<T>() {
            @Override
            public void perform(final T obj) {
                if (comparator.meetsCondition(obj)){
                    ts.add(obj);
                }
            }
        });

        return ts;
    }

    /**
     * Checks if the {@link Collection} is either null or empty.
     * <br/>{ [ 1, 2, 3, 4 ] } isNullOrEmpty => { false }
     * <br/>{ [ ] } isNullOrEmpty => { true }
     * <br/>{ } isNullOrEmpty => { true }
     * @param collection The collection to operate on.
     * @return True if the {@link Collection} is null or contains zero elements.
     */
    @Override
    public Boolean isNullOrEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Checks if any element meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } any(a >= 2) => { true }
     * <br/>{ [ 1, 2, 3, 4 ] } any(a >= 200) => { false }
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for each element.
     * @return True if any element meet the condition specified in the {@link Collection}.
     */
    @Override
    public Boolean any(final Collection<T> collection, final WhereComparator<T> comparator) {
        for (final T t : collection) {
            if (comparator.meetsCondition(t)){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } any => { true }
     * <br/>{ [ ] } any => { false }
     * @param collection The collection to operate on.
     * @return True if any elements are in the {@link Collection}.
     */
    @Override
    public Boolean any(final Collection<T> collection) {
        return collection.size() > 0;
    }

    /**
     * Checks if no elements meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } none(a >= 2) => { false }
     * <br/>{ [ 1, 2, 3, 4 ] } none(a >= 200) => { true }
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for each element.
     * @return True if no elements meet the condition specified in the {@link Collection}.
     */
    @Override
    public Boolean none(final Collection<T> collection, final WhereComparator<T> comparator) {
        return !any(collection, comparator);
    }

    /**
     * Checks if any elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } none => { false }
     * <br/>{ [ ] } none => { true }
     * @param collection The collection to operate on.
     * @return True if no elements are in the {@link Collection}.
     */
    @Override
    public Boolean none(final Collection<T> collection) {
        return !any(collection);
    }

    /**
     * Counts how many elements meet the condition specified in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } count(a >= 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } count(a >= 200) => { 0 }
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for each element.
     * @return How many elements meet the condition specified in the {@link Collection}.
     */
    @Override
    public Integer count(final Collection<T> collection, final WhereComparator<T> comparator) {
        Integer count = 0;

        for (final T t : collection) {
            if (comparator.meetsCondition(t)){
                count++;
            }
        }

        return count;
    }

    /**
     * Counts how many elements are in the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } count => { 4 }
     * @param collection The collection to operate on.
     * @return How many elements are in the {@link Collection}.
     */
    @Override
    public Integer count(final Collection<T> collection) {
        return collection.size();
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } first => { 1 }
     * <br/>{ [ ] } first => IndexOutOfBoundsException
     * @param collection The collection to operate on.
     * @return The first element from the collection.
     * @throws IndexOutOfBoundsException
     */
    @Override
    public T first(final Collection<T> collection) throws IndexOutOfBoundsException{
        return new ArrayList<T>(collection).get(0);
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull => { 1 }
     * <br/>{ [ ] } firstOrNull => { null }
     * @param collection The collection to operate on.
     * @return The first element of the {@link Collection} or null if the {@link Collection} is empty.
     */
    @Override
    public T firstOrNull(Collection<T> collection)
    {
        try
        {
            return first(collection);
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * Selects {@link Collection} for the first element that meets the condition.
     * <br/>{ [ 1, 2, 3, 4 ] } first(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } first(a > 200) => NoSuchElementException
     * <br/>{ [ ] } first(a > 2) => NoSuchElementException
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The first element that meets the condition specified.
     * @throws NoSuchElementException If no matching element found.
     */
    @Override
    public T first(Collection<T> collection, WhereComparator<T> comparator) throws NoSuchElementException
    {
        for (T t : collection)
        {
        if (comparator.meetsCondition(t)){
                return t;
            }
        }
        throw new NoSuchElementException("No element matching given comparator was found in the collection.");
    }

    /**
     * Selects {@link Collection} for the first element that meets the condition, and returns null if no match found.
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } firstOrNull(a > 200) => { null }
     * <br/>{ [ ] } firstOrNull(a > 2) => { null }
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The first element that meets the condition specified, or null if no match found.
     */
    @Override
    public T firstOrNull(Collection<T> collection, WhereComparator<T> comparator)
    {
        try
        {
            return first(collection, comparator);
        }
        catch (NoSuchElementException e)
        {
            return null;
        }
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } last => { 4 }
     * <br/>{ [ ] } last => IndexOutOfBoundsException
     * @param collection The collection to operate on.
     * @return The last element from the collection.
     */
    @Override
    public T last(final Collection<T> collection) throws IndexOutOfBoundsException
    {
        return new ArrayList<T>(collection).get(collection.size() - 1);
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull => { 4 }
     * <br/>{ [ ] } lastOrNull => { null }
     * @param collection The collection to operate on.
     * @return The last element of the {@link Collection} or null if the {@link Collection} is empty.
     */
    @Override
    public T lastOrNull(Collection<T> collection)
    {
        try
        {
            return last(collection);
        }
        catch (IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    /**
     * Selects {@link Collection} for the last element that meets the condition.
     * <br/>{ [ 1, 2, 3, 4 ] } last(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } last(a > 200) => NoSuchElementException
     * <br/>{ [ ] } last(a > 2) => NoSuchElementException
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The last element that meets the condition specified.
     * @throws NoSuchElementException If no matching element found.
     */
    @Override
    public T last(Collection<T> collection, WhereComparator<T> comparator) throws NoSuchElementException
    {
        T result = null;
        for (T t : collection)
        {
            if (comparator.meetsCondition(t)){
                result =  t;
            }
        }
        if (result == null)
        {
            throw new NoSuchElementException("No element matching given comparator was found in the collection.");
        }
        return result;
    }

    /**
     * Selects {@link Collection} for the first element that meets the condition, and returns null if no match found.
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull(a > 2) => { 3 }
     * <br/>{ [ 1, 2, 3, 4 ] } lastOrNull(a > 200) => { null }
     * <br/>{ [ ] } lastOrNull(a > 2) => { null }
     * @param collection The collection to operate on.
     * @param comparator Defines the condition to be met for the element to be found.
     * @return The last element that meets the condition specified, or null if no match found.
     */
    @Override
    public T lastOrNull(Collection<T> collection, WhereComparator<T> comparator)
    {
        try
        {
            return last(collection, comparator);
        }
        catch (NoSuchElementException e)
        {
            return null;
        }
    }

    /**
     * <br/>{ [ 1, 2, 3, 4 ] } at(2) => { 3 }
     * @param collection The collection to operate on.
     * @param index
     * @return The element from the collection at the given index.
     */
    @Override
    public T at(final Collection<T> collection, final int index) {
        return new ArrayList<T>(collection).get(index);
    }

    /**
     * Skips the given number of elements in the {@link Collection} and returns the rest in a new {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } skip(2) => { [ 3, 4 ] }
     * @param collection The collection to operate on.
     * @return A new {@link Collection} containing all elements with the specified number removed from the front of the array.
     */
    @Override
    public Collection<T> skip(Collection<T> collection, int numberToSkip) {
        return new ArrayList<T>(collection).subList(numberToSkip, collection.size());
    }

    /**
     * Takes the given number of elements from the front of the {@link Collection}.
     * <br/>{ [ 1, 2, 3, 4 ] } take(2) => { [ 1, 2 ] }
     * @param collection The collection to operate on.
     * @return A new {@link Collection} containing the given number of elements from the front of the {@link Collection}.
     */
    @Override
    public Collection<T> take(Collection<T> collection, int numberToTake) {
        return new ArrayList<T>(collection).subList(0, numberToTake);
    }
}
